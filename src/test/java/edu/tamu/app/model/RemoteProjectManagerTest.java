package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProjectApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class RemoteProjectManagerTest extends ModelTest {

    @Test
    public void testCreate() {
        Map<String, String> settings = getMockSettings();
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME, ServiceType.VERSION_ONE, settings));
        assertEquals("Remote project manager repo had incorrect number of remote project managers!", 1, remoteProjectManagerRepo.count());
        assertEquals("Remote project manager had incorrect name!", TEST_REMOTE_PROJECT_MANAGER_NAME, remoteProjectManager.getName());
        assertEquals("Remote project manager had incorrect service type!", ServiceType.VERSION_ONE, remoteProjectManager.getType());
        assertEquals("Remote project manager had incorrect settings!", settings, remoteProjectManager.getSettings());
    }

    @Test
    public void testRead() {
        remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        assertEquals("Could not read all remote project managers!", 1, remoteProjectManagerRepo.findAll().size());
    }

    @Test
    public void testUpdate() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        remoteProjectManager.setName(TEST_ALTERNATE_REMOTE_PROJECT_MANAGER_NAME);
        remoteProjectManager = remoteProjectManagerRepo.update(remoteProjectManager);
        assertEquals("Remote project manager did not update name!", TEST_ALTERNATE_REMOTE_PROJECT_MANAGER_NAME, remoteProjectManager.getName());
    }

    @Test
    public void testDelete() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        remoteProjectManagerRepo.delete(remoteProjectManager);
        assertEquals("Remote project manager was note deleted!", 0, remoteProjectManagerRepo.count());
    }

    @Test
    public void testAssociateToProject() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        Project project = projectRepo.create(new Project(TEST_PROJECT_NAME, "1000", remoteProjectManager));
        assertEquals("Project has the incorrect Remote Project Manager name!", TEST_REMOTE_PROJECT_MANAGER_NAME, project.getRemoteProjectManager().getName());
        assertEquals("Project has the incorrect Remote Project Manager url setting value!", "https://localhost:9101/TexasAMLibrary", project.getRemoteProjectManager().getSettings().get("url"));

    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDeleteWhenAssociatedToProject() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        projectRepo.create(new Project(TEST_PROJECT_NAME, "1000", remoteProjectManager));
        remoteProjectManagerRepo.delete(remoteProjectManager);
    }

}
