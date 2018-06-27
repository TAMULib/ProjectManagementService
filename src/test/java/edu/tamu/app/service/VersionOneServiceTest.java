package edu.tamu.app.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.response.RemoteProject;
import edu.tamu.app.service.managing.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.utility.VersionOneJsonNodeUtility;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class VersionOneServiceTest {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private RemoteProjectManager remoteProjectManager;

    private VersionOneService versionOneService;

    private FeatureRequest request;

    @Before
    public void setup() {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("url", "http://localhost:9101/TexasAMLibrary");
        settings.put("username", "username");
        settings.put("password", "password");
        remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager("VersionOne", ServiceType.VERSION_ONE, settings));
        managementBeanRegistry.register(remoteProjectManager);
        versionOneService = (VersionOneService) managementBeanRegistry.getService(remoteProjectManager.getName());
        request = new FeatureRequest("Test Request", "This is only a test!", 1L, "7869");
    }

    @Test
    public void testPush() throws IOException {
        JsonNode actualResponse = objectMapper.convertValue(versionOneService.push(request), JsonNode.class);
        JsonNode expectedResponse = objectMapper.readTree(new ClassPathResource("mock/response.json").getInputStream());
        assertEquals("Response of push to VersionOne not as expected!", expectedResponse, actualResponse);
    }

    @Test
    public void testGetVersionProjects() throws IOException {
        List<RemoteProject> projects = versionOneService.getRemoteProjects();
        JsonNode expectedResponse = objectMapper.readTree(new ClassPathResource("mock/projects.json").getInputStream());
        JsonNode assets = expectedResponse.get("Assets");
        for (int i = 0; i < projects.size(); i++) {
            assertVersionProject(projects.get(i), assets.get(i));
        }
    }

    @Test
    public void testGetVersionProjectByScopeId() throws IOException {
        RemoteProject project = versionOneService.getRemoteProjectByScopeId("7869");
        JsonNode asset = objectMapper.readTree(new ClassPathResource("mock/project.json").getInputStream());
        assertVersionProject(project, asset);
    }

    private void assertVersionProject(RemoteProject project, JsonNode asset) {
        String name = VersionOneJsonNodeUtility.getRemoteProjectName(asset);
        String scopeId = VersionOneJsonNodeUtility.getRemoteProjectScopeId(asset);
        assertEquals("Remote project had the incorrect name!", name, project.getName());
        assertEquals("Remote project had the incorrect scope id!", scopeId, project.getScopeId());
    }

    @After
    public void cleanup() {
        projectRepo.deleteAll();
        remoteProjectManagerRepo.deleteAll();
    }

}