package edu.tamu.app.cache;

public abstract class AbstractCache<T> implements Cache<T> {

    private T cache;

    public synchronized T get() {
        return cache;
    }

    public synchronized void set(T cache) {
        this.cache = cache;
    }

}
