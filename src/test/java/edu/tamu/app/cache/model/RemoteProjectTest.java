package edu.tamu.app.cache.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RemoteProjectTest {

    @Test
    public void testNewRemoteProject() {
        RemoteProject remoteProject = new RemoteProject("0001", "Sprint 1", 2, 3, 10, 3);
        assertEquals("0001", remoteProject.getScopeId());
        assertEquals("Sprint 1", remoteProject.getName());
        assertEquals(2, remoteProject.getRequestCount());
        assertEquals(3, remoteProject.getIssueCount());
        assertEquals(10, remoteProject.getStoryCount());
        assertEquals(3, remoteProject.getDefectCount());
        assertEquals(13, remoteProject.getBacklogItemCount());
    }

}
