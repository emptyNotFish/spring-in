package cn.ocoop.shiro.authc.realm;

import cn.ocoop.shiro.MobileCaptchaToken;
import cn.ocoop.shiro.authc.IncorrectCaptchaException;
import cn.ocoop.shiro.authc.realm.resolves.MobileCaptchaSubjectResolve;
import cn.ocoop.shiro.authc.realm.resolves.SubjectResolve;
import cn.ocoop.shiro.sms.AuthcSmsCaptchaService;
import cn.ocoop.shiro.subject.User;
import cn.ocoop.spring.App;
import org.apache.shiro.authc.*;
import org.apache.shiro.util.SimpleByteSource;

/**
 * Created by liolay on 2016/12/6.
 */
public class MobileCaptchaAuthorizingRealm extends AbstractAuthorizingRealm {


    protected SubjectResolve getSubjectResolve() {
        return App.getBean(MobileCaptchaSubjectResolve.class);
    }


    @Override
    public boolean supports(AuthenticationToken token) {
        return super.supports(token) && MobileCaptchaToken.class.isAssignableFrom(token.getClass());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        MobileCaptchaSubjectResolve subjectResolve = (MobileCaptchaSubjectResolve)getSubjectResolve();
        User user = subjectResolve.findLoginUser(token);
        if (user == null) {//没找到帐号
            user = subjectResolve.unknownAccountProcess(token);
        }
        if (user == null) {//没找到帐号
            throw new UnknownAccountException();
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

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        MobileCaptchaToken mobileCaptchaToken = (MobileCaptchaToken) token;
        try {
            if (!AuthcSmsCaptchaService.verify(mobileCaptchaToken.getUsername(), String.valueOf(mobileCaptchaToken.getPassword()))) {
                String msg = "Submitted captcha for token [" + mobileCaptchaToken + "] did not match the expected captcha.";
                throw new IncorrectCaptchaException(msg);
            }
            return;
        } catch (Throwable e) {
            throw new IncorrectCaptchaException(e);
        }
    }
}
