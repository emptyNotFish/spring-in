package cn.ocoop.shiro.authc.realm;

import cn.ocoop.shiro.UsernamePasswordToken;
import cn.ocoop.shiro.authc.exception.UnActivedAccountException;
import cn.ocoop.shiro.authc.realm.resolves.SubjectResolve;
import cn.ocoop.shiro.authc.realm.resolves.UsernamePasswordSubjectResolve;
import cn.ocoop.shiro.subject.User;
import cn.ocoop.spring.App;
import org.apache.shiro.authc.*;
import org.apache.shiro.util.SimpleByteSource;

/**
 * Created by liolay on 2016/12/6.
 */
public class UsernamePasswordRealm extends AbstractAuthorizingRealm {
    @Override
    public boolean supports(AuthenticationToken token) {
        return super.supports(token) && UsernamePasswordToken.class.isAssignableFrom(token.getClass());
    }

    @Override
    protected SubjectResolve getSubjectResolve() {
        return App.getBean(UsernamePasswordSubjectResolve.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        User user = getSubjectResolve().findLoginUser(token);
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        if (user == null) {//没找到帐号
            AuthenticationException authenticationException = new UnknownAccountException();
            usernamePasswordToken.setAuthenticationException(authenticationException);
            throw authenticationException;
        }

        if ("lock".equals(user.getLocked())) {//已锁定
            AuthenticationException authenticationException = new LockedAccountException();
            usernamePasswordToken.setAuthenticationException(authenticationException);
            throw authenticationException;
        }

        if ("2".equals(user.getLocked())) {//未激活
            AuthenticationException authenticationException = new UnActivedAccountException();
            usernamePasswordToken.setAuthenticationException(authenticationException);
            throw authenticationException;
        }

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                new SimpleByteSource(user.getSalt()),
                getName()  //realm name
        );
        return authenticationInfo;
    }


}
