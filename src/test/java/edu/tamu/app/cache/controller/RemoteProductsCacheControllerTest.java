package edu.tamu.app.cache.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.cache.service.RemoteProductsScheduledCacheService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@RunWith(SpringRunner.class)
public class RemoteProductsCacheControllerTest {

    @Mock
    private RemoteProductsScheduledCacheService remoteProductsScheduledCacheService;

    @InjectMocks
    private RemoteProductsCacheController remoteProductsCacheController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(remoteProductsScheduledCacheService.get()).thenReturn(getMockRemoteProductsCache());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGet() {
        ApiResponse response = remoteProductsCacheController.get();
        assertNotNull("Reponse was null!", response);
        assertEquals("Reponse was not successfull!", ApiStatus.SUCCESS, response.getMeta().getStatus());

        assertNotNull("Reponse payload did not have expected property!", response.getPayload().get("HashMap"));
        assertRemoteProducts((Map<Long, List<RemoteProduct>>) response.getPayload().get("HashMap"));
    }

    @Test
    public void testUpdate() {
        ApiResponse response = remoteProductsCacheController.update();
        assertNotNull(response);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(remoteProductsScheduledCacheService, times(1)).update();
        verify(remoteProductsScheduledCacheService, times(1)).broadcast();
    }

    private void assertRemoteProducts(Map<Long, List<RemoteProduct>> remoteProductsCache) {
        assertFalse(remoteProductsCache.isEmpty());
        assertEquals(1, remoteProductsCache.size());
        List<RemoteProduct> remoteProducts = remoteProductsCache.get(1L);
        assertFalse(remoteProducts.isEmpty());
        assertEquals(1, remoteProducts.size());
        assertEquals("0001", remoteProducts.get(0).getId());
        assertEquals("Sprint 1", remoteProducts.get(0).getName());
        assertEquals(2, remoteProducts.get(0).getRequestCount());
        assertEquals(3, remoteProducts.get(0).getIssueCount());
        assertEquals(10, remoteProducts.get(0).getFeatureCount());
        assertEquals(3, remoteProducts.get(0).getDefectCount());
        assertEquals(13, remoteProducts.get(0).getBacklogItemCount());
    }

    private Map<Long, List<RemoteProduct>> getMockRemoteProductsCache() {
        Map<Long, List<RemoteProduct>> remoteProductCache = new HashMap<Long, List<RemoteProduct>>();
        List<RemoteProduct> remoteProducts = new ArrayList<RemoteProduct>();
        remoteProducts.add(getMockRemoteProduct());
        remoteProductCache.put(1L, remoteProducts);
        return remoteProductCache;
    }

    private RemoteProduct getMockRemoteProduct() {
        return new RemoteProduct("0001", "Sprint 1", 2, 3, 10, 3);
    }

}
