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
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@RunWith(SpringRunner.class)
public class RemoteProjectsScheduledCacheServiceTest {

    @Mock
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

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
        when(remoteProjectManagerRepo.findAll()).thenReturn(Arrays.asList(new RemoteProjectManager[] { getMockRemoteProjectManager() }));
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);
        when(versionOneService.getRemoteProjects()).thenReturn(Arrays.asList(new RemoteProject[] { getMockRemoteProject() }));
    }

    @Test
    public void testSchedule() {
        remoteProjectsScheduledCacheService.schedule();
        assertRemoteProjects(remoteProjectsScheduledCacheService.get());
    }

    @Test
    public void testUpdate() {
        remoteProjectsScheduledCacheService.update();
        assertRemoteProjects(remoteProjectsScheduledCacheService.get());
    }

    @Test
    public void testBroadcast() {
        remoteProjectsScheduledCacheService.broadcast();
        assertTrue(true);
    }

    @Test
    public void testGet() {
        remoteProjectsScheduledCacheService.schedule();
        assertRemoteProjects(remoteProjectsScheduledCacheService.get());
    }

    @Test
    public void testSet() {
        remoteProjectsScheduledCacheService.set(getMockRemoteProjectsCache());
        assertRemoteProjects(remoteProjectsScheduledCacheService.get());
    }

    private RemoteProjectManager getMockRemoteProjectManager() {
        RemoteProjectManager remoteProjectManager = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE);
        remoteProjectManager.setId(1L);
        return remoteProjectManager;
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

    private void assertRemoteProjects(Map<Long, List<RemoteProject>> remoteProjectsCache) {
        assertFalse(remoteProjectsCache.isEmpty());
        assertEquals(1, remoteProjectsCache.size());
        List<RemoteProject> remoteProjects = remoteProjectsCache.get(1L);
        assertFalse(remoteProjects.isEmpty());
        assertEquals(1, remoteProjects.size());
        assertEquals("0001", remoteProjects.get(0).getScopeId());
        assertEquals("Remote Project 1", remoteProjects.get(0).getName());
    }

}
