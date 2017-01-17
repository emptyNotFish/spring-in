package cn.ocoop.shiro.authc.realm;

import cn.ocoop.shiro.authc.TokenExtendFace;
import cn.ocoop.shiro.authc.realm.resolves.SubjectResolve;
import cn.ocoop.shiro.authz.SimpleAuthorizationInfoOrdered;
import cn.ocoop.shiro.subject.User;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;
import org.apache.shiro.util.StringUtils;
import org.springframework.util.Assert;

/**
 * Created by liolay on 2016/12/6.
 */
public abstract class AbstractAuthorizingRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return super.supports(token) && TokenExtendFace.class.isAssignableFrom(token.getClass());
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
        return super.getAuthorizationInfo(principals);
    }

    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Assert.isTrue(getSubjectResolve()!=null , "配置错误,子类请先重写getSubjectResolve");
        String username = (String) principals.getPrimaryPrincipal();

        SimpleAuthorizationInfoOrdered authorizationInfo = new SimpleAuthorizationInfoOrdered();
        authorizationInfo.setRoles(getSubjectResolve().findRoles(username));
        authorizationInfo.setStringPermissions(getSubjectResolve().findPermissions(username));

        return authorizationInfo;
    }

    protected SubjectResolve getSubjectResolve() {
        return null;
    }

    private Cache<Object, AuthenticationInfo> getAvailableAuthenticationCache() {
        Cache<Object, AuthenticationInfo> cache = getAuthenticationCache();
        boolean authcCachingEnabled = isAuthenticationCachingEnabled();
        if (cache == null && authcCachingEnabled) {
            cache = getAuthenticationCacheLazy();
        }
        return cache;
    }

    private Cache<Object, AuthenticationInfo> getAuthenticationCacheLazy() {

        if (this.getAuthenticationCache() == null) {

            CacheManager cacheManager = getCacheManager();

            if (cacheManager != null) {
                String cacheName = getAuthenticationCacheName();
                this.setAuthenticationCache(cacheManager.<Object, AuthenticationInfo>getCache(cacheName));
            }
        }

        return this.getAuthenticationCache();
    }

    public void clearAnyCachedAuthenticationInfo(String primaryPrincipals) {
        if (StringUtils.hasText(primaryPrincipals)) {
            Cache<Object, AuthenticationInfo> cache = getAvailableAuthenticationCache();
            //cache instance will be non-null if caching is enabled:
            if (cache != null) {
                cache.remove(primaryPrincipals);
            }
        }
    }

    protected SimpleAuthenticationInfo getSimpleAuthenticationInfo(User user) {
        return new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                new SimpleByteSource(user.getSalt()),
                getName()  //realm name
        );
    }


}
