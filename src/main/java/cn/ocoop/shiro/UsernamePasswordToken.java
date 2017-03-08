package cn.ocoop.shiro;


import cn.ocoop.shiro.authc.TokenExtendFace;
import org.apache.shiro.authc.AuthenticationException;

public class UsernamePasswordToken extends org.apache.shiro.authc.UsernamePasswordToken implements TokenExtendFace {
    private AuthenticationException authenticationException;

    public UsernamePasswordToken(String username, String password, boolean rememberMe, String host) {
        super(username, password, rememberMe, host);
    }

    public AuthenticationException getAuthenticationException() {
        return authenticationException;
    }

    public void setAuthenticationException(AuthenticationException authenticationException) {
        this.authenticationException = authenticationException;
    }
}
