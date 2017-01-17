package cn.ocoop.shiro.filter;

import cn.ocoop.shiro.spring.AppContextShiro;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class LogoutFilter extends org.apache.shiro.web.filter.authc.LogoutFilter {
    protected String getRedirectUrl(ServletRequest request, ServletResponse response, Subject subject) {
        return AppContextShiro.getBean(AjaxAuthenticationFilter.class).getLoginUrl();
    }

}
