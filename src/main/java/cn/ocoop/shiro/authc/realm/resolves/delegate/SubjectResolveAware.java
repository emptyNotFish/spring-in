package cn.ocoop.shiro.authc.realm.resolves.delegate;

import java.util.List;

public interface SubjectResolveAware {
    Object findLoginUserInfo(String userId, boolean needBindOpenId);

    List<String> findRoles(String userId);

    List<String> findPermissions(String userId);
}
