package edu.tamu.app.controller.integration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.Mock;
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

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.repo.RepoTest;
import edu.tamu.app.service.manager.GitHubService;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@RunWith(SpringRunner.class)
public class RemoteProjectControllerIntegrationTest extends RepoTest {

    private static final String TEST_REMOTE_PROJECT_SCOPE = "0010";
    private static final String TEST_REMOTE_PROJECT_NAME = "Product Name";

    private static final long TEST_REMOTE_PROJECT_REQUEST_COUNT = 1L;
    private static final long TEST_REMOTE_PROJECT_ISSUE_COUNT = 2L;
    private static final long TEST_REMOTE_PROJECT_FEATURE_COUNT = 3L;
    private static final long TEST_REMOTE_PROJECT_DEFECT_COUNT = 4L;
    private static final long TEST_REMOTE_PROJECT_INTERNAL_COUNT = 5L;
    
    private static final RemoteProject TEST_REMOTE_PROJECT = new RemoteProject(TEST_REMOTE_PROJECT_SCOPE, TEST_REMOTE_PROJECT_NAME, TEST_REMOTE_PROJECT_REQUEST_COUNT, TEST_REMOTE_PROJECT_ISSUE_COUNT, TEST_REMOTE_PROJECT_FEATURE_COUNT, TEST_REMOTE_PROJECT_DEFECT_COUNT, TEST_REMOTE_PROJECT_INTERNAL_COUNT);
    
    private static final List<RemoteProject> TEST_REMOTE_PROJECT_LIST = new ArrayList<RemoteProject>(Arrays.asList(TEST_REMOTE_PROJECT));
    
    private static final Map<Long, List<RemoteProject>> TEST_REMOTE_PROJECT_MAP = new HashMap<Long, List<RemoteProject>>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private RemoteProjectManagerBean remoteProjectManagementBean;

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

        TEST_REMOTE_PROJECT_MAP.put(1L, TEST_REMOTE_PROJECT_LIST);

        when(remoteProjectsScheduledCacheService.get()).thenReturn(TEST_REMOTE_PROJECT_MAP);

        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);

        try {
            when(remoteProjectManagementBean.getRemoteProject()).thenReturn(TEST_REMOTE_PROJECT_LIST);
            when(remoteProjectManagementBean.getRemoteProjectByScopeId(any(String.class))).thenReturn(TEST_REMOTE_PROJECT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetRemoteProject() throws JsonProcessingException, Exception {
        // @formatter:off
        mockMvc.perform(
            get("/projects/remote")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "projects/remote/get-all",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the List of RemoteProjects.")
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

}
