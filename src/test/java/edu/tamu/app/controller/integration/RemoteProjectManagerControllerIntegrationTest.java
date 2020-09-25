package edu.tamu.app.controller.integration;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.service.manager.GitHubService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@RunWith(SpringRunner.class)
public class RemoteProjectManagerControllerIntegrationTest extends IntegrationTest {

    private static long currentId = 0L;

    private static final String TEST_REMOTE_PROJECT_MANAGER_NAME = "Remote Project Manager";
    private static final String TEST_REMOTE_PROJECT_MANAGER_URL = "http://localhost/";
    private static final String TEST_REMOTE_PROJECT_MANAGER_TOKEN = "0987654321";

    private static final ServiceType TEST_REMOTE_PROJECT_MANAGER_SERVICE_TYPE = ServiceType.GITHUB;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @MockBean
    protected GitHubService gitHubService;

    @MockBean
    protected VersionOneService versionOneService;

    @MockBean
    protected GitHubBuilder ghBuilder;

    @MockBean
    private ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService;

    @MockBean
    private ProductsStatsScheduledCacheService productsStatsScheduledCacheService;

    @MockBean
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    // @After and @Before cannot be safely specified inside a parent class.
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockGitHubService(gitHubService, ghBuilder);
        mockVersionOneService(versionOneService);
        mockActiveSprintsScheduledCacheService(activeSprintsScheduledCacheService);
        mockProductsStatsScheduledCacheService(productsStatsScheduledCacheService);
        mockRemoteProjectsScheduledCacheService(remoteProjectsScheduledCacheService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetRemoteProjectManagers() throws JsonProcessingException, Exception {
        performCreateRemoteProjectManager();

        // @formatter:off
        mockMvc.perform(
            get("/status")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "remote-project-manager/get-all",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the List of RemoteProjectManager.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetRemoteProjectManagerById() throws JsonProcessingException, Exception {
        performCreateRemoteProjectManager();

        // @formatter:off
        mockMvc.perform(
            get("/remote-project-manager/{id}", currentId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "remote-project-manager/get",
                    pathParameters(
                        parameterWithName("id").description("The Remote Project Manager ID.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the RemoteProjectManager."),
                        fieldWithPath("payload.RemoteProjectManager.id").description("The RemoteProjectManager ID."),
                        fieldWithPath("payload.RemoteProjectManager.name").description("The RemoteProjectManager name."),
                        fieldWithPath("payload.RemoteProjectManager.type").description("The type of RemoteProjectManager."),
                        fieldWithPath("payload.RemoteProjectManager.url").description("The URL of the remote project."),
                        fieldWithPath("payload.RemoteProjectManager.token").description("The authentication token to access the remote project.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateRemoteProjectManager() throws JsonProcessingException, Exception {
        // @formatter:off
        performCreateRemoteProjectManager()
            .andDo(
                document(
                    "remote-project-manager/create",
                    requestFields(
                        fieldWithPath("id").description("The RemoteProjectManager ID."),
                        fieldWithPath("name").description("The RemoteProjectManager name."),
                        fieldWithPath("type").description("The type of RemoteProjectManager."),
                        fieldWithPath("url").description("The URL of the remote project."),
                        fieldWithPath("token").description("The authentication token to access the remote project.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the RemoteProjectManager."),
                        fieldWithPath("payload.RemoteProjectManager.id").description("The RemoteProjectManager ID."),
                        fieldWithPath("payload.RemoteProjectManager.name").description("The RemoteProjectManager name."),
                        fieldWithPath("payload.RemoteProjectManager.type").description("The type of RemoteProjectManager."),
                        fieldWithPath("payload.RemoteProjectManager.url").description("The URL of the remote project."),
                        fieldWithPath("payload.RemoteProjectManager.token").description("The authentication token to access the remote project.")
                    )
                )
            );
       // @formatter:on
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateRemoteProjectManager() throws JsonProcessingException, Exception {
        performCreateRemoteProjectManager();

        RemoteProjectManager rpm = remoteProjectManagerRepo.findOne(currentId);
        rpm.setName("Updated Name");

        ApiResponse expectedResponse = new ApiResponse(ApiStatus.SUCCESS, rpm);

        // @formatter:off
        mockMvc.perform(
            put("/remote-project-manager")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(rpm))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse))
            ).andDo(
                document(
                    "remote-project-manager/update",
                    requestFields(
                        fieldWithPath("id").description("The RemoteProjectManager ID."),
                        fieldWithPath("name").description("The RemoteProjectManager name."),
                        fieldWithPath("type").description("The type of RemoteProjectManager."),
                        fieldWithPath("url").description("The URL of the remote project."),
                        fieldWithPath("token").description("The authentication token to access the remote project.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the RemoteProjectManager."),
                        fieldWithPath("payload.RemoteProjectManager.id").description("The RemoteProjectManager ID."),
                        fieldWithPath("payload.RemoteProjectManager.name").description("The RemoteProjectManager name."),
                        fieldWithPath("payload.RemoteProjectManager.type").description("The type of RemoteProjectManager."),
                        fieldWithPath("payload.RemoteProjectManager.url").description("The URL of the remote project."),
                        fieldWithPath("payload.RemoteProjectManager.token").description("The authentication token to access the remote project.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteRemoteProjectManager() throws JsonProcessingException, Exception {
        performCreateRemoteProjectManager();

        RemoteProjectManager rpm = remoteProjectManagerRepo.findOne(currentId);

        // @formatter:off
        mockMvc.perform(
            delete("/remote-project-manager")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(rpm))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "remote-project-manager/delete",
                    requestFields(
                        fieldWithPath("id").description("The RemoteProjectManager ID."),
                        fieldWithPath("name").description("The RemoteProjectManager name."),
                        fieldWithPath("type").description("The type of RemoteProjectManager."),
                        fieldWithPath("url").description("The URL of the remote project."),
                        fieldWithPath("token").description("The authentication token to access the remote project.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("Empty API response payload.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRemoteProjectManagerTypes() throws Exception {
        // @formatter:off
        mockMvc.perform(
            get("/remote-project-manager/types")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "remote-project-manager/types",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the array of Service Types."),
                        fieldWithPath("payload['ArrayList<HashMap>']").description("The array of Service Types.")
                    )
                )
            );
       // @formatter:on
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @After
    public void cleanup() {
        cleanupRepos();
    }

    private ResultActions performCreateRemoteProjectManager() throws JsonProcessingException, Exception {
        RemoteProjectManager rpm = new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME, TEST_REMOTE_PROJECT_MANAGER_SERVICE_TYPE, TEST_REMOTE_PROJECT_MANAGER_URL, TEST_REMOTE_PROJECT_MANAGER_TOKEN);
        rpm.setId(++currentId);

        // FIXME: why does this fail?
        //ApiResponse expectedResponse = new ApiResponse(ApiStatus.SUCCESS, rpm);

        // @formatter:off
        return mockMvc.perform(
            post("/remote-project-manager")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(rpm))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk());
            //.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            //.andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
        // @formatter:on
    }

}
