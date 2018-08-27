package edu.tamu.app.cache;

public interface Cache<T> {

    public T get();

    public void set(T cache);

}
