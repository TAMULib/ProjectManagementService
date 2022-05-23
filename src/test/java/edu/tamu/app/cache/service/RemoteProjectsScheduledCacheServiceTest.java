package edu.tamu.app.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.versionone.apiclient.exceptions.APIException;
import com.versionone.apiclient.exceptions.ConnectionException;
import com.versionone.apiclient.exceptions.OidException;

import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@ExtendWith(SpringExtension.class)
public class RemoteProjectsScheduledCacheServiceTest {

    private static final String TEST_PROJECT_URL1 = "http://localhost/1";

    private static final String TEST_PROJECT_TOKEN1 = "0123456789";

    @Mock
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Mock
    private InternalRequestRepo internalRequestRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @BeforeEach
    public void setup() throws ConnectionException, APIException, OidException, IOException {
        MockitoAnnotations.openMocks(this);
        VersionOneService versionOneService = mock(VersionOneService.class);
        when(remoteProjectManagerRepo.findAll()).thenReturn(Arrays.asList(new RemoteProjectManager[] { getMockRemoteProductManager() }));
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);
        when(internalRequestRepo.countByProductId(any(Long.class))).thenReturn(1L);
        when(versionOneService.getRemoteProject()).thenReturn(Arrays.asList(new RemoteProject[] { getMockRemoteProduct() }));
    }

    @Test
    public void testSchedule() {
        remoteProjectsScheduledCacheService.schedule();
        assertRemoteProducts(remoteProjectsScheduledCacheService.get());
    }

    @Test
    public void testUpdate() {
        remoteProjectsScheduledCacheService.update();
        assertRemoteProducts(remoteProjectsScheduledCacheService.get());
    }

    @Test
    public void testBroadcast() {
        remoteProjectsScheduledCacheService.broadcast();
        assertTrue(true);
    }

    @Test
    public void testGet() {
        remoteProjectsScheduledCacheService.schedule();
        assertRemoteProducts(remoteProjectsScheduledCacheService.get());
    }

    @Test
    public void testSet() {
        remoteProjectsScheduledCacheService.set(getMockRemoteProductsCache());
        assertRemoteProducts(remoteProjectsScheduledCacheService.get());
    }

    @Test
    public void testGetRemoteProduct() {
        remoteProjectsScheduledCacheService.set(getMockRemoteProductsCache());
        Optional<RemoteProject> remoteProject = remoteProjectsScheduledCacheService.getRemoteProject(1L, "0001");
        assertTrue(remoteProject.isPresent(), "Could not find remote project!");
        assertRemoteProduct(remoteProject.get());
    }

    private RemoteProjectManager getMockRemoteProductManager() {
        RemoteProjectManager remoteProjectManager = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);
        remoteProjectManager.setId(1L);
        return remoteProjectManager;
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

    private void assertRemoteProducts(Map<Long, List<RemoteProject>> remoteProductsCache) {
        assertFalse(remoteProductsCache.isEmpty());
        assertEquals(1, remoteProductsCache.size());
        List<RemoteProject> remoteProjects = remoteProductsCache.get(1L);
        assertFalse(remoteProjects.isEmpty());
        assertEquals(1, remoteProjects.size());
        assertRemoteProduct(remoteProjects.get(0));
    }

    private void assertRemoteProduct(RemoteProject remoteProject) {
        assertEquals("0001", remoteProject.getId());
        assertEquals("Sprint 1", remoteProject.getName());
        assertEquals(2, remoteProject.getRequestCount());
        assertEquals(3, remoteProject.getIssueCount());
        assertEquals(10, remoteProject.getFeatureCount());
        assertEquals(3, remoteProject.getDefectCount());
        assertEquals(1, remoteProject.getInternalCount());
        assertEquals(13, remoteProject.getBacklogItemCount());
    }

}
