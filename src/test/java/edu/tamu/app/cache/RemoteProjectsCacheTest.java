package edu.tamu.app.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.cache.model.RemoteProject;

@ExtendWith(SpringExtension.class)
public class RemoteProjectsCacheTest {

    @Test
    public void testNewRemoteProductsCache() {
        RemoteProjectsCache cache = new RemoteProjectsCache();
        assertNotNull(cache, "New remote projects cache was not created!");
        assertNotNull(cache.get(), "New remote projects cache remote projects were not created!");
    }

    @Test
    public void testSetCache() {
        RemoteProjectsCache cache = new RemoteProjectsCache();
        assertTrue(cache.get().isEmpty(), "Cached remote projects was not empty!");
        Map<Long, List<RemoteProject>> remoteProductMap = new HashMap<Long, List<RemoteProject>>();
        List<RemoteProject> remoteProjects = new ArrayList<RemoteProject>();
        remoteProjects.add(getMockRemoteProduct());
        remoteProductMap.put(1L, remoteProjects);
        cache.set(remoteProductMap);
        assertFalse(cache.get().isEmpty(), "Cached remote projects was empty!");
    }

    @Test
    public void testGetCache() {
        RemoteProjectsCache cache = new RemoteProjectsCache();
        Map<Long, List<RemoteProject>> remoteProductMap = new HashMap<Long, List<RemoteProject>>();
        List<RemoteProject> remoteProjects = new ArrayList<RemoteProject>();
        remoteProjects.add(getMockRemoteProduct());
        remoteProductMap.put(1L, remoteProjects);
        cache.set(remoteProductMap);
        Map<Long, List<RemoteProject>> remoteProductsCache = cache.get();
        assertFalse(remoteProductsCache.isEmpty(), "Cached remote projects was empty!");
        assertEquals(1, remoteProductsCache.size(), "Cached remote projects had incorrect number of remote projects!");
        assertEquals(1, remoteProductsCache.get(1L).size(), "Cached remote projects did not have expected remote projects for a given remote project manager!");

        assertEquals(remoteProductsCache.get(1L).get(0).getId(), "0001", "Cached remote project had incorrect id!");
        assertEquals("Sprint 1", remoteProductsCache.get(1L).get(0).getName(), "Cached remote project had incorrect name!");
        assertEquals(2, remoteProductsCache.get(1L).get(0).getRequestCount(), "Cached remote project had incorrect number of requests!");
        assertEquals(3, remoteProductsCache.get(1L).get(0).getIssueCount(), "Cached remote project had incorrect number of issues!");
        assertEquals(10, remoteProductsCache.get(1L).get(0).getFeatureCount(), "Cached remote project had incorrect number of features!");
        assertEquals(3, remoteProductsCache.get(1L).get(0).getDefectCount(), "Cached remote project had incorrect number of defects!");
        assertEquals(1, remoteProductsCache.get(1L).get(0).getInternalCount(), "Cached remote project had incorrect number of internals!");
        assertEquals(13, remoteProductsCache.get(1L).get(0).getBacklogItemCount(), "Cached remote project had incorrect total backlog items!");
    }

    private RemoteProject getMockRemoteProduct() {
        return new RemoteProject("0001", "Sprint 1", 2, 3, 10, 3, 1);
    }

}
