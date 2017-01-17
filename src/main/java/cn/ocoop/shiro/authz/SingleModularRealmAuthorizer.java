package cn.ocoop.shiro.authz;

import cn.ocoop.shiro.authc.realm.AbstractAuthorizingRealm;
import com.google.common.collect.Lists;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.List;

public class SingleModularRealmAuthorizer extends ModularRealmAuthorizer {

    public boolean hasRole(PrincipalCollection principals, String roleIdentifier) {
        assertRealmsConfigured();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer) || principals.fromRealm(realm.getName()).isEmpty()) continue;
            if (((Authorizer) realm).hasRole(principals, roleIdentifier)) return true;
        }
        return false;
    }

    public boolean isPermitted(PrincipalCollection principals, Permission permission) {
        assertRealmsConfigured();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer) || principals.fromRealm(realm.getName()).isEmpty()) continue;
            if (((Authorizer) realm).isPermitted(principals, permission)) return true;
        }
        return false;
    }

    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        assertRealmsConfigured();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer) || principals.fromRealm(realm.getName()).isEmpty()) continue;
            if (((Authorizer) realm).isPermitted(principals, permission)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getRoles() {
        assertRealmsConfigured();
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        List<String> roles = Lists.newArrayList();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer) || principals.fromRealm(realm.getName()).isEmpty()) continue;
            roles.addAll(((AbstractAuthorizingRealm) realm).getAuthorizationInfo(principals).getRoles());
        }
        return roles;
    }

    public List<String> getPermissions() {
        assertRealmsConfigured();
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        List<String> permissions = Lists.newArrayList();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer) || principals.fromRealm(realm.getName()).isEmpty()) continue;
            permissions.addAll(((AbstractAuthorizingRealm) realm).getAuthorizationInfo(principals).getStringPermissions());
        }
        return permissions;
    }
}