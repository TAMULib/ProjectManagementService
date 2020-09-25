package edu.tamu.app.controller.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.repo.RepoTest;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.manager.GitHubService;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.ticketing.SugarService;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@RunWith(SpringRunner.class)
public class ProductControllerIntegrationTest extends RepoTest {

    @Value("classpath:mock/credentials/aggiejack.json")
    private Resource aggiejack;

    private static long currentId = 0L;

    private static final String TEST_PRODUCT_NAME = "Product Name";
    private static final String TEST_PRODUCT_DEV_URL = "http://localhost/dev/";
    private static final String TEST_PRODUCT_PRE_URL = "http://localhost/pre/";
    private static final String TEST_PRODUCT_PRODUCTION_URL = "http://localhost/production/";
    private static final String TEST_PRODUCT_WIKI_URL = "http://localhost/wiki/";
    private static final String TEST_PRODUCT_OTHER_URL1 = "http://localhost/other1/";
    private static final String TEST_PRODUCT_OTHER_URL2 = "http://localhost/other2/";

    private static final List<String> TEST_PRODUCT_OTHER_URL_LIST = new ArrayList<String>(Arrays.asList(TEST_PRODUCT_OTHER_URL1, TEST_PRODUCT_OTHER_URL2));

    private static final String TEST_PROJECT_SCOPE = "0";
    private static final String TEST_PROJECT_URL = "http://localhost/";
    private static final String TEST_PROJECT_TOKEN = "0123456789";

    private static final long TEST_REMOTE_PROJECT_REQUEST_COUNT = 1L;
    private static final long TEST_REMOTE_PROJECT_ISSUE_COUNT = 2L;
    private static final long TEST_REMOTE_PROJECT_FEATURE_COUNT = 3L;
    private static final long TEST_REMOTE_PROJECT_DEFECT_COUNT = 4L;
    private static final long TEST_REMOTE_PROJECT_INTERNAL_COUNT = 5L;

    private static final RemoteProject TEST_REMOTE_PROJECT1 = new RemoteProject(TEST_PROJECT_SCOPE, TEST_PRODUCT_NAME, TEST_REMOTE_PROJECT_REQUEST_COUNT, TEST_REMOTE_PROJECT_ISSUE_COUNT, TEST_REMOTE_PROJECT_FEATURE_COUNT, TEST_REMOTE_PROJECT_DEFECT_COUNT, TEST_REMOTE_PROJECT_INTERNAL_COUNT);

    private static final List<RemoteProject> TEST_REMOTE_PROJECT_LIST = new ArrayList<RemoteProject>(Arrays.asList(TEST_REMOTE_PROJECT1));

