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
import edu.tamu.app.model.request.ProjectRequest;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.versioning.VersionOneService;
import edu.tamu.weaver.auth.model.Credentials;
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

    private static ApiResponse response;

    @Mock
    protected static Credentials credentials;

    @Mock
    protected ProjectRepo projectRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @InjectMocks
    protected ProjectController projectController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(credentials.getUin()).thenReturn("123456789");
        when(projectRepo.findAll()).thenReturn(mockProjectList);
        when(projectRepo.create(any(Project.class))).thenReturn(TEST_PROJECT1);
        when(projectRepo.findOne(any(Long.class))).thenReturn(TEST_PROJECT1);
        when(projectRepo.update(any(Project.class))).thenReturn(TEST_MODIFIED_PROJECT);
        doNothing().when(projectRepo).delete(any(Project.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetAllProjects() {
        response = projectController.getAll();
        assertEquals("Not successful at getting requested Project", SUCCESS, response.getMeta().getStatus());
        List<Project> projects = (List<Project>) response.getPayload().get("ArrayList<Project>");
        assertEquals("Did not get the expected Projects", mockProjectList, projects);
    }

    @Test
    public void testGetProjectById() {
        response = projectController.getOne(1L);
        assertEquals("Not successful at getting requested Project", SUCCESS, response.getMeta().getStatus());
        Project project = (Project) response.getPayload().get("Project");
        assertEquals("Did not get the expected Project", TEST_PROJECT1, project);
    }

    @Test
    public void testCreate() {
        response = projectController.createProject(TEST_PROJECT1);
        assertEquals("Not successful at creating Project", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdate() {
        response = projectController.updateProject(TEST_MODIFIED_PROJECT);
        assertEquals("Not successful at updating Project", SUCCESS, response.getMeta().getStatus());
        Project project = (Project) response.getPayload().get("Project");
        assertEquals("Project title was not properly updated", TEST_MODIFIED_PROJECT.getName(), project.getName());
    }

    @Test
    public void testDelete() {
        response = projectController.deleteProject(TEST_PROJECT1);
        assertEquals("Not successful at deleting Project", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testPushRequest() throws JsonProcessingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode expectedResponse = objectMapper.readTree(new ClassPathResource("mock/response.json").getInputStream());
        VersionOneService versionOneService = mock(VersionOneService.class);
        when(versionOneService.push(any(ProjectRequest.class))).thenReturn(expectedResponse);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);
        ProjectRequest request = new ProjectRequest("Test Request", "This is only a test!", 1L, "7869");
        response = projectController.pushRequest(request);
        assertEquals("Pushing request was not successful!", SUCCESS, response.getMeta().getStatus());
        JsonNode actualResponse = objectMapper.convertValue(response.getPayload().get("ObjectNode"), JsonNode.class);
        assertEquals("Response of push to version one not as expected!", expectedResponse, actualResponse);
    }

}
