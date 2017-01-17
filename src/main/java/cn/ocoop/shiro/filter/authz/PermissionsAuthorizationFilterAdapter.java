package cn.ocoop.shiro.filter.authz;

import cn.ocoop.shiro.utils.RequestUtil;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Created by liolay on 15-7-28.
 */
public class PermissionsAuthorizationFilterAdapter extends PermissionsAuthorizationFilter {
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
        if (subject.getPrincipal() == null) {
            RequestUtil.tipLoginInvalid(response, unLoginStatusCode);
        } else {
            RequestUtil.tipPermsInvalid(response, permsInvalidStatusCode);
        }
        return false;
    }

}
