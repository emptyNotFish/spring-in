package cn.ocoop.shiro.utils;


import cn.ocoop.shiro.authz.SingleModularRealmAuthorizer;
import cn.ocoop.shiro.filter.AjaxAuthenticationFilter;
import cn.ocoop.shiro.filter.AutoAuthenticationFilter;
import cn.ocoop.shiro.spring.AppContextShiro;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.shiro.SecurityUtils.getSubject;

public class SubjectUtil {
    private SubjectUtil() {
    }

    public static String getOpenId() {
        return (String) getSubject().getSession().getAttribute(AutoAuthenticationFilter.OPEN_ID);
    }

    public static PrincipalCollection getPrincipals() {
        return SecurityUtils.getSubject().getPrincipals();
    }

    public static Object getLoginInfo() {
        Session session = getSubject().getSession(false);
        if (session == null || !isLogin()) return null;
        return session.getAttribute(AjaxAuthenticationFilter.USER_INFO_KEY);
    }

    public static boolean isLogin() {
        return getSubject().getPrincipal() != null;
    }

    public static boolean hasAllRole(String... role){
        return getSubject().hasAllRoles(Stream.of(role).collect(Collectors.toSet()));
    }

    public static boolean hasAnyRole(String... roleIdentifiers) {
        Set<String> roleTypeList = Stream.of(roleIdentifiers).collect(Collectors.toSet());
        for (String roleType : roleTypeList) {
            if (getSubject().hasRole(roleType)) return true;
        }
        return false;
    }

    public static boolean hasPermission(String permission) {
        return getSubject().isPermitted(permission);
    }
    public static boolean lackPermission(String permission) {
        return getSubject().isPermitted(permission);
    }

    public static List<String> getRoles() {
        return AppContextShiro.getBean(SingleModularRealmAuthorizer.class).getRoles();
    }

    public static List<String> getPermissions() {
        return AppContextShiro.getBean(SingleModularRealmAuthorizer.class).getPermissions();
    }


}
