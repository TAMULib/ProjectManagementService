package edu.tamu.app.controller;

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

import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.RemoteProductManagerRepo;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class RemoteProductManagerControllerTest {

    private static final String TEST_RMP_ONE_NAME = "Test Remote Product Manager 1";
    private static final String TEST_RMP_TWO_NAME = "Test Remote Product Manager 2";
    private static final String TEST_MODIFIED_RMP_NAME = "Modified Remote Product Manager";

    private static RemoteProductManager testRemoteProductManagerOne = new RemoteProductManager(TEST_RMP_ONE_NAME, ServiceType.VERSION_ONE);
    private static RemoteProductManager testRemoteProductManagerTwo = new RemoteProductManager(TEST_RMP_TWO_NAME, ServiceType.VERSION_ONE);
    private static RemoteProductManager testModifiedProductManager = new RemoteProductManager(TEST_MODIFIED_RMP_NAME, ServiceType.VERSION_ONE);
    private static List<RemoteProductManager> mockRemoteProductManagers = new ArrayList<RemoteProductManager>(Arrays.asList(new RemoteProductManager[] { testRemoteProductManagerOne, testRemoteProductManagerTwo }));

    @Mock
    private RemoteProductManagerRepo remoteProductManagerRepo;

    @InjectMocks
    private RemoteProductManagerController remoteProductManagerController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(remoteProductManagerRepo.findAll()).thenReturn(mockRemoteProductManagers);
        when(remoteProductManagerRepo.findOne(any(Long.class))).thenReturn(testRemoteProductManagerOne);
        when(remoteProductManagerRepo.create(any(RemoteProductManager.class))).thenReturn(testRemoteProductManagerOne);
        when(remoteProductManagerRepo.update(any(RemoteProductManager.class))).thenReturn(testModifiedProductManager);
        doNothing().when(remoteProductManagerRepo).delete(any(RemoteProductManager.class));
    }

    @Test
    public void testGetAllRemoteProductManager() {
        ApiResponse response = remoteProductManagerController.getAll();
        assertEquals("Not successful at getting requested Remote Product Managers", SUCCESS, response.getMeta().getStatus());
        @SuppressWarnings("unchecked")
        List<RemoteProductManager> remoteProductManagers = (List<RemoteProductManager>) response.getPayload().get("ArrayList<RemoteProductManager>");
        assertEquals("Did not get the expected Remote Product Managers", mockRemoteProductManagers, remoteProductManagers);
    }

    @Test
    public void testGetRemoteProductManagerById() {
        ApiResponse response = remoteProductManagerController.getOne(1L);
        assertEquals("Not successful at getting requested Remote Product Managers", SUCCESS, response.getMeta().getStatus());
        RemoteProductManager remoteProductManager = (RemoteProductManager) response.getPayload().get("RemoteProductManager");
        assertEquals("Did not get the expected Remote Product Manager", testRemoteProductManagerOne, remoteProductManager);
    }

    @Test
    public void testCreate() {
        ApiResponse response = remoteProductManagerController.createRemoteProductManager(testRemoteProductManagerOne);
        assertEquals("Not successful at creating Remote Product Manager", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdate() {
        ApiResponse response = remoteProductManagerController.updateRemoteProductManager(testModifiedProductManager);
        assertEquals("Note successful at updating Remote Product Manager", SUCCESS, response.getMeta().getStatus());
        RemoteProductManager remoteProductManager = (RemoteProductManager) response.getPayload().get("RemoteProductManager");
        assertEquals("Remote Product Manager title was not properly updated", testModifiedProductManager.getName(), remoteProductManager.getName());
    }

    @Test
    public void testDelete() {
        ApiResponse response = remoteProductManagerController.deleteRemoteProductManager(testRemoteProductManagerOne);
        assertEquals("Not successful at deleting Remote Product Manager", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testGetTypes() {
        ApiResponse response = remoteProductManagerController.getTypes();
        assertEquals("Not successful at getting service types", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testGetScaffolding() {
        ApiResponse response = remoteProductManagerController.getTypeScaffolding(ServiceType.VERSION_ONE.toString());
        assertEquals("Not successful at getting scaffolding", SUCCESS, response.getMeta().getStatus());
    }

}
