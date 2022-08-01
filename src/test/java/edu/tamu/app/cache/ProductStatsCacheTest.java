package edu.tamu.app.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.cache.model.ProductStats;

@ExtendWith(SpringExtension.class)
public class ProductStatsCacheTest {

    @Test
    public void testNewProductStatsCache() {
        ProductsStatsCache cache = new ProductsStatsCache();
        assertNotNull(cache, "New products stats cache was not created!");
        assertNotNull(cache.get(), "New products stats cache products stats were not created!");
    }

    @Test
    public void testSetCache() {
        ProductsStatsCache cache = new ProductsStatsCache();
        assertTrue(cache.get().isEmpty(), "Cached products stats was not empty!");
        List<ProductStats> productsStats = new ArrayList<ProductStats>();
        productsStats.add(getMockProductStats());
        cache.set(productsStats);
        assertFalse(cache.get().isEmpty(), "Cached remoteProducts was empty!");
    }

    @Test
    public void testGetCache() {
        ProductsStatsCache cache = new ProductsStatsCache();
        List<ProductStats> productsStats = new ArrayList<ProductStats>();
        productsStats.add(getMockProductStats());
        cache.set(productsStats);
        List<ProductStats> remoteProductsCache = cache.get();
        assertFalse(remoteProductsCache.isEmpty(), "Cached products stats was empty!");
        assertEquals(1, remoteProductsCache.size(), "Cached products stats had incorrect number of products status!");

        assertEquals(remoteProductsCache.get(0).getId(), "Cached product stats had incorrect id!", "0001");
        assertEquals(remoteProductsCache.get(0).getName(), "Cached product stats had incorrect name!", "Sprint 1");
        assertEquals(2, remoteProductsCache.get(0).getRequestCount(), "Cached product stats had incorrect number of requests!");
        assertEquals(3, remoteProductsCache.get(0).getIssueCount(), "Cached product stats had incorrect number of issues!");
        assertEquals(10, remoteProductsCache.get(0).getFeatureCount(), "Cached product stats had incorrect number of features!");
        assertEquals(3, remoteProductsCache.get(0).getDefectCount(), "Cached product stats had incorrect number of defects!");
        assertEquals(1, remoteProductsCache.get(0).getInternalCount(), "Cached product stats had incorrect number of internals!");
        assertEquals(13, remoteProductsCache.get(0).getBacklogItemCount(), "Cached product stats had incorrect total backlog items!");
    }

    private ProductStats getMockProductStats() {
        return new ProductStats("0001", "Sprint 1", 2, 3, 10, 3, 1);
    }

}
