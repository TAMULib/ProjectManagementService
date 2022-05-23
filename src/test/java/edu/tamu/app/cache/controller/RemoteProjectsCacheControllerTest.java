package edu.tamu.app.cache.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@ExtendWith(SpringExtension.class)
public class RemoteProjectsCacheControllerTest {

    @Mock
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @InjectMocks
    private RemoteProjectsCacheController remoteProjectsCacheController;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(remoteProjectsScheduledCacheService.get()).thenReturn(getMockRemoteProductsCache());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGet() {
        ApiResponse response = remoteProjectsCacheController.get();
        assertNotNull(response, "Response was null!");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), "Response was not successful!");

        assertNotNull(response.getPayload().get("HashMap"), "Response payload did not have expected property!");
        assertRemoteProducts((Map<Long, List<RemoteProject>>) response.getPayload().get("HashMap"));
    }

    @Test
    public void testUpdate() {
        ApiResponse response = remoteProjectsCacheController.update();
        assertNotNull(response);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(remoteProjectsScheduledCacheService, times(1)).update();
        verify(remoteProjectsScheduledCacheService, times(1)).broadcast();
    }

    private void assertRemoteProducts(Map<Long, List<RemoteProject>> remoteProductsCache) {
        assertFalse(remoteProductsCache.isEmpty());
        assertEquals(1, remoteProductsCache.size());
        List<RemoteProject> remoteProjects = remoteProductsCache.get(1L);
        assertFalse(remoteProjects.isEmpty());
        assertEquals(1, remoteProjects.size());
        assertEquals("0001", remoteProjects.get(0).getId());
        assertEquals("Sprint 1", remoteProjects.get(0).getName());
        assertEquals(2, remoteProjects.get(0).getRequestCount());
        assertEquals(3, remoteProjects.get(0).getIssueCount());
        assertEquals(10, remoteProjects.get(0).getFeatureCount());
        assertEquals(3, remoteProjects.get(0).getDefectCount());
        assertEquals(1, remoteProjects.get(0).getInternalCount());
        assertEquals(13, remoteProjects.get(0).getBacklogItemCount());
    }

    private Map<Long, List<RemoteProject>> getMockRemoteProductsCache() {
        Map<Long, List<RemoteProject>> remoteProductCache = new HashMap<Long, List<RemoteProject>>();
        List<RemoteProject> remoteProjects = new ArrayList<RemoteProject>();
        remoteProjects.add(getMockRemoteProduct());
        remoteProductCache.put(1L, remoteProjects);
        return remoteProductCache;
    }

    private RemoteProject getMockRemoteProduct() {
        return new RemoteProject("0001", "Sprint 1", 2, 3, 10, 3, 1);
    }

}
