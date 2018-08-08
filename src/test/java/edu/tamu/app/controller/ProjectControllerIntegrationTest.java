package edu.tamu.app.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Before
    public void setup() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager("VersionTwo", ServiceType.VERSION_ONE, new HashMap<String, String>() {
            private static final long serialVersionUID = 2020874481642498006L;
            {
                put("url", "https://localhost:9101/TexasAMLibrary");
                put("username", "username");
                put("password", "password");
            }
        }));
        Project project = projectRepo.create(new Project("Test"));
        project.setScopeId("123456");
        project.setRemoteProjectManager(remoteProjectManager);
        project = projectRepo.update(project);
    }

    @Test
    public void testGetProjects() throws Exception {
        // @formatter:off
        mockMvc.perform(get("/projects").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("meta.status", equalTo("SUCCESS")))
            .andExpect(jsonPath("payload.ArrayList<Project>[0].id", equalTo(1)))
            .andExpect(jsonPath("payload.ArrayList<Project>[0].name", equalTo("Test")))
            .andExpect(jsonPath("payload.ArrayList<Project>[0].scopeId", equalTo("123456")))
            .andExpect(jsonPath("payload.ArrayList<Project>[0].remoteProjectManager.id", equalTo(1)))
            .andExpect(jsonPath("payload.ArrayList<Project>[0].remoteProjectManager.name", equalTo("VersionTwo")))
            .andExpect(jsonPath("payload.ArrayList<Project>[0].remoteProjectManager.type", equalTo("VERSION_ONE")))
            .andExpect(jsonPath("payload.ArrayList<Project>[0].remoteProjectManager.settings").doesNotExist());
        // @formatter:on
    }

    @Test
    public void testGetProjectById() throws Exception {
     // @formatter:off
        mockMvc.perform(get("/projects/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("meta.status", equalTo("SUCCESS")))
            .andExpect(jsonPath("payload.Project.id", equalTo(2)))
            .andExpect(jsonPath("payload.Project.name", equalTo("Test")))
            .andExpect(jsonPath("payload.Project.scopeId", equalTo("123456")))
            .andExpect(jsonPath("payload.Project.remoteProjectManager.id", equalTo(2)))
            .andExpect(jsonPath("payload.Project.remoteProjectManager.name", equalTo("VersionTwo")))
            .andExpect(jsonPath("payload.Project.remoteProjectManager.type", equalTo("VERSION_ONE")))
            .andExpect(jsonPath("payload.Project.remoteProjectManager.settings").doesNotExist());
        // @formatter:on
    }

    @After
    public void cleanup() {
        projectRepo.deleteAll();
        remoteProjectManagerRepo.deleteAll();
    }

}
