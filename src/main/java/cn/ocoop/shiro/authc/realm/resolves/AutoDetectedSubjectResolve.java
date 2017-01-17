package cn.ocoop.shiro.authc.realm.resolves;

import cn.ocoop.shiro.AutoDetectedToken;
import cn.ocoop.shiro.authc.realm.resolves.delegate.AutoDetectedSubjectResolveAware;
import cn.ocoop.shiro.spring.AppContextShiro;
import cn.ocoop.shiro.subject.User;
import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Created by liolay on 2016/12/6.
 */
@Service
public class AutoDetectedSubjectResolve extends SubjectResolve {

    @Override
    public User findLoginUser(AuthenticationToken token) {
        return AppContextShiro.getBean(AutoDetectedSubjectResolveAware.class).findLoginUser((AutoDetectedToken) token);
    }

}
