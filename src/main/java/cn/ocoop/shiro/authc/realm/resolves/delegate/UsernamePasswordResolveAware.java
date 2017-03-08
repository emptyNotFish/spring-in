package cn.ocoop.shiro.authc.realm.resolves.delegate;

import cn.ocoop.shiro.subject.User;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * Created by liolay on 2017/3/7.
 */
public interface UsernamePasswordResolveAware {
    User findLoginUser(AuthenticationToken token);
}
