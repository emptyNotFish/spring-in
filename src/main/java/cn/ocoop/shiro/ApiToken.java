package cn.ocoop.shiro;

import cn.ocoop.shiro.authc.TokenExtendFace;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 第三方应用登录token
 * Created by liolay on 15-8-19.
 */
public class ApiToken extends UsernamePasswordToken implements TokenExtendFace {

    public ApiToken(String username, String password, boolean rememberMe, String host) {
        super(username, password, rememberMe, host);
    }
}
