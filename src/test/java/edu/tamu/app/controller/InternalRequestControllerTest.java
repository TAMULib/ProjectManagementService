package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

import edu.tamu.app.cache.service.RemoteProductsScheduledCacheService;
import edu.tamu.app.model.InternalRequest;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProductInfo;
import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProductManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.service.manager.RemoteProductManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class InternalRequestControllerTest {

    private static final String TEST_REQUEST_TITLE_BELLS = "Test Feature Request Title Bells";
    private static final String TEST_REQUEST_TITLE_WHISTLES = "Test Feature Request Title Whistles";
    private static final String TEST_REQUEST_DESCRIPTION_BELLS = "Test Feature Request Description Bells";
    private static final String TEST_REQUEST_DESCRIPTION_WHISTLES = "Test Feature Request Description Whistles";

    private static final String TEST_PRODUCT1_NAME = "Test Product 1 Name";
    private static final String TEST_PRODUCT1_SCOPE1 = "0010";
    private static final String TEST_PRODUCT1_SCOPE2 = "0011";
    private static final String TEST_PRODUCT2_NAME = "Test Product 2 Name";
    private static final String TEST_PRODUCT_WITHOUT_RPM_NAME = "Test Product Without Remote Product Manager Name";

    private static final String PUSH_ERROR_MESSAGE = "Error pushing request to Test Remote Product Manager for product Test Product 1 Name!";
    private static final String NO_RPM_ERROR_MESSAGE = "Test Product Without Remote Product Manager Name product does not have a Remote Product Manager!";
    private static final String NO_PRODUCT_ERROR_MESSAGE = "Product with id null not found!";

    private static final RemoteProductManager TEST_REMOTE_PRODUCT_MANAGER = new RemoteProductManager("Test Remote Product Manager", ServiceType.VERSION_ONE, new HashMap<String, String>());

    private static final RemoteProductInfo TEST_REMOTE_PRODUCT_INFO_1 = new RemoteProductInfo(TEST_PRODUCT1_SCOPE1, TEST_REMOTE_PRODUCT_MANAGER);
    private static final RemoteProductInfo TEST_REMOTE_PRODUCT_INFO_2 = new RemoteProductInfo(TEST_PRODUCT1_SCOPE2, TEST_REMOTE_PRODUCT_MANAGER);
    private static final RemoteProductInfo TEST_REMOTE_PRODUCT_INVALID_RPM = new RemoteProductInfo(TEST_PRODUCT1_SCOPE2, null);
    private static final RemoteProductInfo TEST_REMOTE_PRODUCT_INVALID_SCOPE = new RemoteProductInfo(null, TEST_REMOTE_PRODUCT_MANAGER);

    private static final List<RemoteProductInfo> TEST_PRODUCT1_REMOTE_PRODUCT_INFO_LIST = new ArrayList<RemoteProductInfo>(Arrays.asList(TEST_REMOTE_PRODUCT_INFO_1, TEST_REMOTE_PRODUCT_INFO_2)
    );

    private static Product TEST_PRODUCT1 = new Product(TEST_PRODUCT1_NAME, TEST_PRODUCT1_REMOTE_PRODUCT_INFO_LIST);
    private static Product TEST_PRODUCT2 = new Product(TEST_PRODUCT2_NAME);
    private static Product TEST_PRODUCT_WIHTOUT_RPM = new Product(TEST_PRODUCT_WITHOUT_RPM_NAME);

    private static FeatureRequest TEST_FEATURE_REQUEST = new FeatureRequest(TEST_REQUEST_TITLE_BELLS, TEST_REQUEST_DESCRIPTION_BELLS, TEST_PRODUCT1.getId(), TEST_PRODUCT1_SCOPE1);

    private static Date TEST_REQUEST_CREATED_ON_BELLS = Date.from(Instant.now().minusSeconds(30L));
    private static Date TEST_REQUEST_CREATED_ON_WHISTLES = Date.from(Instant.now());

    private static InternalRequest TEST_REQUEST_BELLS = new InternalRequest(TEST_REQUEST_TITLE_BELLS, TEST_REQUEST_DESCRIPTION_BELLS, TEST_REQUEST_CREATED_ON_BELLS);
    private static InternalRequest TEST_REQUEST_WHISTLES = new InternalRequest(TEST_REQUEST_TITLE_WHISTLES, TEST_REQUEST_DESCRIPTION_WHISTLES, TEST_REQUEST_CREATED_ON_WHISTLES);

    private static List<Product> mockProductList = new ArrayList<Product>(Arrays.asList(new Product[] { TEST_PRODUCT1, TEST_PRODUCT2 }));

    private static List<InternalRequest> mockRequestsRepo;

    private ApiResponse apiResponse;

    @Mock
    private InternalRequestRepo internalRequestRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private RemoteProductManagerRepo remoteProductManagerRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private RemoteProductManagerBean remoteProductManagementBean;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private RemoteProductsScheduledCacheService remoteProductsScheduledCacheService;

    @InjectMocks
    private InternalRequestController internalRequestController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        TEST_PRODUCT1.setId(1L);
        TEST_PRODUCT2.setId(2L);

        TEST_REQUEST_BELLS.setId(1L);
        TEST_REQUEST_WHISTLES.setId(2L);

        mockRequestsRepo = new ArrayList<InternalRequest>(Arrays.asList(new InternalRequest[] { TEST_REQUEST_BELLS, TEST_REQUEST_WHISTLES }));

        when(internalRequestRepo.findAll()).thenReturn(mockRequestsRepo);
        when(internalRequestRepo.count()).thenReturn((long) mockRequestsRepo.size());
        when(internalRequestRepo.create(any(InternalRequest.class))).thenReturn(TEST_REQUEST_BELLS);
        when(internalRequestRepo.update(any(InternalRequest.class))).thenReturn(TEST_REQUEST_BELLS);
        when(internalRequestRepo.findOne(any(Long.class))).thenReturn(TEST_REQUEST_BELLS);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProductManagementBean);

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

        when(remoteProductManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PRODUCT_MANAGER);
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
    public void testPush() throws Exception {
        int initialCount = mockRequestsRepo.size();

        when(remoteProductManagementBean.push(any(FeatureRequest.class))).thenReturn(TEST_FEATURE_REQUEST);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProductManagementBean);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), TEST_REMOTE_PRODUCT_INFO_1);

        assertEquals("Product controller did not push request", SUCCESS, apiResponse.getMeta().getStatus());
        assertEquals("InternalRequest should be deleted after successful push", initialCount - 1, mockRequestsRepo.size());
    }

    @Test
    public void testPushToInvalidRemoteProductManager() {
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), TEST_REMOTE_PRODUCT_INVALID_RPM);

        assertEquals("Invalid push did not throw an exception", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Push without Remote Product Manager did not result in the expected error", PUSH_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
        assertEquals("InternalRequest should not be deleted after failed push", 2, internalRequestRepo.count());
    }

    @Test
    public void testPushInvalidScope() {
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT_WIHTOUT_RPM);

        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), TEST_PRODUCT1.getId(), TEST_REMOTE_PRODUCT_INVALID_SCOPE);

        assertEquals("Push without Remote Product Manager did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Push without Remote Product Manager did not result in the expected error", NO_RPM_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
        assertEquals("InternalRequest should not be deleted after failed push", 2, internalRequestRepo.count());
    }

    @Test
    public void testPushInvalidProduct() {
        apiResponse = internalRequestController.push(TEST_REQUEST_BELLS.getId(), 0L, TEST_REMOTE_PRODUCT_INFO_1);

        assertEquals("Push without Product did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Push without Product did not result in the expected error", NO_PRODUCT_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
        assertEquals("InternalRequest should not be deleted after failed push", 2, internalRequestRepo.count());
    }

}