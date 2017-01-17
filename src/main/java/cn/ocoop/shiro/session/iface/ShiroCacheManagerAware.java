package cn.ocoop.shiro.session.iface;

import org.apache.shiro.cache.Cache;

/**
 * Created by liolay on 15-7-27.
 */
public interface ShiroCacheManagerAware {
    <K, V> Cache<K, V> getCache(String name);

    void destroy();
}
