package cn.ocoop.shiro;


import cn.ocoop.shiro.authc.TokenExtendFace;

public class UsernamePasswordToken extends org.apache.shiro.authc.UsernamePasswordToken implements TokenExtendFace {
    public UsernamePasswordToken(String username, String password, boolean rememberMe, String host) {
        super(username, password, rememberMe, host);
    }
}
