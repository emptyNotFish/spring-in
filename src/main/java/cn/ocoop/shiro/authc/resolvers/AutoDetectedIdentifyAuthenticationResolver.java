package cn.ocoop.shiro.authc.resolvers;

import cn.ocoop.shiro.authc.realm.resolves.AutoDetectedSubjectResolve;
import cn.ocoop.shiro.cache.ShiroRealmCacheManager;
import cn.ocoop.shiro.filter.AjaxAuthenticationFilter;
import cn.ocoop.shiro.spring.AppContextShiro;
import cn.ocoop.shiro.utils.SubjectUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;

@Service
public class AutoDetectedIdentifyAuthenticationResolver extends AbstractAuthenticationResolver {
    @Override
    protected void onLoginSuccess(AuthenticationToken token) {

        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(
                AjaxAuthenticationFilter.USER_INFO_KEY,
                AppContextShiro.getBean(AutoDetectedSubjectResolve.class).findLoginUserInfo((String) SecurityUtils.getSubject().getPrincipal())
        );
        ShiroRealmCacheManager.clearCachedAuthorizationInfo(SubjectUtil.getPrincipals());
    }
}
