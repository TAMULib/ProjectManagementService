package edu.tamu.app.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.model.ProductStats;

@RunWith(SpringRunner.class)
public class ProductStatsCacheTest {

    @Test
    public void testNewProductStatsCache() {
        ProductsStatsCache cache = new ProductsStatsCache();
        assertNotNull("New products stats cache was not created!", cache);
        assertNotNull("New products stats cache products stats were not created!", cache.get());
    }

    @Test
    public void testSetCache() {
        ProductsStatsCache cache = new ProductsStatsCache();
        assertTrue("Cached products stats was not empty!", cache.get().isEmpty());
        List<ProductStats> productsStats = new ArrayList<ProductStats>();
        productsStats.add(getMockProductStats());
        cache.set(productsStats);
        assertFalse("Cached remoteProducts was empty!", cache.get().isEmpty());
    }

    @Test
    public void testGetCache() {
        ProductsStatsCache cache = new ProductsStatsCache();
        List<ProductStats> productsStats = new ArrayList<ProductStats>();
        productsStats.add(getMockProductStats());
        cache.set(productsStats);
        List<ProductStats> remoteProductsCache = cache.get();
        assertFalse("Cached products statss was empty!", remoteProductsCache.isEmpty());
        assertEquals("Cached products statss had incorrect number of products statss!", 1, remoteProductsCache.size());

        assertEquals("Cached product stats had incorrect id!", "0001", remoteProductsCache.get(0).getId());
        assertEquals("Cached product stats had incorrect name!", "Sprint 1", remoteProductsCache.get(0).getName());
        assertEquals("Cached product stats had incorrect number of requests!", 2, remoteProductsCache.get(0).getRequestCount());
        assertEquals("Cached product stats had incorrect number of issues!", 3, remoteProductsCache.get(0).getIssueCount());
        assertEquals("Cached product stats had incorrect number of features!", 10, remoteProductsCache.get(0).getFeatureCount());
        assertEquals("Cached product stats had incorrect number of defects!", 3, remoteProductsCache.get(0).getDefectCount());
        assertEquals("Cached product stats had incorrect total backlog items!", 13, remoteProductsCache.get(0).getBacklogItemCount());
    }

    private ProductStats getMockProductStats() {
        return new ProductStats("0001", "Sprint 1", 2, 3, 10, 3);
    }

}
