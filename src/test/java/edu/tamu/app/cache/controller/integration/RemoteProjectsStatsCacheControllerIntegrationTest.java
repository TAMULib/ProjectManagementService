package edu.tamu.app.cache.controller.integration;

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

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.repo.RepoTest;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@RunWith(SpringRunner.class)
public class RemoteProjectsStatsCacheControllerIntegrationTest extends RepoTest {
    
    private static final String TEST_REMOTE_PROJECTS_ID = "0010";
    private static final String TEST_REMOTE_PROJECTS_NAME = "Remote Project Name";

    private static final long TEST_REMOTE_PROJECTS_REQUEST_COUNT = 1L;
    private static final long TEST_REMOTE_PROJECTS_ISSUE_COUNT = 2L;
    private static final long TEST_REMOTE_PROJECTS_FEATURE_COUNT = 3L;
    private static final long TEST_REMOTE_PROJECTS_DEFECT_COUNT = 4L;
    private static final long TEST_REMOTE_PROJECTS_INTERNAL_COUNT = 5L;

    private static final RemoteProject TEST_REMOTE_PROJECTS = new RemoteProject(TEST_REMOTE_PROJECTS_ID, TEST_REMOTE_PROJECTS_NAME, TEST_REMOTE_PROJECTS_REQUEST_COUNT, TEST_REMOTE_PROJECTS_ISSUE_COUNT, TEST_REMOTE_PROJECTS_FEATURE_COUNT, TEST_REMOTE_PROJECTS_DEFECT_COUNT, TEST_REMOTE_PROJECTS_INTERNAL_COUNT);

    private static final List<RemoteProject> TEST_REMOTE_PROJECTS_LIST = new ArrayList<RemoteProject>(Arrays.asList(TEST_REMOTE_PROJECTS));

    private static final Map<Long, List<RemoteProject>> TEST_REMOTE_PROJECTS_MAP = new HashMap<Long, List<RemoteProject>>();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService;

    @MockBean
    private ProductsStatsScheduledCacheService productsStatsScheduledCacheService;

    @MockBean
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockActiveSprintsScheduledCacheService(activeSprintsScheduledCacheService);
        mockProductsStatsScheduledCacheService(productsStatsScheduledCacheService);
        mockRemoteProjectsScheduledCacheService(remoteProjectsScheduledCacheService);

        TEST_REMOTE_PROJECTS_MAP.put(1L, TEST_REMOTE_PROJECTS_LIST);

        when(remoteProjectsScheduledCacheService.get()).thenReturn(TEST_REMOTE_PROJECTS_MAP);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRemoteProjectsCacheGet() throws Exception {
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
                    "projects/remote",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the map of Remote Project stats."),
                        fieldWithPath("payload.HashMap['1']").description("The array of Remote Project stats."),
                        fieldWithPath("payload.HashMap['1'][0].id").description("The Remote Project Scope ID."),
                        fieldWithPath("payload.HashMap['1'][0].name").description("The Remote Project Name."),
                        fieldWithPath("payload.HashMap['1'][0].requestCount").description("The Remote Project total Requests."),
                        fieldWithPath("payload.HashMap['1'][0].issueCount").description("The Remote Project total Issues."),
                        fieldWithPath("payload.HashMap['1'][0].featureCount").description("The Remote Project total Features."),
                        fieldWithPath("payload.HashMap['1'][0].defectCount").description("The Remote Project total Defects."),
                        fieldWithPath("payload.HashMap['1'][0].internalCount").description("The Remote Project total Internal Requests.")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRemoteProjectsCacheUpdate() throws Exception {
        // @formatter:off
        mockMvc.perform(
            get("/projects/remote/update")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "projects/remote/update",
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

    @After
    public void cleanup() {
    }

}
