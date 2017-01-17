package cn.ocoop.shiro.sms;

public interface AuthcSmsCaptchaServiceAware {
    void sendMessage(String mobile);

    boolean verify(String mobile, String captcha);
}
