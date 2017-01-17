package cn.ocoop.shiro;

import cn.ocoop.shiro.authc.TokenExtendFace;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 根据openId自动发现身份信息token
 * Created by liolay on 15-8-19.
 */
public class AutoDetectedToken extends UsernamePasswordToken implements TokenExtendFace {
    private String openId;

    public AutoDetectedToken(String username, String password, boolean rememberMe, String host, String openId) {
        super(username, password, rememberMe, host);
        this.openId = openId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
