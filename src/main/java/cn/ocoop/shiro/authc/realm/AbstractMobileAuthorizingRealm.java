package cn.ocoop.shiro.authc.realm;

import cn.ocoop.shiro.authc.realm.resolves.MobileCaptchaSubjectResolve;
import cn.ocoop.shiro.authc.realm.resolves.SubjectResolve;
import cn.ocoop.shiro.subject.User;
import cn.ocoop.spring.App;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;

/**
 * Created by liolay on 2016/12/6.
 */
public abstract class AbstractMobileAuthorizingRealm extends AbstractAuthorizingRealm {

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        User user = getSubjectResolve().findLoginUser(token);
        if (user == null) throw new UnknownAccountException();//没找到帐号
        return getSimpleAuthenticationInfo(user);
    }

    protected SubjectResolve getSubjectResolve() {
        return App.getBean(MobileCaptchaSubjectResolve.class);
    }


}
