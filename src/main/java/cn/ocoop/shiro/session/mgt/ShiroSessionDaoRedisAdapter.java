package cn.ocoop.shiro.session.mgt;

import cn.ocoop.shiro.session.iface.ShiroSessionRepositoryAware;
import org.apache.shiro.session.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by liolay on 15-7-27.
 */
public class ShiroSessionDaoRedisAdapter implements ShiroSessionRepositoryAware {
    private static final String REDIS_SHIRO_SESSION = "shiro-session:";
    private String sessionPrefix = REDIS_SHIRO_SESSION;
    private RedisTemplate redisTemplate = new StringRedisTemplate();

    public String getSessionPrefix() {
        return sessionPrefix;
    }

    public void setSessionPrefix(String sessionPrefix) {
        this.sessionPrefix = sessionPrefix;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveSession(final Session session) {
        if (session == null || session.getId() == null) return;

        final Long expireValue = session.getTimeout() / 1000;
        final String sessionKey = getRedisSessionKey(session.getId());
        redisTemplate.execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();

                operations.opsForValue().set(sessionKey, session);
                operations.expire(sessionKey, expireValue, TimeUnit.SECONDS);

                return operations.exec();
            }
        });

    }

    @Override
    public void deleteSession(final Serializable sessionId) {
        if (sessionId == null) {
            return;
        }
        redisTemplate.delete(getRedisSessionKey(sessionId));
    }

    @Override
    public Session getSession(final Serializable sessionId) {
        if (sessionId == null) {
            return null;
        }
        Object session = redisTemplate.opsForValue().get(getRedisSessionKey(sessionId));
        return session == null ? null : (Session) session;
    }

    @Override
    public Collection<Session> getAllSessions() {

        Set<String> keys = redisTemplate.keys(getSessionPrefix() + "*");
        if (keys == null || keys.size() <= 0) return null;

        return redisTemplate.opsForValue().multiGet(keys);
    }

    public String getRedisSessionKey(Serializable sessionId) {
        return getSessionPrefix() + sessionId;
    }

}
