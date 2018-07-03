package edu.tamu.app.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.ProjectApplication;
import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.VersionManagementSoftwareRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.versioning.VersionOneService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class VersionOneServiceTest {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private VersionManagementSoftwareRepo versionManagementSoftwareRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private VersionManagementSoftware versionManagementSoftware;

    private VersionOneService versionOneService;

    private FeatureRequest request;

    @Before
    public void setup() {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("url", "http://localhost:9101/TexasAMLibrary");
        settings.put("username", "username");
        settings.put("password", "password");
        versionManagementSoftware = versionManagementSoftwareRepo.create(new VersionManagementSoftware("Version One", ServiceType.VERSION_ONE, settings));
        managementBeanRegistry.register(versionManagementSoftware);
        versionOneService = (VersionOneService) managementBeanRegistry.getService(versionManagementSoftware.getName());
        request = new FeatureRequest("Test Request", "This is only a test!", 1L, "7869");
    }

    @Test
    public void testPush() throws Exception {
        JsonNode actualResponse = objectMapper.convertValue(versionOneService.push(request), JsonNode.class);
        JsonNode expectedResponse = objectMapper.readTree(new ClassPathResource("mock/response.json").getInputStream());
        assertEquals("Response of push to version one not as expected!", expectedResponse, actualResponse);
    }

//    @Test
//    public void testGetVersionProjects() throws Exception {
//        List<VersionProject> projects = versionOneService.getVersionProjects();
//        JsonNode expectedResponse = objectMapper.readTree(new ClassPathResource("mock/projects.json").getInputStream());
//        JsonNode assets = expectedResponse.get("Assets");
//        for (int i = 0; i < projects.size(); i++) {
//            assertVersionProject(projects.get(i), assets.get(i));
//        }
//    }

//    @Test
//    public void testGetVersionProjectByScopeId() throws Exception {
//        VersionProject project = versionOneService.getVersionProjectByScopeId("7869");
//        JsonNode asset = objectMapper.readTree(new ClassPathResource("mock/project.json").getInputStream());
//        assertVersionProject(project, asset);
//    }

    @After
    public void cleanup() {
        projectRepo.deleteAll();
        versionManagementSoftwareRepo.deleteAll();
    }

}
