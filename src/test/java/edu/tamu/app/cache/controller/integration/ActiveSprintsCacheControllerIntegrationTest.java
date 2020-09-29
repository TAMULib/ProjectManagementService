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
import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.repo.RepoTest;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@RunWith(SpringRunner.class)
public class ActiveSprintsCacheControllerIntegrationTest extends RepoTest {

    private static final String TEST_MEMBER_ID = "Test Member ID";
    private static final String TEST_MEMBER_NAME = "Test Member Name";
    private static final String TEST_MEMBER_AVATAR = "Test Member Avatar";

    private static final Member TEST_MEMBER = new Member(TEST_MEMBER_ID, TEST_MEMBER_NAME, TEST_MEMBER_AVATAR);

    private static final List<Member> TEST_MEMBER_LIST = new ArrayList<Member>(Arrays.asList(TEST_MEMBER));

    private static final String TEST_CARD_ID = "Test Card ID";
    private static final String TEST_CARD_NUMBER = "Test Card Number";
    private static final String TEST_CARD_TYPE = "Test Card Type";
    private static final String TEST_CARD_NAME = "Test Card Name";
    private static final String TEST_CARD_DESCRIPTION = "Test Card Description";
    private static final String TEST_CARD_STATUS = "Test Card Status";
    private static final Float TEST_CARD_ESTIMATE = 1.0f;

    private static final Card TEST_CARD = new Card(TEST_CARD_ID, TEST_CARD_NUMBER, TEST_CARD_TYPE, TEST_CARD_NAME, TEST_CARD_DESCRIPTION, TEST_CARD_STATUS, TEST_CARD_ESTIMATE, TEST_MEMBER_LIST);

    private static final List<Card> TEST_CARD_LIST = new ArrayList<Card>(Arrays.asList(TEST_CARD));

    private static final String TEST_SPRINT_ID = "Test Stat ID";
    private static final String TEST_SPRINT_NAME = "Test Stat Name";
    private static final String TEST_SPRINT_PRODUCT = "Test Sprint Product";

    private static final Sprint TEST_SPRINT = new Sprint(TEST_SPRINT_ID, TEST_SPRINT_NAME, TEST_SPRINT_PRODUCT, TEST_CARD_LIST);

    private static final List<Sprint> TEST_SPRINT_LIST = new ArrayList<Sprint>(Arrays.asList(TEST_SPRINT));

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

        when(activeSprintsScheduledCacheService.get()).thenReturn(TEST_SPRINT_LIST);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testActiveSprintsCacheGet() throws Exception {
        // @formatter:off
        mockMvc.perform(
            get("/sprints/active")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "sprints/active",
                    responseFields(
                        fieldWithPath("meta").description("API response meta."),
                        fieldWithPath("meta.id").description("ID of the request."),
                        fieldWithPath("meta.action").description("Action of the request."),
                        fieldWithPath("meta.message").description("Message of the response."),
                        fieldWithPath("meta.status").description("Status of the response."),
                        fieldWithPath("payload").description("API response payload containing the array of Sprints."),
                        fieldWithPath("payload['ArrayList<Sprint>']").description("The array of Sprints."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].id").description("The Sprint ID."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].name").description("The Sprint Name."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].product").description("The Sprint Product."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards").description("An array of Sprint Cards."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].id").description("The Sprint Card ID."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].number").description("The Sprint Card Number."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].type").description("The Sprint Card Type."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].status").description("The Sprint Card Status."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].name").description("The Sprint Card Name."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].description").description("The Description of the Sprint Card."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].estimate").description("A decimal digit representing the Sprint Card estimate."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].assignees").description("An array of Members associated with the Sprint Card."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].assignees[0].id").description("The Member ID."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].assignees[0].name").description("The Member Name."),
                        fieldWithPath("payload['ArrayList<Sprint>'][0].cards[0].assignees[0].avatar").description("The Member Avatar.")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testActiveSprintsCacheUpdate() throws Exception {
        // @formatter:off
        mockMvc.perform(
            get("/sprints/active/update")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(
                document(
                    "sprints/active/update",
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
