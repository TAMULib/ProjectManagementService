package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.model.RemoteProject;
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
public class RemoteProjectManagerControllerTest {

    private static final String TEST_PRODUCT1_NAME = "Test Product 1 Name";
    private static final String TEST_PRODUCT2_NAME = "Test Product 2 Name";

    private static final String TEST_PROJECT1_SCOPE1 = "0010";
    private static final String TEST_PROJECT1_SCOPE2 = "0011";
    private static final String TEST_INVALID_SCOPE = "XXXX";

    private static final String TEST_FEATURE_REQUEST_TITLE = "Test Feature Request Title";
    private static final String TEST_FEATURE_REQUEST_DESCRIPTION = "Test Feature Request Description";

    private static final String TEST_RMP_ONE_NAME = "Test Remote Project Manager 1";
    private static final String TEST_RMP_TWO_NAME = "Test Remote Project Manager 2";
    private static final String TEST_MODIFIED_RMP_NAME = "Modified Remote Project Manager";

    private static final String INVALID_RPM_ID_ERROR_MESSAGE = "Error fetching remote projects from " + TEST_RMP_ONE_NAME + "!";
    private static final String MISSING_RPM_ERROR_MESSAGE = "Remote Project Manager with id null not found!";
    private static final String INVALID_RPM_ID_ERROR_MESSAGE_FIND_BY_ID = "Error fetching remote project with scope id " + TEST_INVALID_SCOPE + " from " + TEST_RMP_ONE_NAME + "!";

