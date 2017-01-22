package cn.ocoop.shiro.filter;

import cn.ocoop.shiro.AutoDetectedToken;
import cn.ocoop.shiro.authc.resolvers.AutoDetectedIdentifyAuthenticationResolver;
import cn.ocoop.shiro.cache.ShiroRealmCacheManager;
import cn.ocoop.shiro.utils.RequestUtil;
import cn.ocoop.spring.App;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.env.Environment;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class AutoAuthenticationFilter extends AdviceFilter {
    public static final String WX_CODE = "code";
    public static final String OPEN_ID = "autoDetectIdentifyFilter-openId";
    private static final Logger log = LoggerFactory.getLogger(AutoAuthenticationFilter.class);

    public boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        try {
            HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
            Environment environment = App.getBean(Environment.class);

            if (RequestUtil.isAjaxRequest(httpServletRequest))
                return super.preHandle(request, response);

            if (isValidWxRedirectURI(request, environment)) {
                return super.preHandle(request, response);
            }
            String openId = getOpenId(request);
            if (StringUtils.isBlank(openId)) return super.preHandle(request, response);

            getSubject().getSession().setAttribute(OPEN_ID, openId);
            log.info("开始尝试通过openId:{}自动登录,请求的url:{}", openId, WebUtils.getRequestUri(httpServletRequest));
            if (getSubject().getPrincipal() != null) {
                log.info("当前账户已经登录,本次通过微信访问,尝试清除已有权限缓存...");
                ShiroRealmCacheManager.clearCachedAuthorizationInfo(getSubject().getPrincipals());
                return super.preHandle(request, response);
            }

            AuthenticationToken token = new AutoDetectedToken(UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    false,
                    request.getRemoteHost(),
                    openId
            );
            App.getBean(AutoDetectedIdentifyAuthenticationResolver.class).login(request, response, token);
            return super.preHandle(request, response);
        } catch (Exception e) {
            log.error("auto detected identify is fail", e);
        }
        return super.preHandle(request, response);
    }

    private String getOpenId(ServletRequest request) {
        log.info("request from WeiXin:code:{}", getWeiXinCode(request));
        String openId = null;
        try {
            openId = App.getBean(WeiXinAware.class).getOpenId(getWeiXinCode(request));
        } catch (NoSuchBeanDefinitionException e) {
            log.warn("WeiXinAware未配置");
        }
        return openId;
    }

    private String getWeiXinCode(ServletRequest request) {
        return WebUtils.getCleanParam(request, WX_CODE);
    }

    private boolean isValidWxRedirectURI(ServletRequest request, Environment environment) {
        return environment.getProperty("shiro.filter.weixin.identifyParam", "state").equals(WebUtils.getCleanParam(request, "state"))
                && StringUtils.isNotBlank(getWeiXinCode(request));
    }

    private Subject getSubject() {
        return SecurityUtils.getSubject();
    }

}
