package cn.ocoop.shiro.authc.realm.resolves;

import cn.ocoop.shiro.authc.realm.resolves.delegate.UsernamePasswordResolveAware;
import cn.ocoop.shiro.subject.User;
import cn.ocoop.spring.App;
import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Created by liolay on 2017/3/7.
 */
@Service
public class UsernamePasswordSubjectResolve extends SubjectResolve {

    @Override
    public User findLoginUser(AuthenticationToken token) {
        return App.getBean(UsernamePasswordResolveAware.class).findLoginUser(token);
    }
}
