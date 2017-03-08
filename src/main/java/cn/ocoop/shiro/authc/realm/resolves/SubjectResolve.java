package cn.ocoop.shiro.authc.realm.resolves;

import cn.ocoop.shiro.authc.realm.resolves.delegate.SubjectResolveAware;
import cn.ocoop.shiro.subject.User;
import cn.ocoop.spring.App;
import org.apache.shiro.authc.AuthenticationToken;

import java.util.List;

public abstract class SubjectResolve {

    public abstract User findLoginUser(AuthenticationToken token);

    public Object findLoginUserInfoAndBindOpenId(String userId) {
        return findLoginUserInfo(userId, true);
    }

    public Object findLoginUserInfo(String userId) {
        return findLoginUserInfo(userId, false);
    }

    private Object findLoginUserInfo(String userId, boolean needBindOpenId) {
        return App.getBean(SubjectResolveAware.class).findLoginUserInfo(userId, needBindOpenId);
    }

    public List<String> findRoles(String userId) {
        return App.getBean(SubjectResolveAware.class).findRoles(userId);
    }

    public List<String> findPermissions(String userId) {
        return App.getBean(SubjectResolveAware.class).findPermissions(userId);
    }
}

