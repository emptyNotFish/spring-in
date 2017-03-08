package cn.ocoop.shiro.authc.exception;

import org.apache.shiro.authc.DisabledAccountException;

/**
 * Created by liolay on 2017/3/8.
 */
public class UnActivedAccountException  extends DisabledAccountException {

    /**
     * Creates a new LockedAccountException.
     */
    public UnActivedAccountException() {
        super();
    }

    /**
     * Constructs a new LockedAccountException.
     *
     * @param message the reason for the exception
     */
    public UnActivedAccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new LockedAccountException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public UnActivedAccountException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new LockedAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public UnActivedAccountException(String message, Throwable cause) {
        super(message, cause);
    }

}