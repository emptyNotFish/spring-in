package cn.ocoop.shiro.sms;

import cn.ocoop.spring.App;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

public class AuthcSmsCaptchaService {
    public static final String LOGIN_SMS_CAPTCHA_LIMIT_TIME = "login_sms_captcha_limit_time";
    public static final int SEND_FREQUENCY = 60;
    private static final Logger log = LoggerFactory.getLogger(AuthcSmsCaptchaService.class);
    private static final ReentrantLock reentrantLock = new ReentrantLock();

    public static int send(String mobile) {

        try {
            reentrantLock.lock();
            if (unReachedFrequency()) return -1;//未到下次获取时间
            return sendMessage(mobile);
        } finally {
            reentrantLock.unlock();
        }
    }

    private static int sendMessage(String mobile) {
        try {
            App.getBean(AuthcSmsCaptchaServiceAware.class).sendMessage(mobile);
            getSession().setAttribute(LOGIN_SMS_CAPTCHA_LIMIT_TIME, Instant.now());
        } catch (Exception e) {
            log.error("登录短信验证码发送失败", e);
        }
        return SEND_FREQUENCY;
    }

    private static boolean unReachedFrequency() {
        Instant instant = (Instant) getSession().getAttribute(LOGIN_SMS_CAPTCHA_LIMIT_TIME);
        Instant now = Instant.now();
        instant = instant == null ? now.minusSeconds(SEND_FREQUENCY) : instant;
        if (instant.plusSeconds(SEND_FREQUENCY).isAfter(Instant.now())) return true;
        return false;
    }


    private static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    public static boolean verify(String mobile, String captcha) {
        boolean verifyResult = App.getBean(AuthcSmsCaptchaServiceAware.class).verify(mobile, captcha);
        getSession().removeAttribute(LOGIN_SMS_CAPTCHA_LIMIT_TIME);
        return verifyResult;
    }
}
