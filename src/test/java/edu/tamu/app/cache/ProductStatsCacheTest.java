package edu.tamu.app.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.model.ProjectStats;

@RunWith(SpringRunner.class)
public class ProjectStatsCacheTest {

    @Test
    public void testNewProjectStatsCache() {
        ProjectsStatsCache cache = new ProjectsStatsCache();
        assertNotNull("New projects stats cache was not created!", cache);
        assertNotNull("New projects stats cache projects stats were not created!", cache.get());
    }

    @Test
    public void testSetCache() {
        ProjectsStatsCache cache = new ProjectsStatsCache();
        assertTrue("Cached projects stats was not empty!", cache.get().isEmpty());
        List<ProjectStats> projectsStats = new ArrayList<ProjectStats>();
        projectsStats.add(getMockProjectStats());
        cache.set(projectsStats);
        assertFalse("Cached remoteProjects was empty!", cache.get().isEmpty());
    }

    @Test
    public void testGetCache() {
        ProjectsStatsCache cache = new ProjectsStatsCache();
        List<ProjectStats> projectsStats = new ArrayList<ProjectStats>();
        projectsStats.add(getMockProjectStats());
        cache.set(projectsStats);
        List<ProjectStats> remoteProjectsCache = cache.get();
        assertFalse("Cached projects statss was empty!", remoteProjectsCache.isEmpty());
        assertEquals("Cached projects statss had incorrect number of projects statss!", 1, remoteProjectsCache.size());

        assertEquals("Cached project stats had incorrect id!", "0001", remoteProjectsCache.get(0).getId());
        assertEquals("Cached project stats had incorrect name!", "Sprint 1", remoteProjectsCache.get(0).getName());
        assertEquals("Cached project stats had incorrect number of requests!", 2, remoteProjectsCache.get(0).getRequestCount());
        assertEquals("Cached project stats had incorrect number of issues!", 3, remoteProjectsCache.get(0).getIssueCount());
        assertEquals("Cached project stats had incorrect number of features!", 10, remoteProjectsCache.get(0).getFeatureCount());
        assertEquals("Cached project stats had incorrect number of defects!", 3, remoteProjectsCache.get(0).getDefectCount());
        assertEquals("Cached project stats had incorrect total backlog items!", 13, remoteProjectsCache.get(0).getBacklogItemCount());
    }

    private ProjectStats getMockProjectStats() {
        return new ProjectStats("0001", "Sprint 1", 2, 3, 10, 3);
    }

}
