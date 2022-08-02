package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
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
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductScheduledCache;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.InternalRequest;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.ticketing.SugarService;
import edu.tamu.weaver.response.ApiResponse;

@ExtendWith(SpringExtension.class)
public class ProductControllerTest {

    private static final Long TEST_PRODUCT1_ID = 1L;
    private static final Long TEST_PRODUCT2_ID = 2L;

    private static final Long TEST_INVALID_PRODUCT_ID = 3L;

    private static final Long TEST_REMOTE_PROJECT_MANAGER_ONE_ID = 1L;

    private static final String TEST_PRODUCT1_NAME = "Test Product 1 Name";
    private static final String TEST_PRODUCT2_NAME = "Test Product 2 Name";
    private static final String TEST_PRODUCT3_NAME = "Test Product 3 Name";

    private static final String TEST_MODIFIED_PRODUCT_NAME = "Modified Product Name";

    private static final String TEST_PROJECT1_SCOPE1 = "0010";
    private static final String TEST_PROJECT1_SCOPE2 = "0011";
    private static final String TEST_INVALID_SCOPE = "XXXX";

    private static final String TEST_PROJECT_URL1 = "http://localhost/1";

    private static final String TEST_PROJECT_TOKEN1 = "0123456789";

    private static final String TEST_FEATURE_REQUEST_TITLE = "Test Feature Request Title";
    private static final String TEST_FEATURE_REQUEST_DESCRIPTION = "Test Feature Request Description";
    private static final String TEST_RMP_ONE_NAME = "Test Remote Project Manager 1";

