package cn.ocoop.shiro.filter.authc;

import cn.ocoop.shiro.utils.RequestUtil;
import cn.ocoop.spring.App;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.core.env.Environment;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by liolay on 16-6-20.
 */
public class CaptchaVerifyFilter extends AccessControlFilter {
    public static final String SUBMIT_CAPTCHA = App.getBean(Environment.class).getProperty("shiro.captcha.header", "Submit-Captcha");

    private static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        if (WebUtils.toHttp(request).getMethod().equalsIgnoreCase(GET_METHOD)) return true;

        if (getSession() == null) throw new InvalidRateParamException("未发现有效的会话,该请求不允许访问");
        return correctCaptcha(request);
    }

    private boolean correctCaptcha(ServletRequest request) {
        String receivedCaptcha = getReceivedCaptcha(request);
        String sessionCaptcha = getAndClearSessionCaptcha();
        if (StringUtils.isAnyBlank(sessionCaptcha, receivedCaptcha) || !sessionCaptcha.equalsIgnoreCase(receivedCaptcha))
            return false;
        return true;
    }

    private String getReceivedCaptcha(ServletRequest request) {
        return WebUtils.toHttp(request).getHeader(SUBMIT_CAPTCHA);
    }

    private String getAndClearSessionCaptcha() {
        return (String) getSession().removeAttribute(SUBMIT_CAPTCHA);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        RequestUtil.response(response, RequestUtil.SC_INVALID_CAPTCHA1, "验证码不正确");
        return false;
    }

}
