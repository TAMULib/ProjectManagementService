package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.versionone.apiclient.exceptions.APIException;
import com.versionone.apiclient.exceptions.ConnectionException;
import com.versionone.apiclient.exceptions.OidException;

import edu.tamu.app.cache.service.ProjectScheduledCache;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.ticketing.SugarService;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class ProjectControllerUnitTest {

    private static final String TEST_PROJECT1_NAME = "Test Project 1 Name";
    private static final String TEST_PROJECT1_SCOPE = "0001";
    private static final String TEST_PROJECT2_NAME = "Test Project 2 Name";
    private static final String TEST_PROJECT2_SCOPE = "0002";
    private static final String TEST_MODIFIED_PROJECT_NAME = "Modified Project Name";
    private static final String TEST_FEATURE_REQUEST_TITLE = "Test Feature Request Title";
    private static final String TEST_FEATURE_REQUEST_DESCRIPTION = "Test Feature Request Description";
    private static final String TEST_PROJECT_WITHOUT_RPM_NAME = "Test Project Without Remote Project Manager Name";

    private static final String PUSH_ERROR_MESSAGE = "Error pushing request to Test Remote Project Manager for project Test Project 1 Name!";
    private static final String NO_RPM_ERROR_MESSAGE = "Test Project Without Remote Project Manager Name project does not have a Remote Project Manager!";
    private static final String NO_PROJECT_ERROR_MESSAGE = "Project with id null not found!";
    private static final String INVALID_RPM_ID_ERROR_MESSAGE = "Error fetching remote projects from Test Remote Project Manager!";
    private static final String MISSING_RPM_ERROR_MESSAGE = "Remote Project Manager with id null not found!";
    private static final String INVALID_RPM_ID_ERROR_MESSAGE_FIND_BY_ID = "Error fetching remote project with scope id null from Test Remote Project Manager!";

    private static final RemoteProjectManager TEST_PROJECT1_REMOTE_PROJECT_MANAGER = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE, new HashMap<String, String>());

    private static Project TEST_PROJECT1 = new Project(TEST_PROJECT1_NAME, TEST_PROJECT1_SCOPE, TEST_PROJECT1_REMOTE_PROJECT_MANAGER);
    private static Project TEST_PROJECT2 = new Project(TEST_PROJECT2_NAME);
    private static Project TEST_MODIFIED_PROJECT = new Project(TEST_MODIFIED_PROJECT_NAME);
    private static Project TEST_PROJECT_WIHTOUT_RPM = new Project(TEST_PROJECT_WITHOUT_RPM_NAME);

    private static TicketRequest TEST_TICKET_REQUEST = new TicketRequest();
    private static FeatureRequest TEST_INVALID_FEATURE_REQUEST = new FeatureRequest(TEST_FEATURE_REQUEST_TITLE, TEST_FEATURE_REQUEST_DESCRIPTION, TEST_PROJECT1.getId(), TEST_PROJECT1_SCOPE);
    private static FeatureRequest TEST_FEATURE_REQUEST_WIHTOUT_VMS = new FeatureRequest(TEST_FEATURE_REQUEST_TITLE, TEST_FEATURE_REQUEST_DESCRIPTION, TEST_PROJECT_WIHTOUT_RPM.getId(), TEST_PROJECT2_SCOPE);
    private static FeatureRequest TEST_FEATURE_REQUEST_WITHOUT_PROJECT = new FeatureRequest();
    private static List<Project> mockProjectList = new ArrayList<Project>(Arrays.asList(new Project[] { TEST_PROJECT1, TEST_PROJECT2 }));

    private static ApiResponse apiResponse;

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Mock
    private SugarService sugarService;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private RemoteProjectManagerBean managementBean;

    @Spy
    private List<ProjectScheduledCache<?, ?>> projectSceduledCaches = new ArrayList<ProjectScheduledCache<?, ?>>();

    @InjectMocks
    private ProjectController projectController;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(projectRepo.findAll()).thenReturn(mockProjectList);
        when(projectRepo.create(any(Project.class))).thenReturn(TEST_PROJECT1);
        when(projectRepo.findOne(any(Long.class))).thenReturn(null);
        when(projectRepo.update(any(Project.class))).thenReturn(TEST_MODIFIED_PROJECT);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_PROJECT1_REMOTE_PROJECT_MANAGER);
        doNothing().when(projectRepo).delete(any(Project.class));
        when(sugarService.submit(any(TicketRequest.class))).thenReturn("Successfully submitted issue for test service!");
        when(managementBean.push(TEST_INVALID_FEATURE_REQUEST)).thenThrow(Exception.class);
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
        when(projectRepo.findOne(any(Long.class))).thenReturn(TEST_PROJECT1);
        apiResponse = projectController.getOne(TEST_PROJECT1.getId());
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
    public void testSubmitIssueRequest() {
        apiResponse = projectController.submitIssueRequest(TEST_TICKET_REQUEST);
        assertEquals("Not successful at submitting issue request", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testPushRequest() throws JsonProcessingException, IOException {

    }

    @Test
    public void testPushRequestToInvalidRemoteProjectManager() {
        when(projectRepo.findOne(any(Long.class))).thenReturn(TEST_PROJECT1);
        apiResponse = projectController.pushRequest(null, TEST_INVALID_FEATURE_REQUEST);
        assertEquals("Invalid push did not throw an exception", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Push without Remote Project Manager did not result in the expected error", PUSH_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testPushRequestWithoutRemoteProjectManager() {
        when(projectRepo.findOne(any(Long.class))).thenReturn(TEST_PROJECT_WIHTOUT_RPM);
        apiResponse = projectController.pushRequest(null, TEST_FEATURE_REQUEST_WIHTOUT_VMS);
        assertEquals("Push without Remote Project Manager did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Push without Remote Project Manager did not result in the expected error", NO_RPM_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testPushRequestWithoutProject() {
        apiResponse = projectController.pushRequest(null, TEST_FEATURE_REQUEST_WITHOUT_PROJECT);
        assertEquals("Push without Project did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Push without Project did not result in the expected error", NO_PROJECT_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetAllRemoteProjects() throws JsonProcessingException, IOException, ConnectionException, APIException, OidException {

    }

    @Test
    public void testGetAllRemoteProjectsWithInvalidRemoteProjectManager() {
        apiResponse = projectController.getAllRemoteProjects(TEST_PROJECT1_REMOTE_PROJECT_MANAGER.getId());
        assertEquals("Request with invalid Remote Project Manager id did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Invalid Remote Project Manager id did not result in the expected error message", INVALID_RPM_ID_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetAllRemoteProjectesWithNoRemoteProjectManager() {
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(null);
        apiResponse = projectController.getAllRemoteProjects(TEST_PROJECT1_REMOTE_PROJECT_MANAGER.getId());
        assertEquals("Request without Remote Project Manager did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Missing Remote Project Manager did not result in the expected error message", MISSING_RPM_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetRemoteProjectByScopeId() throws JsonProcessingException, IOException, ConnectionException, APIException, OidException {

    }

    @Test
    public void testGetRemoteProjectByScopeIdWithInvalidRemoteProjectManager() {
        apiResponse = projectController.getRemoteProjectByScopeId(TEST_PROJECT1_REMOTE_PROJECT_MANAGER.getId(), null);
        assertEquals("Request with invalid Remote Project Manager id did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Invalid Remote Project Manager id did not result in the expected error message", INVALID_RPM_ID_ERROR_MESSAGE_FIND_BY_ID, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetRemoteProjectByScopeIdWithMissingRemoteProjectManager() {
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(null);
        apiResponse = projectController.getRemoteProjectByScopeId(TEST_PROJECT1_REMOTE_PROJECT_MANAGER.getId(), null);
        assertEquals("Request with no Remote Project Manager did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Missing Remote Project Manager did not result in the expected error message", MISSING_RPM_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

}