package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiResponse;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class ProjectControllerTest {

    protected static User TEST_USER1 = new User("123456789");
    
    protected static final String TEST_PROJECT_NAME1 = "Test Project Name 1";
    protected static final String TEST_PROJECT_NAME2 = "Test Project Name 2";
    protected static final String TEST_MODIFIED_PROJECT_NAME = "Modified Project Name";
    
    protected static Project TEST_PROJECT1 = new Project(TEST_PROJECT_NAME1);
    protected static Project TEST_PROJECT2 = new Project(TEST_PROJECT_NAME2);
    protected static Project TEST_MODIFIED_PROJECT = new Project(TEST_MODIFIED_PROJECT_NAME);
    protected static List<Project> mockProjectList = new ArrayList<Project>(Arrays.asList(new Project[] {TEST_PROJECT1, TEST_PROJECT2}));
    
    protected static ApiResponse response;
    
    @Mock
    protected static Credentials credentials;
    
    @Mock
    protected ProjectRepo projectRepo;
    
    @InjectMocks
    protected ProjectController projectController;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(credentials.getUin()).thenReturn("123456789");
        when(projectRepo.findAll()).thenReturn(mockProjectList);
        when(projectRepo.create(any(Project.class))).thenReturn(TEST_PROJECT1);
        when(projectRepo.update(any(Project.class))).thenReturn(TEST_MODIFIED_PROJECT);
        doNothing().when(projectRepo).delete(any(Project.class));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testGetAllProjects() {
        response = projectController.getAll(TEST_USER1);
        assertEquals("Not successful at getting requested Project", SUCCESS, response.getMeta().getStatus());
        List<Project> projects = (List<Project>) response.getPayload().get("ArrayList<Project>");
        assertEquals("Did not get the expected Projects", mockProjectList, projects);
    }
    
    @Test
    public void testCreate() {
        response = projectController.createProject(TEST_PROJECT1);
        assertEquals("Not successful at creating Project", SUCCESS, response.getMeta().getStatus());
    }
    
    @Test
    public void testUpdate() {
        response = projectController.updateProject(TEST_MODIFIED_PROJECT);
        assertEquals("Note successful at updating Project", SUCCESS, response.getMeta().getStatus());
        Project project = (Project) response.getPayload().get("Project");
        assertEquals("Project title was not properly updated", TEST_MODIFIED_PROJECT.getName(), project.getName());
    }
    
    @Test
    public void testDelete() {
        response = projectController.deleteProject(TEST_PROJECT1);
        assertEquals("Not successful at deleting Project", SUCCESS, response.getMeta().getStatus());
    }
}
