package edu.tamu.app.cache.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.model.ProductStats;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@RunWith(SpringRunner.class)
public class ProductsStatsCacheControllerTest {

    @Mock
    private ProductsStatsScheduledCacheService productsStatsScheduledCacheService;

    @InjectMocks
    private ProductsStatsCacheController productsStatsCacheController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(productsStatsScheduledCacheService.get()).thenReturn(getMockProductsStatsCache());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGet() {
        ApiResponse response = productsStatsCacheController.get();
        assertNotNull("Reponse was null!", response);
        assertEquals("Reponse was not successfull!", ApiStatus.SUCCESS, response.getMeta().getStatus());

        assertNotNull("Reponse payload did not have expected property!", response.getPayload().get("ArrayList<ProductStats>"));
        assertProductsStats((List<ProductStats>) response.getPayload().get("ArrayList<ProductStats>"));
    }

    @Test
    public void testUpdate() {
        ApiResponse response = productsStatsCacheController.update();
        assertNotNull(response);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(productsStatsScheduledCacheService, times(1)).update();
        verify(productsStatsScheduledCacheService, times(1)).broadcast();
    }

    private void assertProductsStats(List<ProductStats> productsStatsCache) {
        assertFalse(productsStatsCache.isEmpty());
        assertEquals(1, productsStatsCache.size());
        assertEquals("0001", productsStatsCache.get(0).getId());
        assertEquals("Sprint 1", productsStatsCache.get(0).getName());
        assertEquals(2, productsStatsCache.get(0).getRequestCount());
        assertEquals(3, productsStatsCache.get(0).getIssueCount());
        assertEquals(10, productsStatsCache.get(0).getFeatureCount());
        assertEquals(3, productsStatsCache.get(0).getDefectCount());
        assertEquals(1, productsStatsCache.get(0).getInternalCount());
        assertEquals(13, productsStatsCache.get(0).getBacklogItemCount());
    }

    private List<ProductStats> getMockProductsStatsCache() {
        List<ProductStats> productsStats = new ArrayList<ProductStats>();
        productsStats.add(getMockProductStats());
        return productsStats;
    }

    private ProductStats getMockProductStats() {
        return new ProductStats("0001", "Sprint 1", 2, 3, 10, 3, 1);
    }

}
