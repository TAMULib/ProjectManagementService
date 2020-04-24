package edu.tamu.app.cache.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.model.ProjectStats;
import edu.tamu.app.cache.service.ProjectsStatsScheduledCacheService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@RunWith(SpringRunner.class)
public class ProjectsStatsCacheControllerTest {

    @Mock
    private ProjectsStatsScheduledCacheService projectsStatsScheduledCacheService;

    @InjectMocks
    private ProjectsStatsCacheController projectsStatsCacheController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(projectsStatsScheduledCacheService.get()).thenReturn(getMockProjectsStatsCache());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGet() {
        ApiResponse response = projectsStatsCacheController.get();
        assertNotNull("Reponse was null!", response);
        assertEquals("Reponse was not successfull!", ApiStatus.SUCCESS, response.getMeta().getStatus());

        assertNotNull("Reponse payload did not have expected property!", response.getPayload().get("ArrayList<ProjectStats>"));
        assertProjectsStats((List<ProjectStats>) response.getPayload().get("ArrayList<ProjectStats>"));
    }

    @Test
    public void testUpdate() {
        ApiResponse response = projectsStatsCacheController.update();
        assertNotNull(response);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(projectsStatsScheduledCacheService, times(1)).update();
        verify(projectsStatsScheduledCacheService, times(1)).broadcast();
    }

    private void assertProjectsStats(List<ProjectStats> projectsStatsCache) {
        assertFalse(projectsStatsCache.isEmpty());
        assertEquals(1, projectsStatsCache.size());
        assertEquals("0001", projectsStatsCache.get(0).getId());
        assertEquals("Sprint 1", projectsStatsCache.get(0).getName());
        assertEquals(2, projectsStatsCache.get(0).getRequestCount());
        assertEquals(3, projectsStatsCache.get(0).getIssueCount());
        assertEquals(10, projectsStatsCache.get(0).getFeatureCount());
        assertEquals(3, projectsStatsCache.get(0).getDefectCount());
        assertEquals(13, projectsStatsCache.get(0).getBacklogItemCount());
    }

    private List<ProjectStats> getMockProjectsStatsCache() {
        List<ProjectStats> projectsStats = new ArrayList<ProjectStats>();
        projectsStats.add(getMockProjectStats());
        return projectsStats;
    }

    private ProjectStats getMockProjectStats() {
        return new ProjectStats("0001", "Sprint 1", 2, 3, 10, 3);
    }

}
