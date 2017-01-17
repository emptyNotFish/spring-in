package cn.ocoop.shiro.filter.authz;

import cn.ocoop.shiro.utils.RequestUtil;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Created by liolay on 15-7-28.
 */
public class AnyRolesAuthorizationFilterAdapter extends RolesAuthorizationFilter {
    private int unLoginStatusCode = RequestUtil.SC_UNLOGIN;
    private int permsInvalidStatusCode = RequestUtil.SC_UNAUTHORIZED;

    public void setUnLoginStatusCode(int unLoginStatusCode) {
        this.unLoginStatusCode = unLoginStatusCode;
    }

    public void setPermsInvalidStatusCode(int permsInvalidStatusCode) {
        this.permsInvalidStatusCode = permsInvalidStatusCode;
    }

    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {

        Subject subject = getSubject(request, response);
        String[] rolesArray = (String[]) mappedValue;

        if (rolesArray == null || rolesArray.length == 0) {
            //no roles specified, so nothing to check - allow access.
            return true;
        }

        return Stream.of(rolesArray).anyMatch(subject::hasRole);
    }

    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {

        Subject subject = getSubject(request, response);
        // If the subject isn't identified, redirect to login URL
        if (subject.getPrincipal() == null) {
            RequestUtil.tipLoginInvalid(response, unLoginStatusCode);
        } else {
            RequestUtil.tipPermsInvalid(response, permsInvalidStatusCode);
        }
        return false;
    }

}
