package edu.tamu.app.controller.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.model.InternalRequest;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.repo.AbstractRepoTest;
import edu.tamu.app.service.manager.GitHubProjectService;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@RunWith(SpringRunner.class)
public class InternalRequestControllerIntegrationTest extends AbstractRepoTest {

    private static long currentId = 0L;

    private static final String TEST_INTERNAL_REQUEST_NAME = "Internal Request";
    private static final String TEST_INTERNAL_REQUEST_DESCRIPTION = "Description.";

    private static final String TEST_PRODUCT_NAME = "Product Name";

    private static final long TEST_PROJECT_ID = 1L;
    private static final String TEST_PROJECT_SCOPE = "0010";
    private static final String TEST_PROJECT_URL = "http://localhost/";
    private static final String TEST_PROJECT_TOKEN = "0123456789";

    private static final long TEST_REMOTE_PROJECT_MANAGER_ID = 1L;

    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER = new RemoteProjectManager("Test Remote Project Manager", ServiceType.GITHUB_PROJECT, TEST_PROJECT_URL, TEST_PROJECT_TOKEN);

    private static final List<RemoteProjectManager> TEST_REMOTE_PROJECT_MANAGER_LIST = new ArrayList<RemoteProjectManager>(Arrays.asList(TEST_REMOTE_PROJECT_MANAGER));

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO = new RemoteProjectInfo(TEST_PROJECT_SCOPE, TEST_REMOTE_PROJECT_MANAGER);

