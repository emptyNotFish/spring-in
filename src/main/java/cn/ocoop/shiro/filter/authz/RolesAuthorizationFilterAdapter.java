package cn.ocoop.shiro.filter.authz;

import cn.ocoop.shiro.utils.RequestUtil;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Created by liolay on 15-7-28.
 */
public class RolesAuthorizationFilterAdapter extends RolesAuthorizationFilter {

    private int unLoginStatusCode = RequestUtil.SC_UNLOGIN;
    private int permsInvalidStatusCode = RequestUtil.SC_UNAUTHORIZED;

    public void setUnLoginStatusCode(int unLoginStatusCode) {
        this.unLoginStatusCode = unLoginStatusCode;
    }

    public void setPermsInvalidStatusCode(int permsInvalidStatusCode) {
        this.permsInvalidStatusCode = permsInvalidStatusCode;
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
