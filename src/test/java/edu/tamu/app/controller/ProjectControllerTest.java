package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.ManagementSetting;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.VersionManagementSoftwareRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.response.VersionProject;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.versioning.VersionOneService;
import edu.tamu.app.utility.JsonNodeUtility;
import edu.tamu.weaver.response.ApiResponse;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class ProjectControllerTest {

    private static final String TEST_PROJECT1_NAME = "Test Project 1 Name";
    private static final String TEST_PROJECT1_SCOPE = "1000";
    private static final String TEST_PROJECT2_NAME = "Test Project 2 Name";
    private static final String TEST_MODIFIED_PROJECT_NAME = "Modified Project Name";

    private static final VersionManagementSoftware TEST_PROJECT1_VERSION_MANAGERMENT_SOFTWARE = new VersionManagementSoftware("Test Version Management Software", ServiceType.VERSION_ONE, new ArrayList<ManagementSetting>());

    private static Project TEST_PROJECT1 = new Project(TEST_PROJECT1_NAME, TEST_PROJECT1_SCOPE, TEST_PROJECT1_VERSION_MANAGERMENT_SOFTWARE);

    private static Project TEST_PROJECT2 = new Project(TEST_PROJECT2_NAME);

    private static Project TEST_MODIFIED_PROJECT = new Project(TEST_MODIFIED_PROJECT_NAME);

    private static List<Project> mockProjectList = new ArrayList<Project>(Arrays.asList(new Project[] { TEST_PROJECT1, TEST_PROJECT2 }));

    private static ApiResponse apiResponse;

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private VersionManagementSoftwareRepo versionManagementSoftwareRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @InjectMocks
    private ProjectController projectController;

    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(projectRepo.findAll()).thenReturn(mockProjectList);
        when(projectRepo.create(any(Project.class))).thenReturn(TEST_PROJECT1);
        when(projectRepo.findOne(any(Long.class))).thenReturn(TEST_PROJECT1);
        when(projectRepo.update(any(Project.class))).thenReturn(TEST_MODIFIED_PROJECT);
        when(versionManagementSoftwareRepo.findOne(any(Long.class))).thenReturn(TEST_PROJECT1_VERSION_MANAGERMENT_SOFTWARE);
        doNothing().when(projectRepo).delete(any(Project.class));
        objectMapper = new ObjectMapper();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetAllProjects() {
        apiResponse = projectController.getAll();
        assertEquals("Not successful at getting requested Project", SUCCESS, apiResponse.getMeta().getStatus());
        List<Project> projects = (List<Project>) apiResponse.getPayload().get("ArrayList<Project>");
        assertEquals("Did not get the expected Projects", mockProjectList, projects);
    }

    @Test
    public void testGetProjectById() {
        apiResponse = projectController.getOne(1L);
        assertEquals("Not successful at getting requested Project", SUCCESS, apiResponse.getMeta().getStatus());
        Project project = (Project) apiResponse.getPayload().get("Project");
        assertEquals("Did not get the expected Project", TEST_PROJECT1, project);
    }

    @Test
    public void testCreate() {
        apiResponse = projectController.createProject(TEST_PROJECT1);
        assertEquals("Not successful at creating Project", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testUpdate() {
        apiResponse = projectController.updateProject(TEST_MODIFIED_PROJECT);
        assertEquals("Not successful at updating Project", SUCCESS, apiResponse.getMeta().getStatus());
        Project project = (Project) apiResponse.getPayload().get("Project");
        assertEquals("Project title was not properly updated", TEST_MODIFIED_PROJECT.getName(), project.getName());
    }

    @Test
    public void testDelete() {
        apiResponse = projectController.deleteProject(TEST_PROJECT1);
        assertEquals("Not successful at deleting Project", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testPushRequest() throws JsonProcessingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expectedResponse = getExpectedResponse("mock/response.json");
        VersionOneService versionOneService = mock(VersionOneService.class);
        when(versionOneService.push(any(FeatureRequest.class))).thenReturn(expectedResponse);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);
        FeatureRequest request = new FeatureRequest("Test Request", "This is only a test!", 1L, "7869");
        apiResponse = projectController.pushRequest(request);
        assertEquals("Pushing request was not successful!", SUCCESS, apiResponse.getMeta().getStatus());
        JsonNode actualResponse = objectMapper.convertValue(apiResponse.getPayload().get("ObjectNode"), JsonNode.class);
        assertEquals("Response of push to version one not as expected!", expectedResponse, actualResponse);
    }

    @Test
    public void testGetAllVersionProjects() throws JsonProcessingException, IOException {
        JsonNode expectedResponse = getExpectedResponse("mock/projects.json");
        List<VersionProject> projects = JsonNodeUtility.getVersionProjects(expectedResponse.get("Assets"));
        VersionOneService versionOneService = mock(VersionOneService.class);
        when(versionOneService.getVersionProjects()).thenReturn(projects);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);
        apiResponse = projectController.getAllVersionProjects(1L);
        assertEquals("Get all version projects was not successful!", SUCCESS, apiResponse.getMeta().getStatus());
        JsonNode assets = expectedResponse.get("Assets");
        for (int i = 0; i < projects.size(); i++) {
            assertVersionProject(projects.get(i), assets.get(i));
        }
    }

    @Test
    public void testGetVersionProjectByScopeId() throws JsonProcessingException, IOException {
        JsonNode asset = getExpectedResponse("mock/project.json");
        String name = JsonNodeUtility.getVersionProjectName(asset);
        VersionProject project = new VersionProject(name, "7869");
        VersionOneService versionOneService = mock(VersionOneService.class);
        when(versionOneService.getVersionProjectByScopeId(any(String.class))).thenReturn(project);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);
        apiResponse = projectController.getVersionProjectByScopeId(1L, "7869");
        assertEquals("Get version project by scope id was not successful!", SUCCESS, apiResponse.getMeta().getStatus());
        assertVersionProject(project, asset);
    }

    private JsonNode getExpectedResponse(String path) throws JsonProcessingException, IOException {
        return objectMapper.readTree(new ClassPathResource(path).getInputStream());
    }

    private void assertVersionProject(VersionProject project, JsonNode asset) {
        String name = JsonNodeUtility.getVersionProjectName(asset);
        String scopeId = JsonNodeUtility.getVersionProjectScopeId(asset);
        assertEquals("Version project had the incorrect name!", name, project.getName());
        assertEquals("Version project had the incorrect scope id!", scopeId, project.getScopeId());
    }

}
