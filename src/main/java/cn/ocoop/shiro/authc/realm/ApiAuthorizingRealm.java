package cn.ocoop.shiro.authc.realm;

import cn.ocoop.shiro.ApiToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * Created by liolay on 2016/12/6.
 */
public class ApiAuthorizingRealm extends AbstractMobileAuthorizingRealm {

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return super.supports(token) && ApiToken.class.isAssignableFrom(token.getClass());
    }
}
