package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProjectApplication;
import edu.tamu.app.model.repo.ProjectRepo;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ProjectTest {

    private static final String TEST_PROJECT_NAME = "Test Project Name";

    private static final String TEST_ALTERNATE_PROJECT_NAME = "Alternate Project Name";

    @Autowired
    private ProjectRepo projectRepo;

    @Test
    public void testCreate() {
        long initalCount = projectRepo.count();
        projectRepo.create(new Project(TEST_PROJECT_NAME));
        assertEquals("The number of Projects did not increase by one", initalCount + 1, projectRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testNameNotNull() {
        projectRepo.create(new Project(null));
    }

    @Test
    public void testUpdateName() {
        Project project = projectRepo.create(new Project(TEST_PROJECT_NAME));
        project.setName(TEST_ALTERNATE_PROJECT_NAME);
        projectRepo.save(project);
        project = projectRepo.findOne(project.getId());
        assertEquals("Project name was not changed", TEST_ALTERNATE_PROJECT_NAME, project.getName());
    }

    @Test
    public void testDelete() {
        long initialCount = projectRepo.count();
        Project project = projectRepo.create(new Project(TEST_ALTERNATE_PROJECT_NAME));
        assertEquals("Project not created", initialCount + 1, projectRepo.count());
        projectRepo.delete(project);
        assertEquals("Project was not deleted", initialCount, projectRepo.count());
    }

    @After
    public void cleanUp() {
        projectRepo.deleteAll();
    }

}
