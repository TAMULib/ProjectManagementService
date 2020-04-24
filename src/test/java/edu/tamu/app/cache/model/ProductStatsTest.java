package edu.tamu.app.cache.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ProjectStatsTest {

    @Test
    public void testNewProjectStats() {
        ProjectStats projectStats = new ProjectStats("0001", "Sprint 1", 2, 3, 10, 3);
        assertEquals("0001", projectStats.getId());
        assertEquals("Sprint 1", projectStats.getName());
        assertEquals(2, projectStats.getRequestCount());
        assertEquals(3, projectStats.getIssueCount());
        assertEquals(10, projectStats.getFeatureCount());
        assertEquals(3, projectStats.getDefectCount());
        assertEquals(13, projectStats.getBacklogItemCount());
    }

}
