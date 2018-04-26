package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProjectApplication;
import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.VersionManagementSoftwareRepo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ProjectTest {

    private static final String TEST_PROJECT_NAME = "Test Project Name";

    private static final String TEST_VERSION_MANAGEMENT_SOFTWARE_NAME = "Test Version Management Software";

    private static final String TEST_ALTERNATE_PROJECT_NAME = "Alternate Project Name";

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private VersionManagementSoftwareRepo versionManagementSoftwareRepo;

    @Test
    public void testCreate() {
        long initalCount = projectRepo.count();
        projectRepo.create(new Project(TEST_PROJECT_NAME));
        assertEquals("The number of Projects did not increase by one", initalCount + 1, projectRepo.count());
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
    public void testUpdateName() {
        Project project = projectRepo.create(new Project(TEST_PROJECT_NAME));
        project.setName(TEST_ALTERNATE_PROJECT_NAME);
        projectRepo.save(project);
        project = projectRepo.findOne(project.getId());
        assertEquals("Project name was not changed", TEST_ALTERNATE_PROJECT_NAME, project.getName());
    }

    @Test
    public void testAssociateVersionManagementSoftware() {
        Map<String, String> settings = new HashMap<String, String>() {
            private static final long serialVersionUID = 2020874481642498006L;
            {
                put("url", "https://localhost:9101/TexasAMLibrary");
                put("username", "username");
                put("password", "password");
            }
        };

        VersionManagementSoftware versionManagementSoftware = versionManagementSoftwareRepo.create(new VersionManagementSoftware(TEST_VERSION_MANAGEMENT_SOFTWARE_NAME, ServiceType.VERSION_ONE, settings));

        Project project = projectRepo.create(new Project(TEST_PROJECT_NAME, "1000", versionManagementSoftware));
        project.setName(TEST_ALTERNATE_PROJECT_NAME);
        project = projectRepo.save(project);
        assertEquals("Project has the incorrect name!", TEST_ALTERNATE_PROJECT_NAME, project.getName());
        assertEquals("Project has the incorrect version management software name!", TEST_VERSION_MANAGEMENT_SOFTWARE_NAME, project.getVersionManagementSoftware().getName());
        assertEquals("Project has the incorrect version management software url setting value!", "https://localhost:9101/TexasAMLibrary", project.getVersionManagementSoftware().getSettings().get("url"));
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
        versionManagementSoftwareRepo.deleteAll();
    }

}
