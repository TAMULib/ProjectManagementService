package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

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

@RunWith(SpringRunner.class)
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

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        TEST_PRODUCT1.setId(1L);
        TEST_PRODUCT2.setId(2L);

        TEST_REQUEST_BELLS.setId(1L);
        TEST_REQUEST_WHISTLES.setId(2L);

        mockRequestsRepo = new ArrayList<InternalRequest>(Arrays.asList(new InternalRequest[] { TEST_REQUEST_BELLS, TEST_REQUEST_WHISTLES }));

        when(internalRequestRepo.findAll()).thenReturn(mockRequestsRepo);
        when(internalRequestRepo.count()).thenReturn(2L);
        when(internalRequestRepo.countByProductId(any(Long.class))).thenReturn(1L);
        when(internalRequestRepo.countByProductIsNull()).thenReturn(1L);
        when(internalRequestRepo.create(any(InternalRequest.class))).thenReturn(TEST_REQUEST_BELLS);
        when(internalRequestRepo.update(any(InternalRequest.class))).thenReturn(TEST_REQUEST_BELLS);
        when(remoteProjectManagementBean.push(any(FeatureRequest.class))).thenReturn("1");
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);

        doAnswer(new Answer<ApiResponse>() {
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

        when(productRepo.findAll()).thenReturn(mockProductList);

        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PROJECT_MANAGER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRead() {
        ApiResponse apiResponse = internalRequestController.read();

        assertEquals("Request for Internal Request was unsuccessful", SUCCESS, apiResponse.getMeta().getStatus());
        assertEquals("Number of Internal Requests was not correct", 2, ((ArrayList<InternalRequest>) apiResponse.getPayload().get("ArrayList<InternalRequest>")).size());
    }

    @Test
    public void testReadById() {
        when(internalRequestRepo.findOne(TEST_REQUEST_BELLS.getId())).thenReturn(TEST_REQUEST_BELLS);

        ApiResponse apiResponse = internalRequestController.read(TEST_REQUEST_BELLS.getId());

        assertEquals("Request for Internal Request was unsuccessful", SUCCESS, apiResponse.getMeta().getStatus());
        assertEquals("Internal Request read was incorrect", TEST_REQUEST_BELLS.getTitle(), ((InternalRequest) apiResponse.getPayload().get("InternalRequest")).getTitle());
    }

    @Test
    public void testCreate() {
        ApiResponse apiResponse = internalRequestController.create(TEST_REQUEST_BELLS);

        assertEquals("Internal Request was not successfully created", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testUpdate() {
        ApiResponse apiResponse = internalRequestController.update(TEST_REQUEST_BELLS);

        assertEquals("Internal Request was not successfully updated", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testDelete() {
        ApiResponse apiResponse = internalRequestController.delete(TEST_REQUEST_WHISTLES);

        assertEquals("Internal Request was not successfully deleted", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testPush() {
        int initialCount = mockRequestsRepo.size();

        when(internalRequestRepo.findOne(any(Long.class))).thenReturn(TEST_REQUEST_BELLS);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PROJECT_MANAGER);
        doNothing().when(internalRequestRepo).delete(any(Long.class));

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), TEST_REMOTE_PROJECT_MANAGER.getId(), TEST_REMOTE_PROJECT_INFO1.getScopeId());

        assertEquals("Internal Request controller did not push request", SUCCESS, apiResponse.getMeta().getStatus());
        assertEquals("InternalRequest should be deleted after successful push", initialCount - 1, mockRequestsRepo.size());
    }

    @Test
    public void testPushToInvalidRemoteProductManager() {
        when(internalRequestRepo.findOne(any(Long.class))).thenReturn(TEST_REQUEST_BELLS);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(null);

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), null, TEST_REMOTE_PROJECT_INFO1.getScopeId());

        String expectedMessage = "Remote Project Manager with id null not found!";

        assertEquals("Push without Remote Project Manager did not throw an exception", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Push without Remote Project Manager did not result in the expected error", expectedMessage, apiResponse.getMeta().getMessage());
        assertEquals("InternalRequest should not be deleted after failed push", 2, internalRequestRepo.count());
    }

    @Test
    public void testPushInvalidScope() {
        when(internalRequestRepo.findOne(any(Long.class))).thenReturn(TEST_REQUEST_BELLS);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PROJECT_MANAGER);

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), TEST_REMOTE_PROJECT_MANAGER.getId(), "");

        String expectedMessage = "Internal Request is missing the scope id!";

        assertEquals("Push with invalid Scope did not throw an exception", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Push with invalid Scope did not result in the expected error", expectedMessage, apiResponse.getMeta().getMessage());
        assertEquals("InternalRequest should not be deleted after failed push", 2, internalRequestRepo.count());
    }

    @Test
    public void testPushInvalidProduct() {
        when(internalRequestRepo.findOne(any(Long.class))).thenReturn(TEST_REQUEST_BELLS);
        when(productRepo.findOne(any(Long.class))).thenReturn(null);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PROJECT_MANAGER);

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), null, TEST_REMOTE_PROJECT_MANAGER.getId(), TEST_REMOTE_PROJECT_INFO1.getScopeId());

        String expectedMessage = "Product with id null not found!";

        assertEquals("Push with invalid Product did not throw an exception", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Push with invalid Product did not result in the expected error", expectedMessage, apiResponse.getMeta().getMessage());
        assertEquals("InternalRequest should not be deleted after failed push", 2, internalRequestRepo.count());
    }

    @Test
    public void testPushInvalidInternalRequest() {
        when(internalRequestRepo.findOne(any(Long.class))).thenReturn(null);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PROJECT_MANAGER);

        apiResponse = internalRequestController.push(null, TEST_PRODUCT1.getId(), TEST_REMOTE_PROJECT_MANAGER.getId(), TEST_REMOTE_PROJECT_INFO1.getScopeId());

        String expectedMessage = "Internal Request with id null not found!";

        assertEquals("Push with invalid Internal Request did not throw an exception", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Push with invalid Internal Request did not result in the expected error", expectedMessage, apiResponse.getMeta().getMessage());
        assertEquals("InternalRequest should not be deleted after failed push", 2, internalRequestRepo.count());
    }

    @Test
    public void testPushWhenRemoteProductManagerBeanFails() throws Exception {
        when(remoteProjectManagementBean.push(any(FeatureRequest.class))).thenThrow(new RuntimeException("fail"));
        when(internalRequestRepo.findOne(any(Long.class))).thenReturn(TEST_REQUEST_BELLS);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PROJECT_MANAGER);
        doNothing().when(internalRequestRepo).delete(any(Long.class));

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), TEST_REMOTE_PROJECT_MANAGER.getId(), TEST_REMOTE_PROJECT_INFO1.getScopeId());

        assertEquals("Pushing Internal Request when Remote Project Management Bean push fails did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Pushing Internal Request did not result in the expected error message", "Error pushing Internal Request to " + TEST_REMOTE_PROJECT_MANAGER.getName() + " for Product " + TEST_PRODUCT1_NAME + "!", apiResponse.getMeta().getMessage());
    }

    @Test
    public void testStatsAssigned() {
        ApiResponse apiResponse = internalRequestController.stats();

        assertEquals("Request for assigned Internal Request stats was unsuccessful", SUCCESS, apiResponse.getMeta().getStatus());
        assertEquals("Number of assigned Internal Requests was not correct", 1L, ((InternalStats) apiResponse.getPayload().get("InternalStats")).getAssignedCount());
    }

    @Test
    public void testStatsUnassigned() {
        ApiResponse apiResponse = internalRequestController.stats();

        assertEquals("Request for unassigned Internal Request stats was unsuccessful", SUCCESS, apiResponse.getMeta().getStatus());
        assertEquals("Number of unassigned Internal Requests was not correct", 1L, ((InternalStats) apiResponse.getPayload().get("InternalStats")).getUnassignedCount());
    }

    @Test
    public void testStatsTotal() {
        ApiResponse apiResponse = internalRequestController.stats();

        assertEquals("Request for Internal Request stats was unsuccessful", SUCCESS, apiResponse.getMeta().getStatus());
        assertEquals("Number of Internal Requests was not correct", 2L, ((InternalStats) apiResponse.getPayload().get("InternalStats")).getTotalCount());
    }
}
