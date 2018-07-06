package edu.tamu.app.cache.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RemoteProjectTest {

    @Test
    public void testNewRemoteProject() {
        RemoteProject remoteProject = new RemoteProject("0001", "Sprint 1");
        assertEquals("0001", remoteProject.getScopeId());
        assertEquals("Sprint 1", remoteProject.getName());
    }

}
