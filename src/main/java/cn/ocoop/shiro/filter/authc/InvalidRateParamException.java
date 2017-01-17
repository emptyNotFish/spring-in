package cn.ocoop.shiro.filter.authc;

import org.apache.shiro.session.SessionException;

/**
 * Created by liolay on 2017/1/14.
 */
public class InvalidRateParamException extends SessionException {
    public InvalidRateParamException() {
        super();
    }

    public InvalidRateParamException(String message) {
        super(message);
    }
}
