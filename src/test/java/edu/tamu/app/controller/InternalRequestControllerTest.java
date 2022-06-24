package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.InternalRequest;
import edu.tamu.app.model.InternalStats;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.response.ApiResponse;

@ExtendWith(MockitoExtension.class)
public class InternalRequestControllerTest {

    private static final String TEST_REQUEST_TITLE_BELLS = "Test Feature Request Title Bells";
    private static final String TEST_REQUEST_TITLE_WHISTLES = "Test Feature Request Title Whistles";

    private static final String TEST_REQUEST_DESCRIPTION_BELLS = "Test Feature Request Description Bells";
    private static final String TEST_REQUEST_DESCRIPTION_WHISTLES = "Test Feature Request Description Whistles";

    private static final String TEST_PRODUCT1_NAME = "Test Product 1 Name";
    private static final String TEST_PRODUCT2_NAME = "Test Product 2 Name";

    private static final String TEST_PROJECT1_SCOPE1 = "0010";
    private static final String TEST_PROJECT1_SCOPE2 = "0011";

    private static final String TEST_PROJECT_URL1 = "http://localhost/1";

    private static final String TEST_PROJECT_TOKEN1 = "0123456789";

    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);

    static {
        TEST_REMOTE_PROJECT_MANAGER.setId(1L);
    }

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PROJECT1_SCOPE1, TEST_REMOTE_PROJECT_MANAGER);
    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO2 = new RemoteProjectInfo(TEST_PROJECT1_SCOPE2, TEST_REMOTE_PROJECT_MANAGER);

    private static final List<RemoteProjectInfo> TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1, TEST_REMOTE_PROJECT_INFO2)
    );

    private static Product TEST_PRODUCT1 = new Product(TEST_PRODUCT1_NAME, TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST);
    private static Product TEST_PRODUCT2 = new Product(TEST_PRODUCT2_NAME);

    private static Date TEST_REQUEST_CREATED_ON_BELLS = Date.from(Instant.now().minusSeconds(30L));
    private static Date TEST_REQUEST_CREATED_ON_WHISTLES = Date.from(Instant.now());

    private static InternalRequest TEST_REQUEST_BELLS = new InternalRequest(TEST_REQUEST_TITLE_BELLS, TEST_REQUEST_DESCRIPTION_BELLS, TEST_PRODUCT1, TEST_REQUEST_CREATED_ON_BELLS);
    private static InternalRequest TEST_REQUEST_WHISTLES = new InternalRequest(TEST_REQUEST_TITLE_WHISTLES, TEST_REQUEST_DESCRIPTION_WHISTLES, null, TEST_REQUEST_CREATED_ON_WHISTLES);

    private static List<Product> mockProductList = new ArrayList<Product>(Arrays.asList(new Product[] { TEST_PRODUCT1, TEST_PRODUCT2 }));

    private static List<InternalRequest> mockRequestsRepo;

    private ApiResponse apiResponse;

    @Mock
    private InternalRequestRepo internalRequestRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private RemoteProjectManagerBean remoteProjectManagementBean;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @InjectMocks
    private InternalRequestController internalRequestController;

    @BeforeEach
    public void setup() throws Exception {
        TEST_PRODUCT1.setId(1L);
        TEST_PRODUCT2.setId(2L);

        TEST_REQUEST_BELLS.setId(1L);
        TEST_REQUEST_WHISTLES.setId(2L);

        mockRequestsRepo = new ArrayList<InternalRequest>(Arrays.asList(new InternalRequest[] { TEST_REQUEST_BELLS, TEST_REQUEST_WHISTLES }));

        lenient().when(internalRequestRepo.findAll()).thenReturn(mockRequestsRepo);
        lenient().when(internalRequestRepo.count()).thenReturn(2L);
        lenient().when(internalRequestRepo.countByProductId(any(Long.class))).thenReturn(1L);
        lenient().when(internalRequestRepo.countByProductIsNull()).thenReturn(1L);
        lenient().when(internalRequestRepo.create(any(InternalRequest.class))).thenReturn(TEST_REQUEST_BELLS);
        lenient().when(internalRequestRepo.update(any(InternalRequest.class))).thenReturn(TEST_REQUEST_BELLS);
        lenient().when(remoteProjectManagementBean.push(any(FeatureRequest.class))).thenReturn("1");
        lenient().when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);

        lenient().doAnswer(new Answer<ApiResponse>() {
            @Override
            public ApiResponse answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                InternalRequest request = (InternalRequest) args[0];

                for (int i = 0; i < mockRequestsRepo.size(); i++) {
                    InternalRequest r = mockRequestsRepo.get(i);
                    if (r.getId() == request.getId()) {
                        mockRequestsRepo.remove(i);
                        return new ApiResponse(SUCCESS);
                    }
                }

                return new ApiResponse(ERROR);
            }
        }).when(internalRequestRepo).delete(any(InternalRequest.class));

        lenient().when(productRepo.findAll()).thenReturn(mockProductList);

        lenient().when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REMOTE_PROJECT_MANAGER));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRead() {
        ApiResponse apiResponse = internalRequestController.read();

        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Request for Internal Request was unsuccessful");
        assertEquals(2, ((ArrayList<InternalRequest>) apiResponse.getPayload().get("ArrayList<InternalRequest>")).size(), "Number of Internal Requests was not correct");
    }

    @Test
    public void testReadById() {
        lenient().when(internalRequestRepo.findById(TEST_REQUEST_BELLS.getId())).thenReturn(Optional.of(TEST_REQUEST_BELLS));

        ApiResponse apiResponse = internalRequestController.read(TEST_REQUEST_BELLS.getId());

        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Request for Internal Request was unsuccessful");
        assertEquals(TEST_REQUEST_BELLS.getTitle(), ((InternalRequest) apiResponse.getPayload().get("InternalRequest")).getTitle(), "Internal Request read was incorrect");
    }

    @Test
    public void testCreate() {
        ApiResponse apiResponse = internalRequestController.create(TEST_REQUEST_BELLS);

        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Internal Request was not successfully created");
    }

    @Test
    public void testUpdate() {
        ApiResponse apiResponse = internalRequestController.update(TEST_REQUEST_BELLS);

        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Internal Request was not successfully updated");
    }

    @Test
    public void testDelete() {
        ApiResponse apiResponse = internalRequestController.delete(TEST_REQUEST_WHISTLES);

        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Internal Request was not successfully deleted");
    }

    @Test
    public void testPush() {
        int initialCount = mockRequestsRepo.size();

        lenient().when(internalRequestRepo.findById((any(Long.class)))).thenReturn(Optional.of(TEST_REQUEST_BELLS));
        lenient().when(productRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_PRODUCT1));
        lenient().when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REMOTE_PROJECT_MANAGER));
        lenient().doNothing().when(internalRequestRepo).deleteById(any(Long.class));

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), TEST_REMOTE_PROJECT_MANAGER.getId(), TEST_REMOTE_PROJECT_INFO1.getScopeId());

        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Internal Request controller did not push request");
        assertEquals(initialCount - 1, mockRequestsRepo.size(), "InternalRequest should be deleted after successful push");
    }

    @Test
    public void testPushToInvalidRemoteProductManager() {
        lenient().when(internalRequestRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REQUEST_BELLS));
        lenient().when(productRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_PRODUCT1));
        lenient().when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(null);

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), null, TEST_REMOTE_PROJECT_INFO1.getScopeId());

        String expectedMessage = "Remote Project Manager with id null not found!";

        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Push without Remote Project Manager did not throw an exception");
        assertEquals(expectedMessage, apiResponse.getMeta().getMessage(), "Push without Remote Project Manager did not result in the expected error");
        assertEquals(2, internalRequestRepo.count(), "InternalRequest should not be deleted after failed push");
    }

    @Test
    public void testPushInvalidScope() {
        lenient().when(internalRequestRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REQUEST_BELLS));
        lenient().when(productRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_PRODUCT1));
        lenient().when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REMOTE_PROJECT_MANAGER));

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), TEST_REMOTE_PROJECT_MANAGER.getId(), "");

        String expectedMessage = "Internal Request is missing the scope id!";

        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Push with invalid Scope did not throw an exception");
        assertEquals(expectedMessage, apiResponse.getMeta().getMessage(), "Push with invalid Scope did not result in the expected error");
        assertEquals(2, internalRequestRepo.count(), "InternalRequest should not be deleted after failed push");
    }

    @Test
    public void testPushInvalidProduct() {
        lenient().when(internalRequestRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REQUEST_BELLS));
        lenient().when(productRepo.findById(any(Long.class))).thenReturn(null);
        lenient().when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REMOTE_PROJECT_MANAGER));

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), null, TEST_REMOTE_PROJECT_MANAGER.getId(), TEST_REMOTE_PROJECT_INFO1.getScopeId());

        String expectedMessage = "Product with id null not found!";

        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Push with invalid Product did not throw an exception");
        assertEquals(expectedMessage, apiResponse.getMeta().getMessage(), "Push with invalid Product did not result in the expected error");
        assertEquals(2, internalRequestRepo.count(), "InternalRequest should not be deleted after failed push");
    }

    @Test
    public void testPushInvalidInternalRequest() {
        lenient().when(internalRequestRepo.findById(any(Long.class))).thenReturn(null);
        lenient().when(productRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_PRODUCT1));
        lenient().when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REMOTE_PROJECT_MANAGER));

        apiResponse = internalRequestController.push(null, TEST_PRODUCT1.getId(), TEST_REMOTE_PROJECT_MANAGER.getId(), TEST_REMOTE_PROJECT_INFO1.getScopeId());

        String expectedMessage = "Internal Request with id null not found!";

        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Push with invalid Internal Request did not throw an exception");
        assertEquals(expectedMessage, apiResponse.getMeta().getMessage(), "Push with invalid Internal Request did not result in the expected error");
        assertEquals(2, internalRequestRepo.count(), "InternalRequest should not be deleted after failed push");
    }

    @Test
    public void testPushWhenRemoteProductManagerBeanFails() throws Exception {
        lenient().when(remoteProjectManagementBean.push(any(FeatureRequest.class))).thenThrow(new RuntimeException("fail"));
        lenient().when(internalRequestRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REQUEST_BELLS));
        lenient().when(productRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_PRODUCT1));
        lenient().when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REMOTE_PROJECT_MANAGER));
        lenient().doNothing().when(internalRequestRepo).deleteById(any(Long.class));

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), TEST_REMOTE_PROJECT_MANAGER.getId(), TEST_REMOTE_PROJECT_INFO1.getScopeId());

        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Pushing Internal Request when Remote Project Management Bean push fails did not result in an error");
        assertEquals("Error pushing Internal Request to " + TEST_REMOTE_PROJECT_MANAGER.getName() + " for Product " + TEST_PRODUCT1_NAME + "!", apiResponse.getMeta().getMessage(), "Pushing Internal Request did not result in the expected error message");
    }

    @Test
    public void testStatsAssigned() {
        ApiResponse apiResponse = internalRequestController.stats();

        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Request for assigned Internal Request stats was unsuccessful");
        assertEquals(1L, ((InternalStats) apiResponse.getPayload().get("InternalStats")).getAssignedCount(), "Number of assigned Internal Requests was not correct");
    }

    @Test
    public void testStatsUnassigned() {
        ApiResponse apiResponse = internalRequestController.stats();

        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Request for unassigned Internal Request stats was unsuccessful");
        assertEquals(1L, ((InternalStats) apiResponse.getPayload().get("InternalStats")).getUnassignedCount(), "Number of unassigned Internal Requests was not correct");
    }

    @Test
    public void testStatsTotal() {
        ApiResponse apiResponse = internalRequestController.stats();

        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Request for Internal Request stats was unsuccessful");
        assertEquals(2L, ((InternalStats) apiResponse.getPayload().get("InternalStats")).getTotalCount(), "Number of Internal Requests was not correct");
    }
}
