package edu.tamu.app.controller.integration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.repo.AbstractRepoTest;
import edu.tamu.app.model.repo.StatusRepo;
import edu.tamu.app.service.manager.GitHubService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@RunWith(SpringRunner.class)
public class StatusControllerIntegrationTest extends AbstractRepoTest {

    private static long currentId = 0L;

    private static final String TEST_PRODUCT_NAME = "Product Name";
    private static final String TEST_PRODUCT_SCOPE = "0010";
    private static final String TEST_PROJECT_URL = "http://localhost/";
    private static final String TEST_PROJECT_TOKEN = "0123456789";

    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE, TEST_PROJECT_URL, TEST_PROJECT_TOKEN);

    private static final List<RemoteProjectManager> TEST_REMOTE_PROJECT_MANAGER_LIST = new ArrayList<RemoteProjectManager>(Arrays.asList(TEST_REMOTE_PROJECT_MANAGER));

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PRODUCT_SCOPE, TEST_REMOTE_PROJECT_MANAGER);

    private static final List<RemoteProjectInfo> TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1));

    private static final Product TEST_PRODUCT = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST);

    private static final List<Product> TEST_PRODUCT_LIST = new ArrayList<Product>(Arrays.asList(TEST_PRODUCT));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StatusRepo statusRepo;

    @MockBean
    private ProductRepo productRepo;

    @MockBean
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @MockBean
    protected SugarService sugarService;

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

    // @After and @Before cannot be safely specified inside a parent class.
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockSugarService(sugarService);
        mockGitHubService(gitHubService, ghBuilder);
        mockVersionOneService(versionOneService);
        mockActiveSprintsScheduledCacheService(activeSprintsScheduledCacheService);
        mockProductsStatsScheduledCacheService(productsStatsScheduledCacheService);

        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PROJECT_MANAGER);
        when(remoteProjectManagerRepo.findAll()).thenReturn(TEST_REMOTE_PROJECT_MANAGER_LIST);

        when(productRepo.findAll()).thenReturn(TEST_PRODUCT_LIST);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetStatuses() throws JsonProcessingException, Exception {
        performCreateStatus();

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
                    "status/get-all",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the List of Status.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetStatusById() throws JsonProcessingException, Exception {
        performCreateStatus();

        // @formatter:off
        mockMvc.perform(
            get("/status/{id}", currentId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "status/get",
                    pathParameters(
                        parameterWithName("id").description("The Status ID.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the Status."),
                        fieldWithPath("payload.Status.id").description("The Status ID."),
                        fieldWithPath("payload.Status.identifier").description("The Status name."),
                        fieldWithPath("payload.Status.mapping").description("A List of Strings each representing a status name.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateStatus() throws JsonProcessingException, Exception {
        // @formatter:off
        performCreateStatus()
            .andDo(
                document(
                    "status/create",
                    requestFields(
                        fieldWithPath("id").description("The Status ID."),
                        fieldWithPath("identifier").description("The Status name."),
                        fieldWithPath("mapping").description("A List of Strings each representing a status name.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the Status."),
                        fieldWithPath("payload.Status.id").description("The Status ID."),
                        fieldWithPath("payload.Status.identifier").description("The Status name."),
                        fieldWithPath("payload.Status.mapping").description("A List of Strings each representing a status name.")
                    )
                )
            );
       // @formatter:on
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateStatus() throws JsonProcessingException, Exception {
        performCreateStatus();
        Status status = statusRepo.findOne(currentId);
        Status updated = getMockUpdatedStatus();

        status.setIdentifier(updated.getIdentifier());
        status.setMapping(updated.getMapping());

        ApiResponse expectedResponse = new ApiResponse(ApiStatus.SUCCESS, status);

        // @formatter:off
        mockMvc.perform(
            put("/status")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(status))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse))
            ).andDo(
                document(
                    "status/update",
                    requestFields(
                        fieldWithPath("id").description("The Status ID."),
                        fieldWithPath("identifier").description("The Status name."),
                        fieldWithPath("mapping").description("A List of Strings each representing a status name.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the Status."),
                        fieldWithPath("payload.Status.id").description("The Status ID."),
                        fieldWithPath("payload.Status.identifier").description("The Status name."),
                        fieldWithPath("payload.Status.mapping").description("A List of Strings each representing a status name.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteStatus() throws JsonProcessingException, Exception {
        performCreateStatus();

        Status status = statusRepo.findOne(currentId);

        // @formatter:off
        mockMvc.perform(
            delete("/status")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(status))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "status/delete",
                    requestFields(
                        fieldWithPath("id").description("The Status ID."),
                        fieldWithPath("identifier").description("The Status name."),
                        fieldWithPath("mapping").description("A List of Strings each representing a status name.")
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

    private ResultActions performCreateStatus() throws JsonProcessingException, Exception {
        Status status = getMockStatus();
        status.setId(++currentId);

        ApiResponse expectedResponse = new ApiResponse(ApiStatus.SUCCESS, status);

        // @formatter:off
        return mockMvc.perform(
            post("/status")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(status))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
        // @formatter:on
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @After
    public void cleanup() {
        cleanupRepos();
    }

    private Status getMockStatus() {
        Set<String> matches = new HashSet<String>(Arrays.asList("In Progress", "In Review"));
        return new Status("In Progress", matches);
    }

    private Status getMockUpdatedStatus() {
        Set<String> matches = new HashSet<String>(Arrays.asList("Under Development", "In Progress", "In Review"));
        return new Status("Under Development", matches);
    }

}
