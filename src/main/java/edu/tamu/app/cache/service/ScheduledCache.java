package edu.tamu.app.cache.service;

import edu.tamu.app.cache.Cache;

public interface ScheduledCache<T, C extends Cache<T>> {

    public void schedule();

    public T get();

    public void set(T cache);

    public void update();

    public void broadcast();

}
