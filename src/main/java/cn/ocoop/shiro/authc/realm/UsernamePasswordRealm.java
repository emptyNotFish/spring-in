package cn.ocoop.shiro.authc.realm;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Created by liolay on 2016/12/6.
 */
public class UsernamePasswordRealm extends AbstractMobileAuthorizingRealm {
    @Override
    public boolean supports(AuthenticationToken token) {
        return super.supports(token) && UsernamePasswordRealm.class.isAssignableFrom(token.getClass());
    }


}
