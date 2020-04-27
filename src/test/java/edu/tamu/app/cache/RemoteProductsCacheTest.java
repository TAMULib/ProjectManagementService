package edu.tamu.app.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.model.RemoteProduct;

@RunWith(SpringRunner.class)
public class RemoteProductsCacheTest {

    @Test
    public void testNewRemoteProductsCache() {
        RemoteProductsCache cache = new RemoteProductsCache();
        assertNotNull("New remote products cache was not created!", cache);
        assertNotNull("New remote products cache remote products were not created!", cache.get());
    }

    @Test
    public void testSetCache() {
        RemoteProductsCache cache = new RemoteProductsCache();
        assertTrue("Cached remote products was not empty!", cache.get().isEmpty());
        Map<Long, List<RemoteProduct>> remoteProductMap = new HashMap<Long, List<RemoteProduct>>();
        List<RemoteProduct> remoteProducts = new ArrayList<RemoteProduct>();
        remoteProducts.add(getMockRemoteProduct());
        remoteProductMap.put(1L, remoteProducts);
        cache.set(remoteProductMap);
        assertFalse("Cached remote products was empty!", cache.get().isEmpty());
    }

    @Test
    public void testGetCache() {
        RemoteProductsCache cache = new RemoteProductsCache();
        Map<Long, List<RemoteProduct>> remoteProductMap = new HashMap<Long, List<RemoteProduct>>();
        List<RemoteProduct> remoteProducts = new ArrayList<RemoteProduct>();
        remoteProducts.add(getMockRemoteProduct());
        remoteProductMap.put(1L, remoteProducts);
        cache.set(remoteProductMap);
        Map<Long, List<RemoteProduct>> remoteProductsCache = cache.get();
        assertFalse("Cached remote products was empty!", remoteProductsCache.isEmpty());
        assertEquals("Cached remote products had incorrect number of remote products!", 1, remoteProductsCache.size());
        assertEquals("Cached remote products did not have expected remote products for a given remote product manager!", 1, remoteProductsCache.get(1L).size());

        assertEquals("Cached remote product had incorrect id!", "0001", remoteProductsCache.get(1L).get(0).getId());
        assertEquals("Cached remote product had incorrect name!", "Sprint 1", remoteProductsCache.get(1L).get(0).getName());
        assertEquals("Cached remote product had incorrect number of requests!", 2, remoteProductsCache.get(1L).get(0).getRequestCount());
        assertEquals("Cached remote product had incorrect number of issues!", 3, remoteProductsCache.get(1L).get(0).getIssueCount());
        assertEquals("Cached remote product had incorrect number of features!", 10, remoteProductsCache.get(1L).get(0).getFeatureCount());
        assertEquals("Cached remote product had incorrect number of defects!", 3, remoteProductsCache.get(1L).get(0).getDefectCount());
        assertEquals("Cached remote product had incorrect total backlog items!", 13, remoteProductsCache.get(1L).get(0).getBacklogItemCount());
    }

    private RemoteProduct getMockRemoteProduct() {
        return new RemoteProduct("0001", "Sprint 1", 2, 3, 10, 3);
    }

}
