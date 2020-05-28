package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class RemoteProjectInfoModelTest extends ModelTest {

    @Test
    public void testGetScopeId() {
        RemoteProjectInfo rpi = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
        assertEquals("RemoteProjectInfo did not return the correct scope id!", TEST_PROJECT_SCOPE1, rpi.getScopeId());
    }

    @Test
    public void testGetRemoteProjectManager() {
        RemoteProjectInfo rpi = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
        assertEquals("RemoteProjectInfo did not return the correct remote project manager!", TEST_REMOTE_PROJECT_MANAGER1, rpi.getRemoteProjectManager());
    }

    @Test
    public void testSetScopeId() {
        RemoteProjectInfo rpi = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
        rpi.setScopeId(TEST_PROJECT_SCOPE2);
        assertEquals("RemoteProjectInfo did not correctly update the scope id!", TEST_PROJECT_SCOPE2, rpi.getScopeId());
    }

    @Test
    public void testSetRemoteProductManager() {
        RemoteProjectInfo rpi = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
        rpi.setRemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER2);
        assertEquals("RemoteProjectInfo did not return the correct remote project manager!", TEST_REMOTE_PROJECT_MANAGER2, rpi.getRemoteProjectManager());
    }

}
