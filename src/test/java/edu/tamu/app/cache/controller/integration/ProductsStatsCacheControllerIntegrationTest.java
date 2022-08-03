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
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.model.ProductStats;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.repo.AbstractRepoTest;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class ProductsStatsCacheControllerIntegrationTest extends AbstractRepoTest {

    private static final String TEST_PRODUCT_STATS_ID = "0010";
    private static final String TEST_PRODUCT_STATS_NAME = "Product Name";

    private static final long TEST_PRODUCT_STATS_REQUEST_COUNT = 1L;
    private static final long TEST_PRODUCT_STATS_ISSUE_COUNT = 2L;
    private static final long TEST_PRODUCT_STATS_FEATURE_COUNT = 3L;
    private static final long TEST_PRODUCT_STATS_DEFECT_COUNT = 4L;
    private static final long TEST_PRODUCT_STATS_INTERNAL_COUNT = 5L;

    private static final ProductStats TEST_PRODUCT_STATS = new ProductStats(TEST_PRODUCT_STATS_ID, TEST_PRODUCT_STATS_NAME, TEST_PRODUCT_STATS_REQUEST_COUNT, TEST_PRODUCT_STATS_ISSUE_COUNT, TEST_PRODUCT_STATS_FEATURE_COUNT, TEST_PRODUCT_STATS_DEFECT_COUNT, TEST_PRODUCT_STATS_INTERNAL_COUNT);

    private static final List<ProductStats> TEST_PRODUCT_STATS_LIST = new ArrayList<ProductStats>(Arrays.asList(TEST_PRODUCT_STATS));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService;

    @MockBean
    private ProductsStatsScheduledCacheService productsStatsScheduledCacheService;

    @MockBean
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        mockActiveSprintsScheduledCacheService(activeSprintsScheduledCacheService);
        mockProductsStatsScheduledCacheService(productsStatsScheduledCacheService);
        mockRemoteProjectsScheduledCacheService(remoteProjectsScheduledCacheService);
        
        when(productsStatsScheduledCacheService.get()).thenReturn(TEST_PRODUCT_STATS_LIST);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testProductStatsCacheGet() throws Exception {
        // @formatter:off
        mockMvc.perform(
            get("/products/stats")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "products/stats",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the array of Product Stats."),
                        fieldWithPath("payload['ArrayList<ProductStats>']").description("The array of Product Stats."),
                        fieldWithPath("payload['ArrayList<ProductStats>'][0].id").description("The Product Scope ID."),
                        fieldWithPath("payload['ArrayList<ProductStats>'][0].name").description("The Product Name."),
                        fieldWithPath("payload['ArrayList<ProductStats>'][0].backlogItemCount").description("The Product total Backlog Items."),
                        fieldWithPath("payload['ArrayList<ProductStats>'][0].requestCount").description("The Product total Requests."),
                        fieldWithPath("payload['ArrayList<ProductStats>'][0].issueCount").description("The Product total Issues."),
                        fieldWithPath("payload['ArrayList<ProductStats>'][0].featureCount").description("The Product total Features."),
                        fieldWithPath("payload['ArrayList<ProductStats>'][0].defectCount").description("The Product total Defects."),
                        fieldWithPath("payload['ArrayList<ProductStats>'][0].internalCount").description("The Product total Internal Requests.")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testProductStatsCacheUpdate() throws Exception {
        // @formatter:off
        mockMvc.perform(
            get("/products/stats/update")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(
                document(
                    "products/stats/update",
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

    @AfterEach
    public void cleanup() {
    }

}
