package edu.tamu.app.cache.service;

import edu.tamu.app.cache.Cache;
import edu.tamu.app.model.Product;

public interface ProductScheduledCache<T, C extends Cache<T>> extends ScheduledCache<T, C> {

    public void addProduct(Product product);

    public void updateProduct(Product product);

    public void removeProduct(Product product);

}
