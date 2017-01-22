package cn.ocoop.shiro.filter;

import cn.ocoop.shiro.MobileCaptchaToken;
import cn.ocoop.shiro.UsernamePasswordToken;
import cn.ocoop.shiro.authc.LoginType;
import cn.ocoop.shiro.authc.realm.resolves.MobileCaptchaSubjectResolve;
import cn.ocoop.shiro.authc.realm.resolves.SubjectResolve;
import cn.ocoop.shiro.cache.ShiroRealmCacheManager;
import cn.ocoop.shiro.utils.RequestUtil;
import cn.ocoop.shiro.utils.SubjectUtil;
import cn.ocoop.shiro.vo.Result;
import cn.ocoop.spring.App;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class AjaxAuthenticationFilter extends FormAuthenticationFilter {
    public static final String USER_INFO_KEY = "user";
    public static final String DEFAULT_ERROR_KEY_EXCEPTION_NAME = "shiroLoginFailureException";
    public static final String LOGIN_TYPE = getEnvironment().getProperty("shiro.authc.loginTypeParam","loginType");

    private static Environment getEnvironment() {
        return App.getBean(Environment.class);
    }
    private static final Logger log = LoggerFactory.getLogger(AjaxAuthenticationFilter.class);
    protected int unLoginStatusCode = RequestUtil.SC_UNLOGIN_1;

    public void setUnLoginStatusCode(int unLoginStatusCode) {
        this.unLoginStatusCode = unLoginStatusCode;
    }

    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {

            if (isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }
                return executeLogin(request, response);
            }

            if (log.isTraceEnabled()) {
                log.trace("Login page view.");
            }
            //allow them to see the login page ;)
            return true;
        }


        if (log.isTraceEnabled()) {
            log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                    "Authentication url [" + getLoginUrl() + "]");
        }

        RequestUtil.tipLoginInvalid(response, unLoginStatusCode);
        return false;


    }

    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                     ServletRequest request, ServletResponse response) throws Exception {

        resolveLoginInfo(token);
        RequestUtil.response(response, HttpServletResponse.SC_OK, Result.build("success", "登录成功", getRedirectUrl(request, getDefaultSuccessUrl())));
        return false;
    }

    protected String getDefaultSuccessUrl() {
        return null;
    }

    protected void resolveLoginInfo(AuthenticationToken token) {
        saveUserInfo2Session(token);
        ShiroRealmCacheManager.clearCachedAuthorizationInfo(SubjectUtil.getPrincipals());
    }

    private void saveUserInfo2Session(AuthenticationToken token) {
        SecurityUtils.getSubject().getSession().setAttribute(
                USER_INFO_KEY,
                getSubjectResolve(token).findLoginUserInfoAndBindOpenId((String) SecurityUtils.getSubject().getPrincipal())
        );
    }

    private SubjectResolve getSubjectResolve(AuthenticationToken token) {

        if (MobileCaptchaToken.class.isAssignableFrom(token.getClass()) || UsernamePasswordToken.class.isAssignableFrom(token.getClass())) {
            return App.getBean(MobileCaptchaSubjectResolve.class);
        }
        throw new Error("不支持的token类型");
    }

    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {

        if (LoginType.CAPTCHA.equals(WebUtils.getCleanParam(request, LOGIN_TYPE))) {
            return new MobileCaptchaToken(getUsername(request),
                    getPassword(request),
                    isRememberMe(request),
                    getHost(request));
        }
        return new UsernamePasswordToken(getUsername(request),
                getPassword(request),
                isRememberMe(request),
                getHost(request));
    }

    protected String getRedirectUrl(ServletRequest request, String fallbackUrl) {
        String successUrl = null;
        SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(request);
        if (savedRequest != null && savedRequest.getMethod().equalsIgnoreCase(AccessControlFilter.GET_METHOD)) {
            successUrl = savedRequest.getRequestUrl();
        }

        if (successUrl == null) {
            successUrl = fallbackUrl;
        }

        if (successUrl == null) {
            throw new IllegalStateException("Success URL not available via saved request or via the " +
                    "successUrlFallback method parameter. One of these must be non-null for " +
                    "issueSuccessRedirect() to work.");
        }
        return successUrl;
    }

    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                     ServletRequest request, ServletResponse response) {
        request.setAttribute(DEFAULT_ERROR_KEY_EXCEPTION_NAME, e);
        request.setAttribute(getFailureKeyAttribute(), e.getClass().getName());
        request.setAttribute("token", token);
        //login failed, let request continue back to the login page:
        return true;
    }
}
