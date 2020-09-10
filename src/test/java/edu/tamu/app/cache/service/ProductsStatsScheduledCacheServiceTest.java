package edu.tamu.app.cache.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;

@RunWith(SpringRunner.class)
public class ProductsStatsScheduledCacheServiceTest {
    private static final String TEST_PRODUCT_NAME = "Test Product";

    private static final String TEST_PROJECT_SCOPE = "0010";

    private static final String TEST_PROJECT_URL1 = "http://localhost/1";

    private static final String TEST_PROJECT_TOKEN1 = "0123456789";

    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO = new RemoteProjectInfo(TEST_PROJECT_SCOPE, TEST_REMOTE_PROJECT_MANAGER);

    private static final List<RemoteProjectInfo> TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO));

    @Mock
    private ProductRepo productRepo;

    @Mock
    private InternalRequestRepo internalRequestRepo;

    @Mock
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private ProductsStatsScheduledCacheService productsStatsScheduledCacheService;

    @Before
    public void setup() throws ConnectionException, APIException, OidException, IOException {
        MockitoAnnotations.initMocks(this);
        when(productRepo.findAll()).thenReturn(Arrays.asList(new Product[] { getMockProduct() }));
        when(remoteProjectsScheduledCacheService.getRemoteProject(any(Long.class), any(String.class))).thenReturn(Optional.of(getMockRemoteProduct()));
        when(internalRequestRepo.countByProductId(any(Long.class))).thenReturn(1L);
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
        product.setName("new Name");
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
        return new ProductStats("1000", TEST_PRODUCT_NAME, 2, 3, 10, 3, 1);
    }

    private RemoteProject getMockRemoteProduct() {
        return new RemoteProject("1000", TEST_PRODUCT_NAME, 2, 3, 10, 3, 1);
    }

    private void assertProductsStats(List<ProductStats> productStatsCache) {
        assertFalse(productStatsCache.isEmpty());
        assertEquals(1, productStatsCache.size());
        assertEquals("1000", productStatsCache.get(0).getId());
        assertEquals(TEST_PRODUCT_NAME, productStatsCache.get(0).getName());
        assertEquals(2, productStatsCache.get(0).getRequestCount());
        assertEquals(3, productStatsCache.get(0).getIssueCount());
        assertEquals(10, productStatsCache.get(0).getFeatureCount());
        assertEquals(3, productStatsCache.get(0).getDefectCount());
        assertEquals(1, productStatsCache.get(0).getInternalCount());
        assertEquals(13, productStatsCache.get(0).getBacklogItemCount());
    }

    private Product getMockProduct() {
        Product mockProduct = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST);
        mockProduct.setId(1000L);
        return mockProduct;
    }

}
