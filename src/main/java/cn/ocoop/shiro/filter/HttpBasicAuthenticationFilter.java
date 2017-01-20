package cn.ocoop.shiro.filter;

import cn.ocoop.shiro.HttpBasicToken;
import org.apache.shiro.authc.AuthenticationToken;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by liolay on 15-8-20.
 */
public class HttpBasicAuthenticationFilter extends org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter {

    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false;
    }

    protected AuthenticationToken createToken(String username, String password,
                                              boolean rememberMe, String host) {
        return new HttpBasicToken(username, password, rememberMe, host);
    }
}

