package edu.tamu.app.cache.service;

import edu.tamu.app.cache.Cache;

public abstract class AbstractProductScheduledCacheService<T, C extends Cache<T>> extends AbstractScheduledCacheService<T, C> implements ProductScheduledCache<T, C> {

    public AbstractProductScheduledCacheService(C cache) {
        super(cache);
    }

}
