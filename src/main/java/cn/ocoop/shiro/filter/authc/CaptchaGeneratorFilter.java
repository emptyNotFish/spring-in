package cn.ocoop.shiro.filter.authc;

import cn.ocoop.captcha.Captcha;
import cn.ocoop.captcha.GifCaptcha;
import cn.ocoop.captcha.SpecCaptcha;
import cn.ocoop.spring.App;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.core.env.Environment;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class CaptchaGeneratorFilter extends RateLimitWithCaptchaFilter {

    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        Captcha captcha = getCaptcha();
        Session session = SecurityUtils.getSubject().getSession();
        if (session == null) return;

        session.setAttribute(SUBMIT_CAPTCHA, captcha.getCaptcha());
        captcha.out(response.getOutputStream());
    }

    private Captcha getCaptcha() {
        Environment environment = App.getBean(Environment.class);
        String captchaType = environment.getProperty("shiro.captcha.img.type", "jpg");
        int width = environment.getProperty("shiro.captcha.img.width", int.class, 150);
        int height = environment.getProperty("shiro.captcha.img.height", int.class, 40);
        int length = environment.getProperty("shiro.captcha.img.length", int.class, 4);
        if ("jpg".equals(captchaType)) return new SpecCaptcha(width, height, length);
        return new GifCaptcha(width, height, length);
    }
}
