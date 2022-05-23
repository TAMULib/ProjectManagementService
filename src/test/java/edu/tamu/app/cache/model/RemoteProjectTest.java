package edu.tamu.app.cache.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RemoteProjectTest {

    @Test
    public void testNewRemoteProduct() {
        RemoteProject remoteProject = new RemoteProject("0001", "Sprint 1", 2, 3, 10, 3, 1);
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
