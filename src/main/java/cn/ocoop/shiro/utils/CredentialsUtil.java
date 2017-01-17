package cn.ocoop.shiro.utils;

import cn.ocoop.shiro.spring.AppContextShiro;
import cn.ocoop.shiro.subject.User;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.core.env.Environment;

public class CredentialsUtil {
    private static final String algorithmName = "md5";
    private static final int hashIterations = AppContextShiro.getBean(Environment.class).getProperty("shiro.credentials.iterations", int.class, 2);
    private static final boolean storedCredentialsHexEncoded = AppContextShiro.getBean(Environment.class).getProperty("shiro.credentials.hexEncoded", boolean.class, true);
    private static final RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    public static void encryptPassword(User user) {
        user.setSalt(randomNumberGenerator.nextBytes().toBase64());
        user.setPassword(getPassword(user.getPassword(), user.getSalt()));
    }

    public static String getPassword(final String password, final String salt) {
        SimpleHash simpleHash = new SimpleHash(
                algorithmName,
                password,
                salt,
                hashIterations
        );
        if (storedCredentialsHexEncoded) {
            return simpleHash.toString();
        }
        return simpleHash.toHex();
    }
}
