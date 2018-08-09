package edu.tamu.app.cache.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

import edu.tamu.app.cache.model.ProjectStats;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProjectRepo;

@RunWith(SpringRunner.class)
public class ProjectsStatsScheduledCacheServiceTest {

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private ProjectsStatsScheduledCacheService projectsStatsScheduledCacheService;

    @Before
    public void setup() throws ConnectionException, APIException, OidException, IOException {
        MockitoAnnotations.initMocks(this);
        when(projectRepo.findAll()).thenReturn(Arrays.asList(new Project[] { getMockProject() }));
        when(remoteProjectsScheduledCacheService.getRemoteProject(any(Long.class), any(String.class))).thenReturn(Optional.of(getMockRemoteProject()));
    }

    @Test
    public void testSchedule() {
        projectsStatsScheduledCacheService.schedule();
        assertProjectsStats(projectsStatsScheduledCacheService.get());
    }

    @Test
    public void testUpdate() {
        projectsStatsScheduledCacheService.update();
        assertProjectsStats(projectsStatsScheduledCacheService.get());
    }

    @Test
    public void testBroadcast() {
        projectsStatsScheduledCacheService.broadcast();
        assertTrue(true);
    }

    @Test
    public void testGet() {
        projectsStatsScheduledCacheService.schedule();
        assertProjectsStats(projectsStatsScheduledCacheService.get());
    }

    @Test
    public void testSet() {
        projectsStatsScheduledCacheService.set(getMockProjectsStatsCache());
        assertProjectsStats(projectsStatsScheduledCacheService.get());
    }

    private List<ProjectStats> getMockProjectsStatsCache() {
        List<ProjectStats> projectsStatsCache = new ArrayList<ProjectStats>();
        projectsStatsCache.add(getMockProjectStats());
        return projectsStatsCache;
    }

    private ProjectStats getMockProjectStats() {
        return new ProjectStats("1", "Test Project", 2, 3, 10, 3);
    }

    private RemoteProject getMockRemoteProject() {
        return new RemoteProject("0001", "Sprint 1", 2, 3, 10, 3);
    }

    private void assertProjectsStats(List<ProjectStats> projectStatsCache) {
        assertFalse(projectStatsCache.isEmpty());
        assertEquals(1, projectStatsCache.size());
        assertEquals("1", projectStatsCache.get(0).getId());
        assertEquals("Test Project", projectStatsCache.get(0).getName());
        assertEquals(2, projectStatsCache.get(0).getRequestCount());
        assertEquals(3, projectStatsCache.get(0).getIssueCount());
        assertEquals(10, projectStatsCache.get(0).getFeatureCount());
        assertEquals(3, projectStatsCache.get(0).getDefectCount());
        assertEquals(13, projectStatsCache.get(0).getBacklogItemCount());
    }

    private Project getMockProject() {
        RemoteProjectManager remoteProjectManager = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE);
        Project mockProject = new Project("Test Project", "0001", remoteProjectManager);
        mockProject.setId(1L);
        return mockProject;
    }

}
