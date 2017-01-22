package cn.ocoop.shiro.authc.realm;

import cn.ocoop.shiro.HttpBasicToken;
import cn.ocoop.shiro.authc.realm.resolves.delegate.HttpBasicSubjectAware;
import cn.ocoop.shiro.authz.SimpleAuthorizationInfoOrdered;
import cn.ocoop.shiro.subject.BasicHttpAuthcUser;
import cn.ocoop.spring.App;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by liolay on 2016/12/6.
 */
public class HttpBasicAuthorizingRealm extends AbstractAuthorizingRealm {
    private static final Logger log = LoggerFactory.getLogger(HttpBasicAuthorizingRealm.class);
    private static final Map<String, AuthenticationInfo> usernameRefAuthenticationInfo = new HashMap<>();
    private static final Map<String, AuthorizationInfo> usernameRefAuthorizationInfo = new HashMap<>();
    private static final Collection<BasicHttpAuthcUser> users = getUsers();

    {
        users.stream().forEach(basicHttpAuthcUser -> {
            usernameRefAuthenticationInfo.put(
                    basicHttpAuthcUser.getUser().getUsername(),
                    new SimpleAuthenticationInfo(
                            basicHttpAuthcUser.getUser().getUsername(), //用户名
                            basicHttpAuthcUser.getUser().getPassword(), //密码
                            new SimpleByteSource(basicHttpAuthcUser.getUser().getSalt()),
                            getName()  //realm name
                    )
            );
            SimpleAuthorizationInfoOrdered authorizationInfo = new SimpleAuthorizationInfoOrdered();
            authorizationInfo.setStringPermissions(basicHttpAuthcUser.getPerms());
            usernameRefAuthorizationInfo.put(basicHttpAuthcUser.getUser().getUsername(), authorizationInfo);
        });
    }

    private static Collection<BasicHttpAuthcUser> getUsers() {
        Collection<BasicHttpAuthcUser> users = null;
        try {
            users = App.getBean(HttpBasicSubjectAware.class).getUsers();
        } catch (NoSuchBeanDefinitionException e) {
            log.warn("HttpBasicSubjectAware未配置");
        }
        if (users == null) users = new HashSet<>();
        return users;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return super.supports(token) && HttpBasicToken.class.isAssignableFrom(token.getClass());
    }

    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return usernameRefAuthorizationInfo.get(principals.getPrimaryPrincipal());
    }


    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return usernameRefAuthenticationInfo.get(token.getPrincipal());
    }
}
