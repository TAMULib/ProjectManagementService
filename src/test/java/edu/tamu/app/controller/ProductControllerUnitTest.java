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
import org.springframework.test.util.ReflectionTestUtils;

import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductScheduledCache;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
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
import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.manager.RemoteProductManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.ticketing.SugarService;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class ProductControllerUnitTest {

    private static final String TEST_PRODUCT1_NAME = "Test Product 1 Name";
    private static final String TEST_PRODUCT1_SCOPE1 = "0010";
    private static final String TEST_PRODUCT1_SCOPE2 = "0011";
    private static final String TEST_PRODUCT2_NAME = "Test Product 2 Name";
    private static final String TEST_MODIFIED_PRODUCT_NAME = "Modified Product Name";
    private static final String TEST_FEATURE_REQUEST_TITLE = "Test Feature Request Title";
    private static final String TEST_FEATURE_REQUEST_DESCRIPTION = "Test Feature Request Description";

    private static final InternalRequest TEST_REQUEST_NAME = new InternalRequest(TEST_FEATURE_REQUEST_TITLE, TEST_FEATURE_REQUEST_DESCRIPTION);

    private static final String INVALID_RPM_ID_ERROR_MESSAGE = "Error fetching remote products from Test Remote Product Manager!";
    private static final String MISSING_RPM_ERROR_MESSAGE = "Remote Product Manager with id null not found!";
    private static final String INVALID_RPM_ID_ERROR_MESSAGE_FIND_BY_ID = "Error fetching remote product with scope id " + TEST_PRODUCT1_SCOPE1 + " from Test Remote Product Manager!";

    private static final RemoteProductManager TEST_REMOTE_PRODUCT_MANAGER = new RemoteProductManager("Test Remote Product Manager", ServiceType.VERSION_ONE, new HashMap<String, String>());

    private static final RemoteProductInfo TEST_REMOTE_PRODUCT_INFO1 = new RemoteProductInfo(TEST_PRODUCT1_SCOPE1, TEST_REMOTE_PRODUCT_MANAGER);
    private static final RemoteProductInfo TEST_REMOTE_PRODUCT_INFO2 = new RemoteProductInfo(TEST_PRODUCT1_SCOPE2, TEST_REMOTE_PRODUCT_MANAGER);

    private static final List<RemoteProductInfo> TEST_PRODUCT1_REMOTE_PRODUCT_INFO_LIST = new ArrayList<RemoteProductInfo>(Arrays.asList(TEST_REMOTE_PRODUCT_INFO1, TEST_REMOTE_PRODUCT_INFO2));

    private static Product TEST_PRODUCT1 = new Product(TEST_PRODUCT1_NAME, TEST_PRODUCT1_REMOTE_PRODUCT_INFO_LIST);
    private static Product TEST_PRODUCT2 = new Product(TEST_PRODUCT2_NAME);
    private static Product TEST_MODIFIED_PRODUCT = new Product(TEST_MODIFIED_PRODUCT_NAME);

    private static TicketRequest TEST_TICKET_REQUEST = new TicketRequest();

    private static FeatureRequest TEST_FEATURE_REQUEST = new FeatureRequest(TEST_FEATURE_REQUEST_TITLE, TEST_FEATURE_REQUEST_DESCRIPTION, TEST_PRODUCT1.getId(), TEST_PRODUCT1_SCOPE1);

    private static List<Product> mockProductList = new ArrayList<Product>(Arrays.asList(new Product[] { TEST_PRODUCT1, TEST_PRODUCT2 }));

    private static ApiResponse apiResponse;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private InternalRequestRepo internalRequestRepo;

    @Mock
    private RemoteProductManagerRepo remoteProductManagerRepo;

    @Mock
    private SugarService sugarService;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private RemoteProductManagerBean remoteProductManagementBean;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private RemoteProductsScheduledCacheService remoteProductsScheduledCacheService;

    @InjectMocks
    private ProductController productController = new ProductController();

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(productRepo.findAll()).thenReturn(mockProductList);
        when(productRepo.create(any(Product.class))).thenReturn(TEST_PRODUCT1);
        when(productRepo.findOne(any(Long.class))).thenReturn(null);
        when(productRepo.update(any(Product.class))).thenReturn(TEST_MODIFIED_PRODUCT);
        when(remoteProductManagerRepo.findOne(any(Long.class))).thenReturn(TEST_REMOTE_PRODUCT_MANAGER);
        doNothing().when(productRepo).delete(any(Product.class));
        when(sugarService.submit(any(TicketRequest.class))).thenReturn("Successfully submitted issue for test service!");
        when(internalRequestRepo.create(any(InternalRequest.class))).thenReturn(TEST_REQUEST_NAME);

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

        List<ProductScheduledCache<?, ?>> productSceduledCaches = new ArrayList<ProductScheduledCache<?, ?>>() {
            private static final long serialVersionUID = 621069988291823739L;
            {
                add(productsStatsScheduledCacheService);
                add(activeSprintsScheduledCacheService);
            }
        };

        ReflectionTestUtils.setField(productController, "productSceduledCaches", productSceduledCaches);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetAllProducts() {
        apiResponse = productController.getAll();
        assertEquals("Not successful at getting requested Product", SUCCESS, apiResponse.getMeta().getStatus());
        List<Product> products = (List<Product>) apiResponse.getPayload().get("ArrayList<Product>");
        assertEquals("Did not get the expected Products", mockProductList, products);
    }

    @Test
    public void testGetProductById() {
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);
        apiResponse = productController.getOne(TEST_PRODUCT1.getId());
        assertEquals("Not successful at getting requested Product", SUCCESS, apiResponse.getMeta().getStatus());
        Product product = (Product) apiResponse.getPayload().get("Product");
        assertEquals("Did not get the expected Product", TEST_PRODUCT1, product);
    }

    @Test
    public void testCreate() {
        apiResponse = productController.createProduct(TEST_PRODUCT1);
        assertEquals("Not successful at creating Product", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testUpdate() {
        apiResponse = productController.updateProduct(TEST_MODIFIED_PRODUCT);
        assertEquals("Not successful at updating Product", SUCCESS, apiResponse.getMeta().getStatus());
        Product product = (Product) apiResponse.getPayload().get("Product");
        assertEquals("Product title was not properly updated", TEST_MODIFIED_PRODUCT.getName(), product.getName());
    }

    @Test
    public void testDelete() {
        apiResponse = productController.deleteProduct(TEST_PRODUCT1);
        assertEquals("Not successful at deleting Product", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testSubmitIssueRequest() {
        apiResponse = productController.submitIssueRequest(TEST_TICKET_REQUEST);
        assertEquals("Not successful at submitting issue request", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testPushRequest() throws Exception {
        when(remoteProductManagementBean.push(TEST_FEATURE_REQUEST)).thenReturn(TEST_FEATURE_REQUEST);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProductManagementBean);
        when(productRepo.findOne(any(Long.class))).thenReturn(TEST_PRODUCT1);
        apiResponse = productController.pushRequest(TEST_FEATURE_REQUEST);
        assertEquals("Product controller did not push request", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testGetAllRemoteProducts() throws Exception {
        when(remoteProductManagementBean.push(TEST_FEATURE_REQUEST)).thenReturn(TEST_FEATURE_REQUEST);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProductManagementBean);
        apiResponse = productController.getAllRemoteProducts(TEST_REMOTE_PRODUCT_MANAGER.getId());
        assertEquals("Product controller unable to get all remote products", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testGetAllRemoteProductsWithInvalidRemoteProductManager() {
        apiResponse = productController.getAllRemoteProducts(TEST_REMOTE_PRODUCT_MANAGER.getId());
        assertEquals("Request with invalid Remote Product Manager id did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Invalid Remote Product Manager id did not result in the expected error message", INVALID_RPM_ID_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetAllRemoteProductsWithNoRemoteProductManager() {
        when(remoteProductManagerRepo.findOne(any(Long.class))).thenReturn(null);
        apiResponse = productController.getAllRemoteProducts(TEST_REMOTE_PRODUCT_MANAGER.getId());
        assertEquals("Request without Remote Product Manager did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Missing Remote Product Manager did not result in the expected error message", MISSING_RPM_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetRemoteProductByScopeId() throws Exception {
        when(remoteProductManagementBean.getRemoteProductByScopeId(TEST_PRODUCT1_SCOPE1)).thenReturn(new RemoteProduct());
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProductManagementBean);
        apiResponse = productController.getRemoteProductByScopeId(TEST_REMOTE_PRODUCT_MANAGER.getId(), TEST_PRODUCT1_SCOPE1);
        assertEquals("Product controller unable to get remote product by scope id", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testGetRemoteProductByScopeIdWithInvalidRemoteProductManager() {
        apiResponse = productController.getRemoteProductByScopeId(TEST_REMOTE_PRODUCT_MANAGER.getId(), TEST_PRODUCT1_SCOPE1);
        assertEquals("Request with invalid Remote Product Manager id did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Invalid Remote Product Manager id did not result in the expected error message", INVALID_RPM_ID_ERROR_MESSAGE_FIND_BY_ID, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetRemoteProductByScopeIdWithMissingRemoteProductManager() {
        when(remoteProductManagerRepo.findOne(any(Long.class))).thenReturn(null);
        apiResponse = productController.getRemoteProductByScopeId(TEST_REMOTE_PRODUCT_MANAGER.getId(), TEST_PRODUCT1_SCOPE1);
        assertEquals("Request with no Remote Product Manager did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Missing Remote Product Manager did not result in the expected error message", MISSING_RPM_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

}