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

import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@RunWith(SpringRunner.class)
public class RemoteProjectsCacheControllerTest {

    @Mock
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @InjectMocks
    private RemoteProjectsCacheController remoteProjectsCacheController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(remoteProjectsScheduledCacheService.get()).thenReturn(getMockRemoteProjectsCache());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGet() {
        ApiResponse response = remoteProjectsCacheController.get();
        assertNotNull("Reponse was null!", response);
        assertEquals("Reponse was not successfull!", ApiStatus.SUCCESS, response.getMeta().getStatus());

        assertNotNull("Reponse payload did not have expected property!", response.getPayload().get("HashMap"));
        assertRemoteProjects((Map<Long, List<RemoteProject>>) response.getPayload().get("HashMap"));
    }

    @Test
    public void testUpdate() {
        ApiResponse response = remoteProjectsCacheController.update();
        assertNotNull(response);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(remoteProjectsScheduledCacheService, times(1)).update();
        verify(remoteProjectsScheduledCacheService, times(1)).broadcast();
    }

    private void assertRemoteProjects(Map<Long, List<RemoteProject>> remoteProjectsCache) {
        assertFalse(remoteProjectsCache.isEmpty());
        assertEquals(1, remoteProjectsCache.size());
        List<RemoteProject> remoteProjects = remoteProjectsCache.get(1L);
        assertFalse(remoteProjects.isEmpty());
        assertEquals(1, remoteProjects.size());
        assertEquals("0001", remoteProjects.get(0).getScopeId());
        assertEquals("Remote Project 1", remoteProjects.get(0).getName());
    }

    private Map<Long, List<RemoteProject>> getMockRemoteProjectsCache() {
        Map<Long, List<RemoteProject>> remoteProjectCache = new HashMap<Long, List<RemoteProject>>();
        List<RemoteProject> remoteProjects = new ArrayList<RemoteProject>();
        remoteProjects.add(getMockRemoteProject());
        remoteProjectCache.put(1L, remoteProjects);
        return remoteProjectCache;
    }

    private RemoteProject getMockRemoteProject() {
        return new RemoteProject("0001", "Remote Project 1");
    }

}
