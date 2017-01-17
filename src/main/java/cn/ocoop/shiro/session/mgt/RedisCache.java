package cn.ocoop.shiro.session.mgt;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by liolay on 15-7-27.
 */
public class RedisCache<K, V> implements Cache<K, V> {
    private String name;
    private RedisTemplate redisTemplate;
    private String cachePrefix;

    public RedisCache(String name, RedisTemplate redisTemplate, String cachePrefix) {
        this.name = name;
        this.redisTemplate = redisTemplate;
        this.cachePrefix = cachePrefix;
    }

    public String getName() {
        if (name == null)
            return "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public V get(K key) throws CacheException {
        Object cachedValue = redisTemplate.opsForValue().get(getCacheKey(key));
        V v = convertNullValueIfNecessary(cachedValue);
        return v;
    }

    @Override
    public V put(final K key, final V value) throws CacheException {
        V previos = get(key);

        redisTemplate.opsForValue().set(getCacheKey(key), SerializationUtils.serialize((Serializable) value));
        return previos;
    }

    @Override
    public V remove(final K key) throws CacheException {
        V previos = get(key);
        if (previos == null) return null;

        redisTemplate.delete(getCacheKey(key));
        return previos;
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate.delete(cachePrefix + "*");
    }

    @Override
    public int size() {
        if (keys() == null)
            return 0;
        return keys().size();
    }

    @Override
    public Set<K> keys() {

        return redisTemplate.keys(cachePrefix + "*");

    }

    @Override
    public Collection<V> values() {
        Set<K> keys = keys();
        if (keys == null || keys.size() <= 0) return null;

        List<String> values = redisTemplate.opsForValue().multiGet(keys);
        if (values == null) return null;

        List<V> cachedValues = Lists.newArrayList();
        for (String cacheValue : values) {
            cachedValues.add(convertNullValueIfNecessary(cacheValue));
        }

        return cachedValues;

    }

    private String getCacheKey(Object key) {
        return cachePrefix + getName() + ":" + key;
    }

    private V convertNullValueIfNecessary(Object value) {
        if (value == null) return null;

        return (V) SerializationUtils.deserialize((byte[]) value);
    }
}
