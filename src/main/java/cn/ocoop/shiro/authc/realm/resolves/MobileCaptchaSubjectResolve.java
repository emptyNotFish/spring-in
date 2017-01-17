package cn.ocoop.shiro.authc.realm.resolves;

import cn.ocoop.shiro.authc.realm.resolves.delegate.MobileCaptchaSubjectResolveAware;
import cn.ocoop.shiro.spring.AppContextShiro;
import cn.ocoop.shiro.subject.User;
import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Created by liolay on 2016/12/6.
 */
@Service
public class MobileCaptchaSubjectResolve extends SubjectResolve {

    @Override
    public User findLoginUser(AuthenticationToken token) {
        return AppContextShiro.getBean(MobileCaptchaSubjectResolveAware.class).findLoginUser(token);
    }

    public User unknownAccountProcess(AuthenticationToken token) {
        if (!((String) token.getPrincipal()).matches("^1\\d{10}$")) return super.unknownAccountProcess(token);
        return AppContextShiro.getBean(MobileCaptchaSubjectResolveAware.class).unknownAccountProcess(token);
    }
}
