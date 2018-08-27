package edu.tamu.app.cache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import edu.tamu.app.cache.Cache;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

public abstract class AbstractScheduledCacheService<T, C extends Cache<T>> implements ScheduledCache<T, C>, Ordered {

    private C cache;

    @Autowired
    protected ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    protected SimpMessagingTemplate simpMessagingTemplate;

    public AbstractScheduledCacheService(C cache) {
        this.cache = cache;
    }

    @Scheduled(initialDelayString = "${app.cache.default.delay}", fixedDelayString = "${app.cache.default.interval}")
    public void schedule() {
        update();
        broadcast();
    }

    public T get() {
        return cache.get();
    }

    public void set(T cache) {
        this.cache.set(cache);
    }

}
