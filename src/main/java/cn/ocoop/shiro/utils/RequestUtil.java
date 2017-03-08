package cn.ocoop.shiro.utils;

import com.alibaba.fastjson.JSON;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by liolay on 2017/1/9.
 */
public class RequestUtil {

    public static final int SC_INVALID_CAPTCHA = 420;//验证码发送太频繁/不正确
    public static final int SC_INVALID_CAPTCHA1 = 433;//验证码不正确
    public static final int SC_UNLOGIN = 418;//未登录
    public static final int SC_UNLOGIN_1 = 419;//未登录
    public static final int SC_UNLOGIN_2 = 421;//未登录
    public static final int SC_UNAUTHORIZED = HttpServletResponse.SC_UNAUTHORIZED;//未授权
    public static final int SC_UNAUTHORIZED_1 = 450;//未授权
    public static final int SC_UNAUTHORIZED_2 = 451;//未授权


    private RequestUtil() {
    }

    public static void response(ServletResponse response, int statusCode, Object message) throws IOException {
        HttpServletResponse resp = WebUtils.toHttp(response);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setStatus(statusCode);
        PrintWriter writer = resp.getWriter();
        writer.write(JSON.toJSONString(message));
        writer.flush();
    }

    public static void tipLoginInvalid(ServletResponse response) throws IOException {
        tipLoginInvalid(response, SC_UNLOGIN);
    }

    public static void tipLoginInvalid(ServletResponse response, int status) throws IOException {
        response(response, status, "登录超时或未登录，请重新登录");
    }


    public static void tipPermsInvalid(ServletResponse response) throws IOException {
        tipPermsInvalid(response, SC_UNAUTHORIZED);
    }

    public static void tipPermsInvalid(ServletResponse response, int status) throws IOException {
        response(response, status, "没有该资源的访问权限");
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return header != null && "XMLHttpRequest".equals(header);
    }
}
