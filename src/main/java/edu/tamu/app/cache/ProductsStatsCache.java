package edu.tamu.app.cache;

import java.util.ArrayList;
import java.util.List;

import edu.tamu.app.cache.model.ProductStats;

public class ProductsStatsCache extends AbstractCache<List<ProductStats>> {

    public ProductsStatsCache() {
        set(new ArrayList<ProductStats>());
    }

}
