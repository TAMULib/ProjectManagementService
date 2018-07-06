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

import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class RemoteProjectManagerControllerTest {

    private static final String TEST_RMP_ONE_NAME = "Test Remote Project Manager 1";
    private static final String TEST_RMP_TWO_NAME = "Test Remote Project Manager 2";
    private static final String TEST_MODIFIED_RMP_NAME = "Modified Remote Project Manager";

    private static RemoteProjectManager testRemoteProjectManagerOne = new RemoteProjectManager(TEST_RMP_ONE_NAME, ServiceType.VERSION_ONE);
    private static RemoteProjectManager testRemoteProjectManagerTwo = new RemoteProjectManager(TEST_RMP_TWO_NAME, ServiceType.VERSION_ONE);
    private static RemoteProjectManager testModifiedProjectManager = new RemoteProjectManager(TEST_MODIFIED_RMP_NAME, ServiceType.VERSION_ONE);
    private static List<RemoteProjectManager> mockRemoteProjectManagers = new ArrayList<RemoteProjectManager>(Arrays.asList(new RemoteProjectManager[] { testRemoteProjectManagerOne, testRemoteProjectManagerTwo }));

    @Mock
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @InjectMocks
    private RemoteProjectManagerController remoteProjectManagerController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(remoteProjectManagerRepo.findAll()).thenReturn(mockRemoteProjectManagers);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(testRemoteProjectManagerOne);
        when(remoteProjectManagerRepo.create(any(RemoteProjectManager.class))).thenReturn(testRemoteProjectManagerOne);
        when(remoteProjectManagerRepo.update(any(RemoteProjectManager.class))).thenReturn(testModifiedProjectManager);
        doNothing().when(remoteProjectManagerRepo).delete(any(RemoteProjectManager.class));
    }

    @Test
    public void testGetAllRemoteProjectManager() {
        ApiResponse response = remoteProjectManagerController.getAll();
        assertEquals("Not successful at getting requested Remote Project Managers", SUCCESS, response.getMeta().getStatus());
        @SuppressWarnings("unchecked")
        List<RemoteProjectManager> remoteProjectManagers = (List<RemoteProjectManager>) response.getPayload().get("ArrayList<RemoteProjectManager>");
        assertEquals("Did not get the expected Remote Project Managers", mockRemoteProjectManagers, remoteProjectManagers);
    }

    @Test
    public void testGetRemoteProjectManagerById() {
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

}
