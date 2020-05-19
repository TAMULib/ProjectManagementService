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

import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@RunWith(SpringRunner.class)
public class RemoteProjectsScheduledCacheServiceTest {

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

    @Before
    public void setup() throws ConnectionException, APIException, OidException, IOException {
        MockitoAnnotations.initMocks(this);
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
        assertTrue("Coult not find remote project!", remoteProject.isPresent());
        assertRemoteProduct(remoteProject.get());
    }

    private RemoteProjectManager getMockRemoteProductManager() {
        RemoteProjectManager remoteProjectManager = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE);
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