    private static final List<RemoteProjectInfo> TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO));

    private static final Product TEST_PRODUCT = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST);

    private static final List<Product> TEST_PRODUCT_LIST = new ArrayList<Product>(Arrays.asList(TEST_PRODUCT));

    private static final Date TEST_DATE = Date.from(Instant.now());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InternalRequestRepo internalRequestRepo;

    @MockBean
    private ProductRepo productRepo;

    @MockBean
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @MockBean
    private RemoteProjectManagerBean remoteProjectManagerBean;

    @MockBean
    protected GitHubProjectService gitHubService;

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

        mockGitHubService(gitHubService, ghBuilder);
        mockVersionOneService(versionOneService);
        mockActiveSprintsScheduledCacheService(activeSprintsScheduledCacheService);
        mockProductsStatsScheduledCacheService(productsStatsScheduledCacheService);

        TEST_REMOTE_PROJECT_MANAGER.setId(TEST_REMOTE_PROJECT_MANAGER_ID);
        TEST_PRODUCT.setId(TEST_PROJECT_ID);

        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PROJECT_MANAGER);
        when(remoteProjectManagerRepo.findAll()).thenReturn(TEST_REMOTE_PROJECT_MANAGER_LIST);

        when(productRepo.findAll()).thenReturn(TEST_PRODUCT_LIST);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT);
        when(productRepo.create(any(Product.class))).thenReturn(TEST_PRODUCT);
        when(productRepo.update(any(Product.class))).thenReturn(TEST_PRODUCT);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetInternalRequests() throws JsonProcessingException, Exception {
        performCreateInternalRequest();

        // @formatter:off
        mockMvc.perform(
            get("/internal/request")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].id", equalTo((int) currentId)))
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].title", equalTo(TEST_INTERNAL_REQUEST_NAME)))
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].description", equalTo(TEST_INTERNAL_REQUEST_DESCRIPTION)))
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].createdOn", notNullValue()))
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].product.id", equalTo((int) TEST_PROJECT_ID)))
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].product.name", equalTo(TEST_PRODUCT.getName())))
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].product.scopeId").doesNotExist())
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].product.devUrl").doesNotExist())
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].product.preUrl").doesNotExist())
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].product.productionUrl").doesNotExist())
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].product.wikiUrl").doesNotExist())
            .andExpect(jsonPath("payload.ArrayList<InternalRequest>[0].product.otherUrl").doesNotExist())
            .andDo(
                document(
                    "internal/request/get-all",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the List of Internal Requests.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetInternalRequestById() throws JsonProcessingException, Exception {
        performCreateInternalRequest();

        // @formatter:off
        mockMvc.perform(
            get("/internal/request/{id}", currentId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("payload.InternalRequest.id", equalTo((int) currentId)))
            .andExpect(jsonPath("payload.InternalRequest.title", equalTo(TEST_INTERNAL_REQUEST_NAME)))
            .andExpect(jsonPath("payload.InternalRequest.description", equalTo(TEST_INTERNAL_REQUEST_DESCRIPTION)))
            .andExpect(jsonPath("payload.InternalRequest.createdOn", notNullValue()))
            .andExpect(jsonPath("payload.InternalRequest.product.id", equalTo((int) TEST_PROJECT_ID)))
            .andExpect(jsonPath("payload.InternalRequest.product.name", equalTo(TEST_PRODUCT.getName())))
            .andExpect(jsonPath("payload.InternalRequest.product.scopeId").doesNotExist())
            .andExpect(jsonPath("payload.InternalRequest.product.devUrl").doesNotExist())
            .andExpect(jsonPath("payload.InternalRequest.product.preUrl").doesNotExist())
            .andExpect(jsonPath("payload.InternalRequest.product.productionUrl").doesNotExist())
            .andExpect(jsonPath("payload.InternalRequest.product.wikiUrl").doesNotExist())
            .andExpect(jsonPath("payload.InternalRequest.product.otherUrl").doesNotExist())
            .andDo(
                document(
                    "internal/request/get",
                    pathParameters(
                        parameterWithName("id").description("The Product ID.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the Internal Request."),
                        fieldWithPath("payload.InternalRequest.id").description("The Internal Request ID."),
                        fieldWithPath("payload.InternalRequest.title").description("The Internal Request name."),
                        fieldWithPath("payload.InternalRequest.createdOn").description("The date and time the Internal Request was created."),
                        fieldWithPath("payload.InternalRequest.description").description("The description of the Internal Request."),
                        fieldWithPath("payload.InternalRequest.product").description("The Product assocaited with the Internal Request.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateInternalRequest() throws JsonProcessingException, Exception {
        // @formatter:off
        performCreateInternalRequest()
            .andDo(
                document(
                    "internal/request/create",
                    requestFields(
                        fieldWithPath("id").description("The Internal Request ID."),
                        fieldWithPath("title").description("The Internal Request name."),
                        fieldWithPath("createdOn").description("The date and time the Internal Request was created."),
                        fieldWithPath("description").description("The description of the Internal Request."),
                        fieldWithPath("product").description("The Product assocaited with the Internal Request.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the Internal Request."),
                        fieldWithPath("payload.InternalRequest.id").description("The Internal Request ID."),
                        fieldWithPath("payload.InternalRequest.title").description("The Internal Request name."),
                        fieldWithPath("payload.InternalRequest.createdOn").description("The date and time the Internal Request was created."),
                        fieldWithPath("payload.InternalRequest.description").description("The description of the Internal Request."),
                        fieldWithPath("payload.InternalRequest.product").description("The Product assocaited with the Internal Request.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateInternalRequest() throws JsonProcessingException, Exception {
        performCreateInternalRequest();

        InternalRequest internalRequest = internalRequestRepo.findOne(currentId);

        internalRequest.setTitle("Updated " + internalRequest.getTitle());
        internalRequest.setCreatedOn(TEST_DATE);

        ApiResponse expectedResponse = new ApiResponse(ApiStatus.SUCCESS, internalRequest);

        // @formatter:off
        mockMvc.perform(
            put("/internal/request")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(internalRequest))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)))
            .andDo(
                document(
                    "internal/request/update",
                    requestFields(
                        fieldWithPath("id").description("The Internal Request ID."),
                        fieldWithPath("title").description("The Internal Request name."),
                        fieldWithPath("createdOn").description("The date and time the Internal Request was created."),
                        fieldWithPath("description").description("The description of the Internal Request."),
                        fieldWithPath("product").description("The Product assocaited with the Internal Request.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the Internal Request."),
                        fieldWithPath("payload.InternalRequest.id").description("The Internal Request ID."),
                        fieldWithPath("payload.InternalRequest.title").description("The Internal Request name."),
                        fieldWithPath("payload.InternalRequest.createdOn").description("The date and time the Internal Request was created."),
                        fieldWithPath("payload.InternalRequest.description").description("The description of the Internal Request."),
                        fieldWithPath("payload.InternalRequest.product").description("The Product assocaited with the Internal Request.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteInternalRequest() throws JsonProcessingException, Exception {
        performCreateInternalRequest();

        InternalRequest internalRequest = internalRequestRepo.findOne(currentId);

        // @formatter:off
        mockMvc.perform(
            delete("/internal/request")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(internalRequest))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "internal/request/delete",
                    requestFields(
                        fieldWithPath("id").description("The Internal Request ID."),
                        fieldWithPath("title").description("The Internal Request name."),
                        fieldWithPath("createdOn").description("The date and time the Internal Request was created."),
                        fieldWithPath("description").description("The description of the Internal Request."),
                        fieldWithPath("product").description("The Product assocaited with the Internal Request.")
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
    public void testInternalRequestPush() throws Exception {
        performCreateInternalRequest();

        InternalRequest internalRequest = internalRequestRepo.findOne(currentId);
        long requestId = internalRequest.getId();
        long productId = internalRequest.getProduct().getId();

        RemoteProjectInfo rpi = internalRequest.getProduct().getRemoteProjectInfo().get(0);
        long rpmId = rpi.getRemoteProjectManager().getId();

        String scopeId = rpi.getScopeId();

        // @formatter:off
        mockMvc.perform(
            put("/internal/request/push/{requestId}/{productId}/{rpmId}", requestId, productId, rpmId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(scopeId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "internal/request/push",
                    pathParameters(
                        parameterWithName("requestId").description("The Request ID."),
                        parameterWithName("productId").description("The Product ID associated with the Request."),
                        parameterWithName("rpmId").description("The Remote Project Manager ID associated with the Request.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the response message from the remote service.")
                    )
                )
            );
       // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInternalRequestStats() throws Exception {
        performCreateInternalRequest();

        // @formatter:off
        mockMvc.perform(
            get("/internal/request/stats")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "internal/request/stats",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the Internal Stats."),
                        fieldWithPath("payload.InternalStats.unassignedCount").description("The Internal Stats representing the total unassigned Internal Requests."),
                        fieldWithPath("payload.InternalStats.assignedCount").description("The Internal Stats representing the total assigned Internal Requests."),
                        fieldWithPath("payload.InternalStats.totalCount").description("The Internal Stats representing the total Internal Requests.")
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

    private ResultActions performCreateInternalRequest() throws JsonProcessingException, Exception {
        InternalRequest ir = new InternalRequest(TEST_INTERNAL_REQUEST_NAME, TEST_INTERNAL_REQUEST_DESCRIPTION, TEST_PRODUCT, TEST_DATE);
        ir.setId(++currentId);

        ApiResponse expectedResponse = new ApiResponse(ApiStatus.SUCCESS, ir);

        // @formatter:off
        return mockMvc.perform(
            post("/internal/request")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(ir))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo((result) -> {
                // @CreationTimestamp may result in a different timestamp by nature, so effectively ignore the createdOn field.
                JsonNode node = objectMapper.readTree(result.getResponse().getContentAsByteArray());
                Long createdOnString = node.findPath("InternalRequest").findValue("createdOn").asLong();

                ir.setCreatedOn(new Date(createdOnString));

                HashMap<String, Object> payload = new HashMap<>(); 

                payload.put(ir.getClass().getSimpleName(), ir);
                expectedResponse.setPayload(payload);
            })
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
        // @formatter:on
    }
}
