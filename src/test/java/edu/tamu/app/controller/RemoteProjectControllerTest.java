package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class RemoteProjectControllerTest {

    private static final String TEST_PRODUCT1_NAME = "Test Product 1 Name";
    private static final String TEST_PRODUCT2_NAME = "Test Product 2 Name";

    private static final String TEST_PROJECT1_SCOPE1 = "0010";
    private static final String TEST_PROJECT1_SCOPE2 = "0011";

    private static final String TEST_FEATURE_REQUEST_TITLE = "Test Feature Request Title";
    private static final String TEST_FEATURE_REQUEST_DESCRIPTION = "Test Feature Request Description";

    private static final String INVALID_PRODUCT_ID_ERROR_MESSAGE = "Product with id null not found!";

    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE, new HashMap<String, String>());

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PROJECT1_SCOPE1, TEST_REMOTE_PROJECT_MANAGER);
    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO2 = new RemoteProjectInfo(TEST_PROJECT1_SCOPE2, TEST_REMOTE_PROJECT_MANAGER);

    private static final List<RemoteProjectInfo> TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1, TEST_REMOTE_PROJECT_INFO2));

    private static Product TEST_PRODUCT1 = new Product(TEST_PRODUCT1_NAME, TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST);
    private static Product TEST_PRODUCT2 = new Product(TEST_PRODUCT2_NAME);

    private static FeatureRequest TEST_FEATURE_REQUEST = new FeatureRequest(TEST_FEATURE_REQUEST_TITLE, TEST_FEATURE_REQUEST_DESCRIPTION, TEST_PRODUCT1.getId(), TEST_PROJECT1_SCOPE1);

    private static List<Product> mockProductList = new ArrayList<Product>(Arrays.asList(new Product[] { TEST_PRODUCT1, TEST_PRODUCT2 }));

    private static ApiResponse apiResponse;

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

    @InjectMocks
    private RemoteProjectController remoteProjectController = new RemoteProjectController();

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(productRepo.findAll()).thenReturn(mockProductList);
        when(productRepo.create(any(Product.class))).thenReturn(TEST_PRODUCT1);
        when(productRepo.findOne(any(Long.class))).thenReturn(null);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PROJECT_MANAGER);
        doNothing().when(productRepo).delete(any(Product.class));

        TEST_PRODUCT1.setId(1L);
        TEST_PRODUCT2.setId(2L);

        ProductsStatsScheduledCacheService productsStatsScheduledCacheService = mock(ProductsStatsScheduledCacheService.class);

        ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService = mock(ActiveSprintsScheduledCacheService.class);

        doNothing().when(productsStatsScheduledCacheService).addProduct(any(Product.class));
        doNothing().when(productsStatsScheduledCacheService).updateProduct(any(Product.class));
        doNothing().when(productsStatsScheduledCacheService).removeProduct(any(Product.class));

        doNothing().when(activeSprintsScheduledCacheService).addProduct(any(Product.class));
        doNothing().when(activeSprintsScheduledCacheService).updateProduct(any(Product.class));
        doNothing().when(activeSprintsScheduledCacheService).removeProduct(any(Product.class));
    }

    @Test
    public void testGetAllRemoteProductsForProduct() throws Exception {
        when(remoteProjectManagementBean.push(TEST_FEATURE_REQUEST)).thenReturn(TEST_FEATURE_REQUEST);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);
        apiResponse = remoteProjectController.getAllForProduct(TEST_PRODUCT1.getId());
        assertEquals("Remote Project controller unable to get all remote products for the specified product", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testGetAllRemoteProductsForProductWithInvalidId() throws Exception {
        when(remoteProjectManagementBean.push(TEST_FEATURE_REQUEST)).thenReturn(TEST_FEATURE_REQUEST);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);
        apiResponse = remoteProjectController.getAllForProduct(null);
        assertEquals("Request with invalid Product id did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Invalid Product id did not result in the expected error message", INVALID_PRODUCT_ID_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetAllRemoteProductsForProductWithNoRemoteProductManager() {
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(null);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);
        apiResponse = remoteProjectController.getAllForProduct(TEST_PRODUCT1.getId());
        assertEquals("Request without Remote Project Manager did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Missing Remote Project Manager did not result in the expected error message", "Error fetching remote projects associated with product " + TEST_PRODUCT1.getName() + "!", apiResponse.getMeta().getMessage());
    }

}