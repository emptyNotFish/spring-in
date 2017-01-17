package cn.ocoop.shiro.authc.realm.resolves.delegate;

import cn.ocoop.shiro.subject.User;
import org.apache.shiro.authc.AuthenticationToken;

import java.util.List;

public interface SubjectResolveAware {
    Object findLoginUserInfo(String userId, boolean needBindOpenId);

    List<String> findRoles(String userId);

    List<String> findPermissions(String userId);

    default User unknownAccountProcess(AuthenticationToken token) {
        return null;
    }

    void bindOpenId(String userId, String mobile, String openId);
}
