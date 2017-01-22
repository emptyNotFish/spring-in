package cn.ocoop.shiro.authc.resolvers;

import cn.ocoop.shiro.authc.realm.resolves.MobileCaptchaSubjectResolve;
import cn.ocoop.shiro.cache.ShiroRealmCacheManager;
import cn.ocoop.shiro.filter.AjaxAuthenticationFilter;
import cn.ocoop.shiro.utils.SubjectUtil;
import cn.ocoop.shiro.vo.Result;
import cn.ocoop.spring.App;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static com.alibaba.fastjson.JSON.toJSONString;

public class UnionAuthenticationResolver  extends AbstractAuthenticationResolver{
    private static final Logger logger = LoggerFactory.getLogger(UnionAuthenticationResolver.class);
    private static final String UNION_LOGIN_PREFIX = "shiro:authentication-key:";

    protected Result onLoginSuccess(AuthenticationToken token, Subject subject,
                                    ServletRequest request, ServletResponse response) {
        logger.info("联合登录成功:{}", toJSONString(token));
        Session session = subject.getSession(false);
        if (session == null) return Result.build("400", "用户名验证成功,由于系统原因未正确创建登录所需信息,请联系API提供者", null);

        saveSidState(session);

        Result result = Result.build("200", "登录成功", session.getId().toString());
        onLoginSuccess(token);
        return result;
    }

    private void saveSidState(Session session) {
        App.getBean(RedisTemplate.class).opsForValue().set(getSidStateKey(session.getId()), null, 5, TimeUnit.MINUTES);
    }

    private static String getSidStateKey(Serializable sid) {
        return UNION_LOGIN_PREFIX + sid;
    }


    @Override
    protected void onLoginSuccess(AuthenticationToken token) {
        MobileCaptchaSubjectResolve resolve = App.getBean(MobileCaptchaSubjectResolve.class);
        Object userInfo = resolve.findLoginUserInfo((String) SecurityUtils.getSubject().getPrincipal());
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(AjaxAuthenticationFilter.USER_INFO_KEY, userInfo);
        ShiroRealmCacheManager.clearCachedAuthorizationInfo(SubjectUtil.getPrincipals());
    }

    public static boolean isValidSid(String sid) {
        boolean hasValidSid = App.getBean(RedisTemplate.class).hasKey(getSidStateKey(sid));
        App.getBean(RedisTemplate.class).delete(getSidStateKey(sid));
        return hasValidSid;
    }

    public static void useAndInvalidSid(HttpServletRequest request, HttpServletResponse response, String sid) {
        if (!isValidSid(sid)) return;

        DefaultWebSessionManager defaultWebSessionManager = (DefaultWebSessionManager) App.getBean(WebSessionManager.class);
        Cookie template = defaultWebSessionManager.getSessionIdCookie();
        Cookie cookie = new SimpleCookie(template);
        cookie.setValue(sid);
        cookie.saveTo(request, response);
    }
}
