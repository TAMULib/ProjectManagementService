package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.response.ApiResponse;

@ExtendWith(SpringExtension.class)
public class RemoteProjectManagerControllerTest {

    private static final String TEST_PRODUCT1_NAME = "Test Product 1 Name";
    private static final String TEST_PRODUCT2_NAME = "Test Product 2 Name";

    private static final String TEST_PROJECT1_SCOPE1 = "0010";
    private static final String TEST_PROJECT1_SCOPE2 = "0011";

    private static final String TEST_PROJECT_URL1 = "http://localhost/1";
    private static final String TEST_PROJECT_URL2 = "http://localhost/2";

    private static final String TEST_PROJECT_TOKEN1 = "0123456789";
    private static final String TEST_PROJECT_TOKEN2 = "9876543210";

    private static final String TEST_RMP_ONE_NAME = "Test Remote Project Manager 1";
    private static final String TEST_RMP_TWO_NAME = "Test Remote Project Manager 2";
    private static final String TEST_MODIFIED_RMP_NAME = "Modified Remote Project Manager";

    private static RemoteProjectManager testRemoteProjectManagerOne = new RemoteProjectManager(TEST_RMP_ONE_NAME, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);
    private static RemoteProjectManager testRemoteProjectManagerTwo = new RemoteProjectManager(TEST_RMP_TWO_NAME, ServiceType.VERSION_ONE, TEST_PROJECT_URL2, TEST_PROJECT_TOKEN2);
    private static RemoteProjectManager testModifiedProjectManager = new RemoteProjectManager(TEST_MODIFIED_RMP_NAME, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);
    private static List<RemoteProjectManager> mockRemoteProjectManagers = new ArrayList<RemoteProjectManager>(Arrays.asList(new RemoteProjectManager[] { testRemoteProjectManagerOne, testRemoteProjectManagerTwo }));

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PROJECT1_SCOPE1, testRemoteProjectManagerOne);
    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO2 = new RemoteProjectInfo(TEST_PROJECT1_SCOPE2, testRemoteProjectManagerOne);

    private static final List<RemoteProjectInfo> TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1, TEST_REMOTE_PROJECT_INFO2));

    private static Product TEST_PRODUCT1 = new Product(TEST_PRODUCT1_NAME, TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST);
    private static Product TEST_PRODUCT2 = new Product(TEST_PRODUCT2_NAME);

    private static List<Product> mockProductList = new ArrayList<Product>(Arrays.asList(new Product[] { TEST_PRODUCT1, TEST_PRODUCT2 }));

    @Mock
    private ProductRepo productRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private RemoteProjectManagerBean remoteProjectManagementBean;

    @Mock
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @InjectMocks
    private RemoteProjectManagerController remoteProjectManagerController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(remoteProjectManagerRepo.findAll()).thenReturn(mockRemoteProjectManagers);
        when(remoteProjectManagerRepo.create(any(RemoteProjectManager.class))).thenReturn(testRemoteProjectManagerOne);
        when(remoteProjectManagerRepo.update(any(RemoteProjectManager.class))).thenReturn(testModifiedProjectManager);
        doNothing().when(remoteProjectManagerRepo).delete(any(RemoteProjectManager.class));
        when(productRepo.findAll()).thenReturn(mockProductList);

        when(productRepo.create(any(Product.class))).thenReturn(TEST_PRODUCT1);
        when(productRepo.findById(any(Long.class))).thenReturn(null);
        doNothing().when(productRepo).delete(any(Product.class));
    }

    @Test
    public void testGetAllRemoteProductManager() {
        ApiResponse response = remoteProjectManagerController.getAll();
        assertEquals(SUCCESS, response.getMeta().getStatus(), "Not successful at getting requested Remote Project Managers");
        @SuppressWarnings("unchecked")
        List<RemoteProjectManager> remoteProjectManagers = (List<RemoteProjectManager>) response.getPayload().get("ArrayList<RemoteProjectManager>");
        assertEquals(mockRemoteProjectManagers, remoteProjectManagers, "Did not get the expected Remote Project Managers");
    }

    @Test
    public void testGetRemoteProductManagerById() {
        when(remoteProjectManagerRepo.findById(any(Long.class))).thenReturn(Optional.of(testRemoteProjectManagerOne));
        ApiResponse response = remoteProjectManagerController.getOne(1L);
        assertEquals(SUCCESS, response.getMeta().getStatus(), "Not successful at getting requested Remote Project Managers");
        RemoteProjectManager remoteProjectManager = (RemoteProjectManager) response.getPayload().get("RemoteProjectManager");
        assertEquals(testRemoteProjectManagerOne, remoteProjectManager, "Did not get the expected Remote Project Manager");
    }

    @Test
    public void testCreate() {
        ApiResponse response = remoteProjectManagerController.createRemoteProjectManager(testRemoteProjectManagerOne);
        assertEquals(SUCCESS, response.getMeta().getStatus(), "Not successful at creating Remote Project Manager");
    }

    @Test
    public void testUpdate() {
        ApiResponse response = remoteProjectManagerController.updateRemoteProjectManager(testModifiedProjectManager);
        assertEquals(SUCCESS, response.getMeta().getStatus(), "Note successful at updating Remote Project Manager");
        RemoteProjectManager remoteProjectManager = (RemoteProjectManager) response.getPayload().get("RemoteProjectManager");
        assertEquals(testModifiedProjectManager.getName(), remoteProjectManager.getName(), "Remote Project Manager title was not properly updated");
    }

    @Test
    public void testDelete() {
        ApiResponse response = remoteProjectManagerController.deleteRemoteProjectManager(testRemoteProjectManagerOne);
        assertEquals(SUCCESS, response.getMeta().getStatus(), "Not successful at deleting Remote Project Manager");
    }

    @Test
    public void testDeleteWithAssociatedProducts() {
        String message = "Cannot delete Remote Project Manager " + testRemoteProjectManagerOne.getName() + " because it has one or more associated Products.";
        when(productRepo.countByRemoteProjectInfoRemoteProjectManagerId(any(Long.class))).thenReturn(1L);

        ApiResponse response = remoteProjectManagerController.deleteRemoteProjectManager(testRemoteProjectManagerOne);
        assertEquals(ERROR, response.getMeta().getStatus(), "Should not delete when there are associated Products");
        assertEquals(message, response.getMeta().getMessage(), "Should not delete with friendly message when there are associated Products");
    }

    @Test
    public void testGetTypes() {
        ApiResponse response = remoteProjectManagerController.getTypes();
        assertEquals(SUCCESS, response.getMeta().getStatus(), "Not successful at getting service types");
    }

}
