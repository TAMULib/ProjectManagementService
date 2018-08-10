package edu.tamu.app.cache.service;

import edu.tamu.app.cache.Cache;

public abstract class AbstractProjectScheduledCacheService<T, C extends Cache<T>> extends AbstractScheduledCacheService<T, C> implements ProjectScheduledCache<T, C> {

    public AbstractProjectScheduledCacheService(C cache) {
        super(cache);
    }

}
