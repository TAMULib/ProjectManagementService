package edu.tamu.app.cache.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.versionone.apiclient.exceptions.APIException;
import com.versionone.apiclient.exceptions.ConnectionException;
import com.versionone.apiclient.exceptions.OidException;

import edu.tamu.app.cache.model.ProductStats;
import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProductInfo;
import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProductRepo;

@RunWith(SpringRunner.class)
public class ProductsStatsScheduledCacheServiceTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private RemoteProductsScheduledCacheService remoteProductsScheduledCacheService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private ProductsStatsScheduledCacheService productsStatsScheduledCacheService;

    @Before
    public void setup() throws ConnectionException, APIException, OidException, IOException {
        MockitoAnnotations.initMocks(this);
        when(productRepo.findAll()).thenReturn(Arrays.asList(new Product[] { getMockProduct() }));
        when(remoteProductsScheduledCacheService.getRemoteProduct(any(Long.class), any(String.class))).thenReturn(Optional.of(getMockRemoteProduct()));
    }

    @Test
    public void testSchedule() {
        productsStatsScheduledCacheService.schedule();
        assertProductsStats(productsStatsScheduledCacheService.get());
    }

    @Test
    public void testUpdate() {
        productsStatsScheduledCacheService.update();
        assertProductsStats(productsStatsScheduledCacheService.get());
    }

    @Test
    public void testBroadcast() {
        productsStatsScheduledCacheService.broadcast();
        assertTrue(true);
    }

    @Test
    public void testAddProduct() {
        productsStatsScheduledCacheService.addProduct(getMockProduct());
        assertProductsStats(productsStatsScheduledCacheService.get());
    }

    @Test
    public void testUpdateProduct() {
        Product product = getMockProduct();
        productsStatsScheduledCacheService.addProduct(product);
        product.setScopeId("1001");
        productsStatsScheduledCacheService.updateProduct(product);
        assertTrue(true);
    }

    @Test
    public void testRemoveProduct() {
        Product product = getMockProduct();
        productsStatsScheduledCacheService.addProduct(product);
        productsStatsScheduledCacheService.removeProduct(product);
        assertTrue(productsStatsScheduledCacheService.get().isEmpty());
    }

    @Test
    public void testGet() {
        productsStatsScheduledCacheService.schedule();
        assertProductsStats(productsStatsScheduledCacheService.get());
    }

    @Test
    public void testSet() {
        productsStatsScheduledCacheService.set(getMockProductsStatsCache());
        assertProductsStats(productsStatsScheduledCacheService.get());
    }

    private List<ProductStats> getMockProductsStatsCache() {
        List<ProductStats> productsStatsCache = new ArrayList<ProductStats>();
        productsStatsCache.add(getMockProductStats());
        return productsStatsCache;
    }

    private ProductStats getMockProductStats() {
        return new ProductStats("1000", "Test Product", 2, 3, 10, 3);
    }

    private RemoteProduct getMockRemoteProduct() {
        return new RemoteProduct("1000", "Test Product", 2, 3, 10, 3);
    }

    private void assertProductsStats(List<ProductStats> productStatsCache) {
        assertFalse(productStatsCache.isEmpty());
        assertEquals(1, productStatsCache.size());
        assertEquals("1000", productStatsCache.get(0).getId());
        assertEquals("Test Product", productStatsCache.get(0).getName());
        assertEquals(2, productStatsCache.get(0).getRequestCount());
        assertEquals(3, productStatsCache.get(0).getIssueCount());
        assertEquals(10, productStatsCache.get(0).getFeatureCount());
        assertEquals(3, productStatsCache.get(0).getDefectCount());
        assertEquals(13, productStatsCache.get(0).getBacklogItemCount());
    }

    private Product getMockProduct() {
        RemoteProductManager remoteProductManager = new RemoteProductManager("Test Remote Product Manager", ServiceType.VERSION_ONE);
        Product mockProduct = new Product("Test Product", "0001", remoteProductManager);
        mockProduct.setId(1000L);
        return mockProduct;
    }

}
