package org.zarhub.config.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.lang.NonNull;

import java.util.Collection;

public class CacheManagerProxy implements CacheManager {

    private final CacheManager delegate;

    public CacheManagerProxy(CacheManager delegate) {
        this.delegate = delegate;
    }

    @Override
    @NonNull
    public Cache getCache(@NonNull String name) {
        Cache cache = delegate.getCache(name);
        if (cache == null) {
            return null;
        }
        return new TransactionAwareCacheDecorator(cache);
    }

    @Override
    @NonNull
    public Collection<String> getCacheNames() {
        return delegate.getCacheNames();
    }
}
