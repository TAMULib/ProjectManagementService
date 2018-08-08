package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProjectApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ProjectTest extends ModelTest {

    @Test
    public void testCreate() {
        projectRepo.create(new Project(TEST_PROJECT_NAME));
        assertEquals("Project repo had incorrect number of projects!", 1, projectRepo.count());
    }

    @Test
    public void testRead() {
        projectRepo.create(new Project(TEST_PROJECT_NAME));
        Optional<Project> project = projectRepo.findByName(TEST_PROJECT_NAME);
        assertTrue("Could not read project!", project.isPresent());
        assertEquals("Project read did not have the correct name!", TEST_PROJECT_NAME, project.get().getName());
    }

    @Test
    public void testUpdate() {
        Project project = projectRepo.create(new Project(TEST_PROJECT_NAME));
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        project.setName(TEST_ALTERNATE_PROJECT_NAME);
        project.setScopeId("123456");
        project.setRemoteProjectManager(remoteProjectManager);
        project = projectRepo.update(project);
        assertEquals("Project name was not updated!", TEST_ALTERNATE_PROJECT_NAME, project.getName());
        assertEquals("Project scope id was not updated!", "123456", project.getScopeId());
        assertEquals("Project remote project manager was not updated!", TEST_REMOTE_PROJECT_MANAGER_NAME, project.getRemoteProjectManager().getName());
        assertEquals("Project remote project manager settings were not updated!", "https://localhost:9101/TexasAMLibrary", project.getRemoteProjectManager().getSettings().get("url"));
    }

    @Test
    public void testDelete() {
        Project project = projectRepo.create(new Project(TEST_ALTERNATE_PROJECT_NAME));
        assertEquals("Project not created!", 1, projectRepo.count());
        projectRepo.delete(project);
        assertEquals("Project was not deleted!", 0, projectRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicate() {
        projectRepo.create(new Project(TEST_PROJECT_NAME));
        projectRepo.create(new Project(TEST_PROJECT_NAME));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testNameNotNull() {
        projectRepo.create(new Project(null));
    }

    @Test
    public void testSetRemoteProjectManager() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME, ServiceType.VERSION_ONE, getMockSettings()));

        Project project = projectRepo.create(new Project(TEST_PROJECT_NAME, "1000", remoteProjectManager));
        assertEquals("Project has the incorrect name!", TEST_PROJECT_NAME, project.getName());
        assertEquals("Project has the incorrect Remote Project Manager name!", TEST_REMOTE_PROJECT_MANAGER_NAME, project.getRemoteProjectManager().getName());
        assertEquals("Project has the incorrect Remote Project Manager url setting value!", "https://localhost:9101/TexasAMLibrary", project.getRemoteProjectManager().getSettings().get("url"));

        projectRepo.delete(project);

        assertEquals("Project repo had incorrect number of projects!", 0, projectRepo.count());
        assertEquals("Remote project manager was deleted when project was deleted!", 1, remoteProjectManagerRepo.count());
    }

}