    private static final String INVALID_RPM_ID_ERROR_MESSAGE_FIND_BY_ID = "Error fetching remote project with scope id " + TEST_INVALID_SCOPE + " from " + TEST_RMP_ONE_NAME + "!";
    private static final String INVALID_PRODUCT_ID_ERROR_MESSAGE = "Product with id " + TEST_INVALID_PRODUCT_ID + " not found!";
    private static final String INVALID_RPM_ID_ERROR_MESSAGE = "Error fetching remote projects from " + TEST_RMP_ONE_NAME + "!";
    private static final String MISSING_RPM_ERROR_MESSAGE = "Remote Project Manager with id " + TEST_REMOTE_PROJECT_MANAGER_ONE_ID + " not found!";

    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PROJECT1_SCOPE1, TEST_REMOTE_PROJECT_MANAGER);
    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO2 = new RemoteProjectInfo(TEST_PROJECT1_SCOPE2, TEST_REMOTE_PROJECT_MANAGER);

    private static final List<RemoteProjectInfo> TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1, TEST_REMOTE_PROJECT_INFO2));

    private static Instant TEST_INSTANT_NOW = new Date().toInstant();
    private static Date TEST_CREATED_ON1 = Date.from(TEST_INSTANT_NOW);

    private static Product TEST_PRODUCT1 = new Product(TEST_PRODUCT1_NAME, TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST);
    private static Product TEST_PRODUCT2 = new Product(TEST_PRODUCT2_NAME);
    private static Product TEST_PRODUCT3 = new Product(TEST_PRODUCT3_NAME);
    private static Product TEST_MODIFIED_PRODUCT = new Product(TEST_MODIFIED_PRODUCT_NAME);

    private static RemoteProjectManager testRemoteProjectManagerOne = new RemoteProjectManager(TEST_RMP_ONE_NAME, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);

    private static final InternalRequest TEST_REQUEST1 = new InternalRequest(TEST_FEATURE_REQUEST_TITLE, TEST_FEATURE_REQUEST_DESCRIPTION, TEST_PRODUCT1, TEST_CREATED_ON1);

    private static TicketRequest TEST_TICKET_REQUEST = new TicketRequest();

    private static FeatureRequest TEST_FEATURE_REQUEST = new FeatureRequest(TEST_FEATURE_REQUEST_TITLE, TEST_FEATURE_REQUEST_DESCRIPTION, TEST_PRODUCT1_ID, TEST_PROJECT1_SCOPE1);

    private static List<Product> mockProductList = new ArrayList<Product>(Arrays.asList(new Product[] { TEST_PRODUCT1, TEST_PRODUCT2 }));

    private static ApiResponse apiResponse;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private InternalRequestRepo internalRequestRepo;

    @Mock
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Mock
    private SugarService sugarService;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private RemoteProjectManagerBean remoteProjectManagementBean;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @InjectMocks
    private ProductController productController = new ProductController();

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(productRepo.findAll()).thenReturn(mockProductList);
        when(productRepo.create(any(Product.class))).thenReturn(TEST_PRODUCT1);
        when(productRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        when(productRepo.update(any(Product.class))).thenReturn(TEST_MODIFIED_PRODUCT);
        when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_REMOTE_PROJECT_MANAGER));
        doNothing().when(productRepo).delete(any(Product.class));
        when(sugarService.submit(any(TicketRequest.class))).thenReturn("Successfully submitted issue for test service!");
        when(internalRequestRepo.create(any(InternalRequest.class))).thenReturn(TEST_REQUEST1);

        TEST_PRODUCT1.setId(TEST_PRODUCT1_ID);
        TEST_PRODUCT2.setId(TEST_PRODUCT2_ID);

        testRemoteProjectManagerOne.setId(TEST_REMOTE_PROJECT_MANAGER_ONE_ID);

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
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Not successful at getting requested Product");
        List<Product> products = (List<Product>) apiResponse.getPayload().get("ArrayList<Product>");
        assertEquals(mockProductList, products, "Did not get the expected Products");
    }

    @Test
    public void testGetProductById() {
        when(productRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_PRODUCT1));
        apiResponse = productController.getOne(TEST_PRODUCT1_ID);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Not successful at getting requested Product");
        Product product = (Product) apiResponse.getPayload().get("Product");
        assertEquals(TEST_PRODUCT1, product, "Did not get the expected Product");
    }

    @Test
    public void testCreate() {
        apiResponse = productController.createProduct(TEST_PRODUCT3);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Not successful at creating Product");
    }

    @Test
    public void testUpdate() {
        apiResponse = productController.updateProduct(TEST_MODIFIED_PRODUCT);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Not successful at updating Product");
        Product product = (Product) apiResponse.getPayload().get("Product");
        assertEquals(TEST_MODIFIED_PRODUCT.getName(), product.getName(), "Product title was not properly updated");
    }

    @Test
    public void testDelete() {
        apiResponse = productController.deleteProduct(TEST_PRODUCT3);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Not successful at deleting Product");
    }

    @Test
    public void testDeleteWithAssociatedInternalRequests() {
        String message = "Cannot delete Product " + TEST_PRODUCT1.getName() + " because it has one or more associated Internal Requests.";
        when(internalRequestRepo.countByProductId(any(Long.class))).thenReturn(1L);

        apiResponse = productController.deleteProduct(TEST_PRODUCT1);
        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Should not delete when there are associated Internal Requests");
        assertEquals(message, apiResponse.getMeta().getMessage(), "Should not delete with friendly message when there are associated Internal Requests");
    }

    @Test
    public void testSubmitIssueRequest() {
        apiResponse = productController.submitIssueRequest(TEST_TICKET_REQUEST);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Not successful at submitting issue request");
    }

    @Test
    public void testPushRequest() throws Exception {
        when(remoteProjectManagementBean.push(TEST_FEATURE_REQUEST)).thenReturn(TEST_FEATURE_REQUEST.getScopeId());
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);
        when(productRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_PRODUCT1));
        apiResponse = productController.pushRequest(TEST_FEATURE_REQUEST);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Product controller did not push request");
    }

    @Test
    public void testGetAllRemoteProductsForProduct() throws Exception {
        when(remoteProjectManagementBean.push(TEST_FEATURE_REQUEST)).thenReturn(TEST_FEATURE_REQUEST.getScopeId());
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);
        when(productRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_PRODUCT1));
        apiResponse = productController.getAllRemoteProjectsForProduct(TEST_PRODUCT1_ID);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Remote Project controller unable to get all remote products for the specified product");
    }

    @Test
    public void testGetAllRemoteProductsForProductWithInvalidId() throws Exception {
        when(remoteProjectManagementBean.push(TEST_FEATURE_REQUEST)).thenReturn(TEST_FEATURE_REQUEST.getScopeId());
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);
        apiResponse = productController.getAllRemoteProjectsForProduct(3L);
        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Request with invalid Product id did not result in an error");
        assertEquals(INVALID_PRODUCT_ID_ERROR_MESSAGE, apiResponse.getMeta().getMessage(), "Invalid Product id did not result in the expected error message");
    }

    @Test
    public void testGetAllRemoteProductsForProductWithNoRemoteProductManager() {
        when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        when(productRepo.findById(any(Long.class))).thenReturn(Optional.of(TEST_PRODUCT1));
        apiResponse = productController.getAllRemoteProjectsForProduct(TEST_PRODUCT1_ID);
        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Request without Remote Project Manager did not result in an error");
        assertEquals("Error fetching remote projects associated with product " + TEST_PRODUCT1.getName() + "!", apiResponse.getMeta().getMessage(), "Missing Remote Project Manager did not result in the expected error message");
    }

    @Test
    public void testGetAllRemoteProjects() throws Exception {
        when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(testRemoteProjectManagerOne));
        when(remoteProjectManagementBean.push(TEST_FEATURE_REQUEST)).thenReturn("1");
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);
        apiResponse = productController.getAllRemoteProjects(TEST_REMOTE_PROJECT_MANAGER_ONE_ID);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Remote Project Manager controller unable to get all remote projects");
    }

    @Test
    public void testGetAllRemoteProjectsWithInvalidRemoteProjectManager() {
        when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(testRemoteProjectManagerOne));
        apiResponse = productController.getAllRemoteProjects(TEST_REMOTE_PROJECT_MANAGER_ONE_ID);
        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Request with invalid Remote Project Manager id did not result in an error");
        assertEquals(INVALID_RPM_ID_ERROR_MESSAGE, apiResponse.getMeta().getMessage(), "Invalid Remote Project Manager id did not result in the expected error message");
    }

    @Test
    public void testGetAllRemoteProjectsWithNoRemoteProjectManager() {
        when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        apiResponse = productController.getAllRemoteProjects(TEST_REMOTE_PROJECT_MANAGER_ONE_ID);
        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Request without Remote Project Manager did not result in an error");
        assertEquals(MISSING_RPM_ERROR_MESSAGE, apiResponse.getMeta().getMessage(), "Missing Remote Project Manager did not result in the expected error message");
    }

    @Test
    public void testGetRemoteProjectByScopeId() throws Exception {
        when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(testRemoteProjectManagerOne));
        when(remoteProjectManagementBean.getRemoteProjectByScopeId(TEST_PROJECT1_SCOPE1)).thenReturn(new RemoteProject());
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);
        apiResponse = productController.getRemoteProjectByScopeId(TEST_REMOTE_PROJECT_MANAGER_ONE_ID, TEST_PROJECT1_SCOPE1);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Product controller unable to get remote project by scope id");
    }

    @Test
    public void testGetRemoteProjectByScopeIdWithInvalidScope() {
        when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(testRemoteProjectManagerOne));
        apiResponse = productController.getRemoteProjectByScopeId(TEST_REMOTE_PROJECT_MANAGER_ONE_ID, TEST_INVALID_SCOPE);
        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Request with invalid Remote Project Manager id did not result in an error");
        assertEquals(INVALID_RPM_ID_ERROR_MESSAGE_FIND_BY_ID, apiResponse.getMeta().getMessage(), "Invalid Remote Project Manager id did not result in the expected error message");
    }

    @Test
    public void testGetRemoteProjectByScopeIdWithMissingRemoteProjectManager() {
        when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.empty());
        apiResponse = productController.getRemoteProjectByScopeId(TEST_REMOTE_PROJECT_MANAGER_ONE_ID, TEST_PROJECT1_SCOPE1);
        assertEquals(ERROR, apiResponse.getMeta().getStatus(), "Request with no Remote Project Manager did not result in an error");
        assertEquals(MISSING_RPM_ERROR_MESSAGE, apiResponse.getMeta().getMessage(), "Missing Remote Project Manager did not result in the expected error message");
    }

}
