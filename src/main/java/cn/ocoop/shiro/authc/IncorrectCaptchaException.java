package cn.ocoop.shiro.authc;

import org.apache.shiro.authc.IncorrectCredentialsException;

public class IncorrectCaptchaException extends IncorrectCredentialsException {
    public IncorrectCaptchaException() {
        super();
    }

    public IncorrectCaptchaException(String message) {
        super(message);
    }

    public IncorrectCaptchaException(Throwable cause) {
        super(cause);
    }

    public IncorrectCaptchaException(String message, Throwable cause) {
        super(message, cause);
    }
}