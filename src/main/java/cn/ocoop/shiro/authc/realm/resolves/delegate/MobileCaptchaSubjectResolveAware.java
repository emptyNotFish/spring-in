package cn.ocoop.shiro.authc.realm.resolves.delegate;

import cn.ocoop.shiro.subject.User;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * Created by liolay on 2017/1/9.
 */
public interface MobileCaptchaSubjectResolveAware {
    User findLoginUser(AuthenticationToken token);

    User unknownAccountProcess(AuthenticationToken token);
}
