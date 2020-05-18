package edu.tamu.app.cache.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.RemoteProductManagerRepo;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@RunWith(SpringRunner.class)
public class RemoteProductsScheduledCacheServiceTest {

    @Mock
    private RemoteProductManagerRepo remoteProductManagerRepo;

    @Mock
    private InternalRequestRepo internalRequestRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RemoteProductsScheduledCacheService remoteProductsScheduledCacheService;

    @Before
    public void setup() throws ConnectionException, APIException, OidException, IOException {
        MockitoAnnotations.initMocks(this);
        VersionOneService versionOneService = mock(VersionOneService.class);
        when(remoteProductManagerRepo.findAll()).thenReturn(Arrays.asList(new RemoteProductManager[] { getMockRemoteProductManager() }));
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);
        when(internalRequestRepo.countByProductId(any(Long.class))).thenReturn(1L);
        when(versionOneService.getRemoteProduct()).thenReturn(Arrays.asList(new RemoteProduct[] { getMockRemoteProduct() }));
    }

    @Test
    public void testSchedule() {
        remoteProductsScheduledCacheService.schedule();
        assertRemoteProducts(remoteProductsScheduledCacheService.get());
    }

    @Test
    public void testUpdate() {
        remoteProductsScheduledCacheService.update();
        assertRemoteProducts(remoteProductsScheduledCacheService.get());
    }

    @Test
    public void testBroadcast() {
        remoteProductsScheduledCacheService.broadcast();
        assertTrue(true);
    }

    @Test
    public void testGet() {
        remoteProductsScheduledCacheService.schedule();
        assertRemoteProducts(remoteProductsScheduledCacheService.get());
    }

    @Test
    public void testSet() {
        remoteProductsScheduledCacheService.set(getMockRemoteProductsCache());
        assertRemoteProducts(remoteProductsScheduledCacheService.get());
    }

    @Test
    public void testGetRemoteProduct() {
        remoteProductsScheduledCacheService.set(getMockRemoteProductsCache());
        Optional<RemoteProduct> remoteProduct = remoteProductsScheduledCacheService.getRemoteProduct(1L, "0001");
        assertTrue("Coult not find remote product!", remoteProduct.isPresent());
        assertRemoteProduct(remoteProduct.get());
    }

    private RemoteProductManager getMockRemoteProductManager() {
        RemoteProductManager remoteProductManager = new RemoteProductManager("Test Remote Product Manager", ServiceType.VERSION_ONE);
        remoteProductManager.setId(1L);
        return remoteProductManager;
    }

    private Map<Long, List<RemoteProduct>> getMockRemoteProductsCache() {
        Map<Long, List<RemoteProduct>> remoteProductCache = new HashMap<Long, List<RemoteProduct>>();
        List<RemoteProduct> remoteProducts = new ArrayList<RemoteProduct>();
        remoteProducts.add(getMockRemoteProduct());
        remoteProductCache.put(1L, remoteProducts);
        return remoteProductCache;
    }

    private RemoteProduct getMockRemoteProduct() {
        return new RemoteProduct("0001", "Sprint 1", 2, 3, 10, 3, 1);
    }

    private void assertRemoteProducts(Map<Long, List<RemoteProduct>> remoteProductsCache) {
        assertFalse(remoteProductsCache.isEmpty());
        assertEquals(1, remoteProductsCache.size());
        List<RemoteProduct> remoteProducts = remoteProductsCache.get(1L);
        assertFalse(remoteProducts.isEmpty());
        assertEquals(1, remoteProducts.size());
        assertRemoteProduct(remoteProducts.get(0));
    }

    private void assertRemoteProduct(RemoteProduct remoteProduct) {
        assertEquals("0001", remoteProduct.getId());
        assertEquals("Sprint 1", remoteProduct.getName());
        assertEquals(2, remoteProduct.getRequestCount());
        assertEquals(3, remoteProduct.getIssueCount());
        assertEquals(10, remoteProduct.getFeatureCount());
        assertEquals(3, remoteProduct.getDefectCount());
        assertEquals(1, remoteProduct.getInternalCount());
        assertEquals(13, remoteProduct.getBacklogItemCount());
    }

}
