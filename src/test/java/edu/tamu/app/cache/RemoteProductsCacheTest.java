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

import edu.tamu.app.cache.model.RemoteProject;

@RunWith(SpringRunner.class)
public class RemoteProjectsCacheTest {

    @Test
    public void testNewRemoteProjectsCache() {
        RemoteProjectsCache cache = new RemoteProjectsCache();
        assertNotNull("New remote projects cache was not created!", cache);
        assertNotNull("New remote projects cache remote projects were not created!", cache.get());
    }

    @Test
    public void testSetCache() {
        RemoteProjectsCache cache = new RemoteProjectsCache();
        assertTrue("Cached remote projects was not empty!", cache.get().isEmpty());
        Map<Long, List<RemoteProject>> remoteProjectMap = new HashMap<Long, List<RemoteProject>>();
        List<RemoteProject> remoteProjects = new ArrayList<RemoteProject>();
        remoteProjects.add(getMockRemoteProject());
        remoteProjectMap.put(1L, remoteProjects);
        cache.set(remoteProjectMap);
        assertFalse("Cached remote projects was empty!", cache.get().isEmpty());
    }

    @Test
    public void testGetCache() {
        RemoteProjectsCache cache = new RemoteProjectsCache();
        Map<Long, List<RemoteProject>> remoteProjectMap = new HashMap<Long, List<RemoteProject>>();
        List<RemoteProject> remoteProjects = new ArrayList<RemoteProject>();
        remoteProjects.add(getMockRemoteProject());
        remoteProjectMap.put(1L, remoteProjects);
        cache.set(remoteProjectMap);
        Map<Long, List<RemoteProject>> remoteProjectsCache = cache.get();
        assertFalse("Cached remote projects was empty!", remoteProjectsCache.isEmpty());
        assertEquals("Cached remote projects had incorrect number of remote projects!", 1, remoteProjectsCache.size());
        assertEquals("Cached remote projects did not have expected remote projects for a given remote project manager!", 1, remoteProjectsCache.get(1L).size());

        assertEquals("Cached remote project had incorrect id!", "0001", remoteProjectsCache.get(1L).get(0).getId());
        assertEquals("Cached remote project had incorrect name!", "Sprint 1", remoteProjectsCache.get(1L).get(0).getName());
        assertEquals("Cached remote project had incorrect number of requests!", 2, remoteProjectsCache.get(1L).get(0).getRequestCount());
        assertEquals("Cached remote project had incorrect number of issues!", 3, remoteProjectsCache.get(1L).get(0).getIssueCount());
        assertEquals("Cached remote project had incorrect number of features!", 10, remoteProjectsCache.get(1L).get(0).getFeatureCount());
        assertEquals("Cached remote project had incorrect number of defects!", 3, remoteProjectsCache.get(1L).get(0).getDefectCount());
        assertEquals("Cached remote project had incorrect total backlog items!", 13, remoteProjectsCache.get(1L).get(0).getBacklogItemCount());
    }

    private RemoteProject getMockRemoteProject() {
        return new RemoteProject("0001", "Sprint 1", 2, 3, 10, 3);
    }

}