    private static RemoteProjectManager testRemoteProjectManagerOne = new RemoteProjectManager(TEST_RMP_ONE_NAME, ServiceType.VERSION_ONE);
    private static RemoteProjectManager testRemoteProjectManagerTwo = new RemoteProjectManager(TEST_RMP_TWO_NAME, ServiceType.VERSION_ONE);
    private static RemoteProjectManager testModifiedProjectManager = new RemoteProjectManager(TEST_MODIFIED_RMP_NAME, ServiceType.VERSION_ONE);
    private static List<RemoteProjectManager> mockRemoteProjectManagers = new ArrayList<RemoteProjectManager>(Arrays.asList(new RemoteProjectManager[] { testRemoteProjectManagerOne, testRemoteProjectManagerTwo }));

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PROJECT1_SCOPE1, testRemoteProjectManagerOne);
    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO2 = new RemoteProjectInfo(TEST_PROJECT1_SCOPE2, testRemoteProjectManagerOne);

    private static final List<RemoteProjectInfo> TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1, TEST_REMOTE_PROJECT_INFO2));

    private static Product TEST_PRODUCT1 = new Product(TEST_PRODUCT1_NAME, TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST);
    private static Product TEST_PRODUCT2 = new Product(TEST_PRODUCT2_NAME);

    private static FeatureRequest TEST_FEATURE_REQUEST = new FeatureRequest(TEST_FEATURE_REQUEST_TITLE, TEST_FEATURE_REQUEST_DESCRIPTION, TEST_PRODUCT1.getId(), TEST_PROJECT1_SCOPE1);

    private static List<Product> mockProductList = new ArrayList<Product>(Arrays.asList(new Product[] { TEST_PRODUCT1, TEST_PRODUCT2 }));

    private static ApiResponse apiResponse;

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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(remoteProjectManagerRepo.findAll()).thenReturn(mockRemoteProjectManagers);
        when(remoteProjectManagerRepo.create(any(RemoteProjectManager.class))).thenReturn(testRemoteProjectManagerOne);
        when(remoteProjectManagerRepo.update(any(RemoteProjectManager.class))).thenReturn(testModifiedProjectManager);
        doNothing().when(remoteProjectManagerRepo).delete(any(RemoteProjectManager.class));
        when(productRepo.findAll()).thenReturn(mockProductList);

        when(productRepo.create(any(Product.class))).thenReturn(TEST_PRODUCT1);
        when(productRepo.findOne(any(Long.class))).thenReturn(null);
        doNothing().when(productRepo).delete(any(Product.class));
    }

    @Test
    public void testGetAllRemoteProductManager() {
        ApiResponse response = remoteProjectManagerController.getAll();
        assertEquals("Not successful at getting requested Remote Project Managers", SUCCESS, response.getMeta().getStatus());
        @SuppressWarnings("unchecked")
        List<RemoteProjectManager> remoteProjectManagers = (List<RemoteProjectManager>) response.getPayload().get("ArrayList<RemoteProjectManager>");
        assertEquals("Did not get the expected Remote Project Managers", mockRemoteProjectManagers, remoteProjectManagers);
    }

    @Test
    public void testGetRemoteProductManagerById() {
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(testRemoteProjectManagerOne);
        ApiResponse response = remoteProjectManagerController.getOne(1L);
        assertEquals("Not successful at getting requested Remote Project Managers", SUCCESS, response.getMeta().getStatus());
        RemoteProjectManager remoteProjectManager = (RemoteProjectManager) response.getPayload().get("RemoteProjectManager");
        assertEquals("Did not get the expected Remote Project Manager", testRemoteProjectManagerOne, remoteProjectManager);
    }

    @Test
    public void testCreate() {
        ApiResponse response = remoteProjectManagerController.createRemoteProjectManager(testRemoteProjectManagerOne);
        assertEquals("Not successful at creating Remote Project Manager", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdate() {
        ApiResponse response = remoteProjectManagerController.updateRemoteProjectManager(testModifiedProjectManager);
        assertEquals("Note successful at updating Remote Project Manager", SUCCESS, response.getMeta().getStatus());
        RemoteProjectManager remoteProjectManager = (RemoteProjectManager) response.getPayload().get("RemoteProjectManager");
        assertEquals("Remote Project Manager title was not properly updated", testModifiedProjectManager.getName(), remoteProjectManager.getName());
    }

    @Test
    public void testDelete() {
        ApiResponse response = remoteProjectManagerController.deleteRemoteProjectManager(testRemoteProjectManagerOne);
        assertEquals("Not successful at deleting Remote Project Manager", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testGetTypes() {
        ApiResponse response = remoteProjectManagerController.getTypes();
        assertEquals("Not successful at getting service types", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testGetScaffolding() {
        ApiResponse response = remoteProjectManagerController.getTypeScaffolding(ServiceType.VERSION_ONE.toString());
        assertEquals("Not successful at getting scaffolding", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testGetAllRemoteProjects() throws Exception {
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(testRemoteProjectManagerOne);
        when(remoteProjectManagementBean.push(TEST_FEATURE_REQUEST)).thenReturn(TEST_FEATURE_REQUEST);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);
        apiResponse = remoteProjectManagerController.getAllRemoteProjects(testRemoteProjectManagerOne.getId());
        assertEquals("Remote Project Manager controller unable to get all remote projects", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testGetAllRemoteProjectsWithInvalidRemoteProjectManager() {
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(testRemoteProjectManagerOne);
        apiResponse = remoteProjectManagerController.getAllRemoteProjects(testRemoteProjectManagerOne.getId());
        assertEquals("Request with invalid Remote Project Manager id did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Invalid Remote Project Manager id did not result in the expected error message", INVALID_RPM_ID_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetAllRemoteProjectsWithNoRemoteProjectManager() {
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(null);
        apiResponse = remoteProjectManagerController.getAllRemoteProjects(testRemoteProjectManagerOne.getId());
        assertEquals("Request without Remote Project Manager did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Missing Remote Project Manager did not result in the expected error message", MISSING_RPM_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetRemoteProjectByScopeId() throws Exception {
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(testRemoteProjectManagerOne);
        when(remoteProjectManagementBean.getRemoteProjectByScopeId(TEST_PROJECT1_SCOPE1)).thenReturn(new RemoteProject());
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(remoteProjectManagementBean);
        apiResponse = remoteProjectManagerController.getRemoteProjectByScopeId(testRemoteProjectManagerOne.getId(), TEST_PROJECT1_SCOPE1);
        assertEquals("Product controller unable to get remote project by scope id", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testGetRemoteProjectByScopeIdWithInvalidScope() {
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(testRemoteProjectManagerOne);
        apiResponse = remoteProjectManagerController.getRemoteProjectByScopeId(testRemoteProjectManagerOne.getId(), TEST_INVALID_SCOPE);
        assertEquals("Request with invalid Remote Project Manager id did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Invalid Remote Project Manager id did not result in the expected error message", INVALID_RPM_ID_ERROR_MESSAGE_FIND_BY_ID, apiResponse.getMeta().getMessage());
    }

    @Test
    public void testGetRemoteProjectByScopeIdWithMissingRemoteProjectManager() {
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(null);
        apiResponse = remoteProjectManagerController.getRemoteProjectByScopeId(testRemoteProjectManagerOne.getId(), TEST_PROJECT1_SCOPE1);
        assertEquals("Request with no Remote Project Manager did not result in an error", ERROR, apiResponse.getMeta().getStatus());
        assertEquals("Missing Remote Project Manager did not result in the expected error message", MISSING_RPM_ERROR_MESSAGE, apiResponse.getMeta().getMessage());
    }

}
