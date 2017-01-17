package cn.ocoop.shiro.session.mgt;

import cn.ocoop.shiro.session.iface.ShiroCacheManagerAware;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;

/**
 * Created by liolay on 15-7-27.
 */
public class ShiroCacheManager implements CacheManager, Destroyable {
    private ShiroCacheManagerAware shiroCacheManagerAware;

    public ShiroCacheManagerAware getShiroCacheManagerAware() {
        return shiroCacheManagerAware;
    }

    public void setShiroCacheManagerAware(ShiroCacheManagerAware shiroCacheManagerAware) {
        this.shiroCacheManagerAware = shiroCacheManagerAware;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return shiroCacheManagerAware.getCache(name);
    }

    @Override
    public void destroy() throws Exception {
        shiroCacheManagerAware.destroy();
    }
}
