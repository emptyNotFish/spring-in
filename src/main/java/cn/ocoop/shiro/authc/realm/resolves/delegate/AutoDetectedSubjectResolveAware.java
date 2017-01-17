package cn.ocoop.shiro.authc.realm.resolves.delegate;

import cn.ocoop.shiro.AutoDetectedToken;
import cn.ocoop.shiro.subject.User;

public interface AutoDetectedSubjectResolveAware {
    User findLoginUser(AutoDetectedToken token);
}
