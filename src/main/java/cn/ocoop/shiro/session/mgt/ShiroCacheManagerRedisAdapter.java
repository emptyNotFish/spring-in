package cn.ocoop.shiro.session.mgt;

import cn.ocoop.shiro.session.iface.ShiroCacheManagerAware;
import org.apache.shiro.cache.Cache;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created by liolay on 15-7-27.
 */
public class ShiroCacheManagerRedisAdapter implements ShiroCacheManagerAware {
    private static final String REDIS_SHIRO_CACHE = "shiro-cache:";
    private String cachePrefix = REDIS_SHIRO_CACHE;
    private RedisTemplate redisTemplate = new StringRedisTemplate();

    public String getCachePrefix() {
        return cachePrefix;
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        return new RedisCache<K, V>(name, redisTemplate, cachePrefix);
    }

    @Override
    public void destroy() {
        try {
            JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) redisTemplate.getConnectionFactory();
            jedisConnectionFactory.destroy();
        } catch (Error error) {
        }
    }
}
