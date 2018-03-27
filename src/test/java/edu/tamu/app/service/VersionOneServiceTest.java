package edu.tamu.app.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.ProjectApplication;
import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.request.ProjectRequest;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.versioning.VersionOneService;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class VersionOneServiceTest {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testPush() throws IOException {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("url", "http://localhost:9101/TexasAMLibrary");
        settings.put("username", "username");
        settings.put("password", "password");

        VersionManagementSoftware versionManagementSoftware = new VersionManagementSoftware("Version One", ServiceType.VERSION_ONE, settings);

        Project project = projectRepo.create(new Project("Cap", "7869", versionManagementSoftware));

        managementBeanRegistry.register(project, versionManagementSoftware);

        VersionOneService versionOneService = (VersionOneService) managementBeanRegistry.getService(versionManagementSoftware.getName());

        ProjectRequest request = new ProjectRequest("Test Request", "This is only a test!", 1L, "7869");

        JsonNode actualResponse = objectMapper.convertValue(versionOneService.push(request), JsonNode.class);

        JsonNode expectedResponse = objectMapper.readTree(new ClassPathResource("mock/response.json").getInputStream());

        assertEquals("Response of push to version one not as expected!", expectedResponse, actualResponse);
    }

    @After
    public void cleanup() {
        projectRepo.deleteAll();
    }

}