    private static final long TEST_REMOTE_PROJECT_MANAGER_ID = 1L;
    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE, TEST_PROJECT_URL, TEST_PROJECT_TOKEN);

    private static final List<RemoteProjectManager> TEST_REMOTE_PROJECT_MANAGER_LIST = new ArrayList<RemoteProjectManager>(Arrays.asList(TEST_REMOTE_PROJECT_MANAGER));

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO = new RemoteProjectInfo(TEST_PROJECT_SCOPE, TEST_REMOTE_PROJECT_MANAGER);

    private static final List<RemoteProjectInfo> TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepo productRepo;

    @MockBean
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @MockBean
    private ManagementBeanRegistry managementBeanRegistry;

    @MockBean
    private RemoteProjectManagerBean remoteProjectManagementBean;

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

    @MockBean
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    // @After and @Before cannot be safely specified inside a parent class.
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockSugarService(sugarService);
        mockGitHubService(gitHubService, ghBuilder);
        mockVersionOneService(versionOneService);
        mockActiveSprintsScheduledCacheService(activeSprintsScheduledCacheService);
        mockProductsStatsScheduledCacheService(productsStatsScheduledCacheService);
        mockRemoteProjectsScheduledCacheService(remoteProjectsScheduledCacheService);

        TEST_REMOTE_PROJECT_MANAGER.setId(TEST_REMOTE_PROJECT_MANAGER_ID);

        when(remoteProjectManagerRepo.findAll()).thenReturn(TEST_REMOTE_PROJECT_MANAGER_LIST);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PROJECT_MANAGER);

        doNothing().when(remoteProjectManagerRepo).delete(any(RemoteProjectManager.class));

        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);

        try {
            when(remoteProjectManagementBean.getRemoteProject()).thenReturn(TEST_REMOTE_PROJECT_LIST);
            when(remoteProjectManagementBean.getRemoteProjectByScopeId(TEST_PROJECT_SCOPE)).thenReturn(TEST_REMOTE_PROJECT1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetProducts() throws Exception {
        performCreateProduct();

        // @formatter:off
        mockMvc.perform(
            get("/products")
                .accept(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("meta.status", equalTo("SUCCESS")))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].id", equalTo((int) currentId)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].name", equalTo(TEST_PRODUCT_NAME + (int) currentId)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].scopeId", equalTo(TEST_PROJECT_SCOPE)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].devUrl", equalTo(TEST_PRODUCT_DEV_URL)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].preUrl", equalTo(TEST_PRODUCT_PRE_URL)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].productionUrl", equalTo(TEST_PRODUCT_PRODUCTION_URL)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].wikiUrl", equalTo(TEST_PRODUCT_WIKI_URL)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].otherUrls[0]", equalTo(TEST_PRODUCT_OTHER_URL1)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].otherUrls[1]", equalTo(TEST_PRODUCT_OTHER_URL2)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProjectInfo[0].scopeId", equalTo(TEST_PROJECT_SCOPE)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProjectInfo[0].remoteProjectManager.id", equalTo((int) TEST_REMOTE_PROJECT_MANAGER_ID)))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProjectInfo[0].remoteProjectManager.name", equalTo(TEST_REMOTE_PROJECT_MANAGER.getName())))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProjectInfo[0].remoteProjectManager.type", equalTo(TEST_REMOTE_PROJECT_MANAGER.getType().toString())))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProjectInfo[0].remoteProjectManager.url").doesNotExist())
            .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProjectInfo[0].remoteProjectManager.token").doesNotExist())
            .andDo(
                document(
                    "products/get-all",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the List of Products.")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetProductById() throws Exception {
        performCreateProduct();

        // @formatter:off
        mockMvc.perform(
            get("/products/{id}", currentId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("meta.status", equalTo("SUCCESS")))
            .andExpect(jsonPath("payload.Product.id", equalTo((int) currentId)))
            .andExpect(jsonPath("payload.Product.name", equalTo(TEST_PRODUCT_NAME + (int) currentId)))
            .andExpect(jsonPath("payload.Product.scopeId", equalTo(TEST_PROJECT_SCOPE)))
            .andExpect(jsonPath("payload.Product.devUrl", equalTo(TEST_PRODUCT_DEV_URL)))
            .andExpect(jsonPath("payload.Product.preUrl", equalTo(TEST_PRODUCT_PRE_URL)))
            .andExpect(jsonPath("payload.Product.productionUrl", equalTo(TEST_PRODUCT_PRODUCTION_URL)))
            .andExpect(jsonPath("payload.Product.wikiUrl", equalTo(TEST_PRODUCT_WIKI_URL)))
            .andExpect(jsonPath("payload.Product.otherUrls[0]", equalTo(TEST_PRODUCT_OTHER_URL1)))
            .andExpect(jsonPath("payload.Product.otherUrls[1]", equalTo(TEST_PRODUCT_OTHER_URL2)))
            .andExpect(jsonPath("payload.Product.remoteProjectInfo[0].scopeId", equalTo(TEST_PROJECT_SCOPE)))
            .andExpect(jsonPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.id", equalTo((int) TEST_REMOTE_PROJECT_MANAGER_ID)))
            .andExpect(jsonPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.name", equalTo(TEST_REMOTE_PROJECT_MANAGER.getName())))
            .andExpect(jsonPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.type", equalTo(TEST_REMOTE_PROJECT_MANAGER.getType().toString())))
            .andExpect(jsonPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.url").doesNotExist())
            .andExpect(jsonPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.token").doesNotExist())
            .andDo(
                document(
                    "products/get",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the Product."),
                        fieldWithPath("payload.Product.id").description("The Product ID."),
                        fieldWithPath("payload.Product.name").description("The Product name."),
                        fieldWithPath("payload.Product.scopeId").description("The Product Scope ID."),
                        fieldWithPath("payload.Product.devUrl").description("The Product development URL."),
                        fieldWithPath("payload.Product.preUrl").description("The Product pre-production URL."),
                        fieldWithPath("payload.Product.productionUrl").description("The Product production URL."),
                        fieldWithPath("payload.Product.wikiUrl").description("The Product wiki URL."),
                        fieldWithPath("payload.Product.otherUrls").description("An array of Product URLs."),
                        fieldWithPath("payload.Product.remoteProjectInfo").description("An array of Remote Project Information associated with the Product."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].scopeId").description("The Remote Project Scope ID."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager").description("The Remote Project Manager."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.id").description("The Remote Project Manager ID."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.name").description("The Remote Project Manager name."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.type").description("The Remote Project Manager type.")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateProduct() throws Exception {

        // @formatter:off
        performCreateProduct().andDo(
            document(
                "products/create",
                requestFields(
                    fieldWithPath("id").description("The Product ID."),
                    fieldWithPath("name").description("The Product name."),
                    fieldWithPath("scopeId").description("The Product Scope ID."),
                    fieldWithPath("devUrl").description("The Product development URL."),
                    fieldWithPath("preUrl").description("The Product pre-production URL."),
                    fieldWithPath("productionUrl").description("The Product production URL."),
                    fieldWithPath("wikiUrl").description("The Product wiki URL."),
                    fieldWithPath("otherUrls").description("An array of Product URLs."),
                    fieldWithPath("remoteProjectInfo").description("An array of Remote Project Information associated with the Product."),
                    fieldWithPath("remoteProjectInfo[0].scopeId").description("The Remote Project Scope ID."),
                    fieldWithPath("remoteProjectInfo[0].remoteProjectManager").description("The Remote Project Manager."),
                    fieldWithPath("remoteProjectInfo[0].remoteProjectManager.id").description("The Remote Project Manager ID."),
                    fieldWithPath("remoteProjectInfo[0].remoteProjectManager.name").description("The Remote Project Manager name."),
                    fieldWithPath("remoteProjectInfo[0].remoteProjectManager.type").description("The Remote Project Manager type."),
                    fieldWithPath("remoteProjectInfo[0].remoteProjectManager.url").description("The Remote Project Manager URL."),
                    fieldWithPath("remoteProjectInfo[0].remoteProjectManager.token").description("The Remote Project Manager token.")
                ),
                responseFields(
                    fieldWithPath("meta").description("API response meta."),
                    fieldWithPath("meta.id").description("ID of the request."),
                    fieldWithPath("meta.action").description("Action of the request."),
                    fieldWithPath("meta.message").description("Message of the response."),
                    fieldWithPath("meta.status").description("Status of the response."),
                    fieldWithPath("payload").description("API response payload containing the Product."),
                    fieldWithPath("payload.Product.id").description("The Product ID."),
                    fieldWithPath("payload.Product.name").description("The Product name."),
                    fieldWithPath("payload.Product.scopeId").description("The Product Scope ID."),
                    fieldWithPath("payload.Product.devUrl").description("The Product development URL."),
                    fieldWithPath("payload.Product.preUrl").description("The Product pre-production URL."),
                    fieldWithPath("payload.Product.productionUrl").description("The Product production URL."),
                    fieldWithPath("payload.Product.wikiUrl").description("The Product wiki URL."),
                    fieldWithPath("payload.Product.otherUrls").description("An array of Product URLs."),
                    fieldWithPath("payload.Product.remoteProjectInfo").description("An array of Remote Project Information associated with the Product."),
                    fieldWithPath("payload.Product.remoteProjectInfo[0].scopeId").description("The Remote Project Scope ID."),
                    fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager").description("The Remote Project Manager."),
                    fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.id").description("The Remote Project Manager ID."),
                    fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.name").description("The Remote Project Manager name."),
                    fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.type").description("The Remote Project Manager type."),
                    fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.url").description("The Remote Project Manager URL."),
                    fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.token").description("The Remote Project Manager token.")
                )
            )
        );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateProduct() throws Exception {
        performCreateProduct();

        Product product = productRepo.findOne(currentId);

        product.setName("Updated " + product.getName());

        ApiResponse expectedResponse = new ApiResponse(ApiStatus.SUCCESS, product);

        // @formatter:off
        mockMvc.perform(
            put("/products")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(product))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse))
            ).andDo(
                document(
                    "products/update",
                    requestFields(
                        fieldWithPath("id").description("The Product ID."),
                        fieldWithPath("name").description("The Product name."),
                        fieldWithPath("scopeId").description("The Product Scope ID."),
                        fieldWithPath("devUrl").description("The Product development URL."),
                        fieldWithPath("preUrl").description("The Product pre-production URL."),
                        fieldWithPath("productionUrl").description("The Product production URL."),
                        fieldWithPath("wikiUrl").description("The Product wiki URL."),
                        fieldWithPath("otherUrls").description("An array of Product URLs."),
                        fieldWithPath("remoteProjectInfo").description("An array of Remote Project Information associated with the Product."),
                        fieldWithPath("remoteProjectInfo[0].scopeId").description("The Remote Project Scope ID."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager").description("The Remote Project Manager."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager.id").description("The Remote Project Manager ID."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager.name").description("The Remote Project Manager name."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager.type").description("The Remote Project Manager type."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager.url").description("The Remote Project Manager URL."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager.token").description("The Remote Project Manager token.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the Product."),
                        fieldWithPath("payload.Product.id").description("The Product ID."),
                        fieldWithPath("payload.Product.name").description("The Product name."),
                        fieldWithPath("payload.Product.scopeId").description("The Product Scope ID."),
                        fieldWithPath("payload.Product.devUrl").description("The Product development URL."),
                        fieldWithPath("payload.Product.preUrl").description("The Product pre-production URL."),
                        fieldWithPath("payload.Product.productionUrl").description("The Product production URL."),
                        fieldWithPath("payload.Product.wikiUrl").description("The Product wiki URL."),
                        fieldWithPath("payload.Product.otherUrls").description("An array of Product URLs."),
                        fieldWithPath("payload.Product.remoteProjectInfo").description("An array of Remote Project Information associated with the Product."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].scopeId").description("The Remote Project Scope ID."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager").description("The Remote Project Manager."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.id").description("The Remote Project Manager ID."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.name").description("The Remote Project Manager name."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.type").description("The Remote Project Manager type."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.url").description("The Remote Project Manager URL."),
                        fieldWithPath("payload.Product.remoteProjectInfo[0].remoteProjectManager.token").description("The Remote Project Manager token.")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteProduct() throws Exception {
        performCreateProduct();

        Product product = productRepo.findOne(currentId);

        // @formatter:off
        mockMvc.perform(
            delete("/products")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(product))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "products/delete",
                    requestFields(
                        fieldWithPath("id").description("The Product ID."),
                        fieldWithPath("name").description("The Product name."),
                        fieldWithPath("scopeId").description("The Product Scope ID."),
                        fieldWithPath("devUrl").description("The Product development URL."),
                        fieldWithPath("preUrl").description("The Product pre-production URL."),
                        fieldWithPath("productionUrl").description("The Product production URL."),
                        fieldWithPath("wikiUrl").description("The Product wiki URL."),
                        fieldWithPath("otherUrls").description("An array of Product URLs."),
                        fieldWithPath("remoteProjectInfo").description("An array of Remote Project Information associated with the Product."),
                        fieldWithPath("remoteProjectInfo[0].scopeId").description("The Remote Project Scope ID."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager").description("The Remote Project Manager."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager.id").description("The Remote Project Manager ID."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager.name").description("The Remote Project Manager name."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager.type").description("The Remote Project Manager type."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager.url").description("The Remote Project Manager URL."),
                        fieldWithPath("remoteProjectInfo[0].remoteProjectManager.token").description("The Remote Project Manager token.")
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
    public void testProductIssue() throws Exception {
        TicketRequest ticket = new TicketRequest("Ticket Title", "Ticket Description", "Ticket Service", getMockAggieJackCredentials());

        // @formatter:off
        mockMvc.perform(
            post("/products/issue")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(ticket))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "products/issue",
                    requestFields(
                        fieldWithPath("title").description("The Ticket Title."),
                        fieldWithPath("description").description("The Ticket Description."),
                        fieldWithPath("service").description("The Service name."),
                        fieldWithPath("credentials").description("The Service credentials."),
                        fieldWithPath("credentials.lastName").description("The Issue Creator's Last Name."),
                        fieldWithPath("credentials.firstName").description("The Issue Creator's First Name."),
                        fieldWithPath("credentials.netid").description("The Issue Creator's Net ID."),
                        fieldWithPath("credentials.uin").description("The Issue Creator's UIN."),
                        fieldWithPath("credentials.exp").description("The Issue Creator's expiration."),
                        fieldWithPath("credentials.email").description("The Issue Creator's E-mail."),
                        fieldWithPath("credentials.role").description("The Issue Creator's role."),
                        fieldWithPath("credentials.affiliation").description("The Issue Creator's affiliation."),
                        fieldWithPath("credentials.allCredentials").description("A map of additional credential information.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the response message from the Ticket service.")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testProductFeature() throws Exception {
        performCreateProduct();

        Product product = productRepo.findOne(currentId);

        FeatureRequest feature = new FeatureRequest("Feature Title", "Feature Description", currentId, product.getScopeId());

        // @formatter:off
        mockMvc.perform(
            post("/products/feature")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(feature))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "products/feature",
                    requestFields(
                        fieldWithPath("title").description("The Feature Title."),
                        fieldWithPath("description").description("The Feature Description."),
                        fieldWithPath("productId").description("The Product ID associated with this Feature."),
                        fieldWithPath("scopeId").description("The Product Scope ID associated with this Feature.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the response message from the Feature service.")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testProductRemoteProjectsByProductId() throws Exception {
        performCreateProduct();

        // @formatter:off
        mockMvc.perform(
            get("/products/remote-projects/{productId}", currentId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "products/remote-projects",
                    pathParameters(
                        parameterWithName("productId").description("The Product ID.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the array of Remote Projects."),
                        fieldWithPath("payload.HashMap").description("An array of Remote Projects associated with the Product."),
                        fieldWithPath("payload.HashMap['" + TEST_PROJECT_SCOPE + "'].id").description("The Remote Project Scope ID."),
                        fieldWithPath("payload.HashMap['" + TEST_PROJECT_SCOPE + "'].name").description("The Remote Project Name."),
                        fieldWithPath("payload.HashMap['" + TEST_PROJECT_SCOPE + "'].requestCount").description("The total number of Requests in the Remote Project."),
                        fieldWithPath("payload.HashMap['" + TEST_PROJECT_SCOPE + "'].issueCount").description("The total number of Issues in the Remote Project."),
                        fieldWithPath("payload.HashMap['" + TEST_PROJECT_SCOPE + "'].featureCount").description("The total number of Features in the Remote Project."),
                        fieldWithPath("payload.HashMap['" + TEST_PROJECT_SCOPE + "'].defectCount").description("The total number of Defects in the Remote Project."),
                        fieldWithPath("payload.HashMap['" + TEST_PROJECT_SCOPE + "'].internalCount").description("The total number of Internals in the Remote Project.")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testProductRemoteProjectsByRemoteProjectManagerId() throws Exception {
        performCreateProduct();

        Product product = productRepo.findOne(currentId);

        RemoteProjectInfo rpi = product.getRemoteProjectInfo().get(0);
        RemoteProjectManager rpm = rpi.getRemoteProjectManager();
        long rpmId = rpm.getId();

        // @formatter:off
        mockMvc.perform(
            get("/products/{remoteProjectManagerId}/remote-projects", rpmId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "products/{remoteProjectManagerId}/remote-projects",
                    pathParameters(
                        parameterWithName("remoteProjectManagerId").description("The Remote Project Manager ID the Remote Projects are associated with.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the array of Remote Projects."),
                        fieldWithPath("payload['ArrayList<RemoteProject>']").description("An array of Remote Projects associated with the Remote Project Manager."),
                        fieldWithPath("payload['ArrayList<RemoteProject>'][0]").description("An array of Remote Projects associated with the Remote Project Manager."),
                        fieldWithPath("payload['ArrayList<RemoteProject>'][0].id").description("The Remote Project Scope ID."),
                        fieldWithPath("payload['ArrayList<RemoteProject>'][0].name").description("The Remote Project Name."),
                        fieldWithPath("payload['ArrayList<RemoteProject>'][0].requestCount").description("The total number of Requests in the Remote Project."),
                        fieldWithPath("payload['ArrayList<RemoteProject>'][0].issueCount").description("The total number of Issues in the Remote Project."),
                        fieldWithPath("payload['ArrayList<RemoteProject>'][0].featureCount").description("The total number of Features in the Remote Project."),
                        fieldWithPath("payload['ArrayList<RemoteProject>'][0].defectCount").description("The total number of Defects in the Remote Project."),
                        fieldWithPath("payload['ArrayList<RemoteProject>'][0].internalCount").description("The total number of Internals in the Remote Project.")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testProductRemoteProjectsByScopeId() throws Exception {
        performCreateProduct();

        Product product = productRepo.findOne(currentId);

        RemoteProjectInfo rpi = product.getRemoteProjectInfo().get(0);
        RemoteProjectManager rpm = rpi.getRemoteProjectManager();
        long rpmId = rpm.getId();
        String scopeId = product.getScopeId();

        // @formatter:off
        mockMvc.perform(
            get("/products/{remoteProjectManagerId}/remote-projects/{scopeId}", rpmId, scopeId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "products/{remoteProjectManagerId}/remote-projects/{scopeId}",
                    pathParameters(
                        parameterWithName("remoteProjectManagerId").description("The Remote Project Manager ID the Remote Projects are associated with."),
                        parameterWithName("scopeId").description("The Remote Project Scope ID.")
                    ),
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the array of Remote Projects."),
                        fieldWithPath("payload.RemoteProject").description("The Remote Project associated with the Remote Project Manager with the given Project Scope ID."),
                        fieldWithPath("payload.RemoteProject.id").description("The Remote Project Scope ID."),
                        fieldWithPath("payload.RemoteProject.name").description("The Remote Project Name."),
                        fieldWithPath("payload.RemoteProject.requestCount").description("The total number of Requests in the Remote Project."),
                        fieldWithPath("payload.RemoteProject.issueCount").description("The total number of Issues in the Remote Project."),
                        fieldWithPath("payload.RemoteProject.featureCount").description("The total number of Features in the Remote Project."),
                        fieldWithPath("payload.RemoteProject.defectCount").description("The total number of Defects in the Remote Project."),
                        fieldWithPath("payload.RemoteProject.internalCount").description("The total number of Internals in the Remote Project.")
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

    private ResultActions performCreateProduct() throws JsonProcessingException, Exception {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST, TEST_PROJECT_SCOPE, TEST_PRODUCT_DEV_URL, TEST_PRODUCT_PRE_URL, TEST_PRODUCT_PRODUCTION_URL, TEST_PRODUCT_WIKI_URL, TEST_PRODUCT_OTHER_URL_LIST);
        product.setId(++currentId);
        product.setName(TEST_PRODUCT_NAME + (int) currentId);

        ApiResponse expectedResponse = new ApiResponse(ApiStatus.SUCCESS, product);

        // @formatter:off
        return mockMvc.perform(
            post("/products")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(product))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
        // @formatter:on
    }

    private Credentials getMockAggieJackCredentials() throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(aggiejack.getFile(), Credentials.class);
    }

}
