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

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class RemoteProjectManagerControllerTest {

    private static final String TEST_VSC_NAME1 = "Test VSC 1";
    private static final String TEST_VSC_NAME2 = "Test VSC 2";
    private static final String TEST_MODIFIED_VSC_NAME = "Modified VSC";

    private static RemoteProjectManager TEST_VMS1 = new RemoteProjectManager(TEST_VSC_NAME1, ServiceType.VERSION_ONE);
    private static RemoteProjectManager TEST_VMS2 = new RemoteProjectManager(TEST_VSC_NAME2, ServiceType.VERSION_ONE);
    private static RemoteProjectManager TEST_MODIFIED_VSC = new RemoteProjectManager(TEST_MODIFIED_VSC_NAME, ServiceType.VERSION_ONE);
    private static List<RemoteProjectManager> mockVMSList = new ArrayList<RemoteProjectManager>(Arrays.asList(new RemoteProjectManager[] { TEST_VMS1, TEST_VMS2 }));

    private static ApiResponse response;

    @Mock
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @InjectMocks
    private RemoteProjectManagerController remoteProjectManagerController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(remoteProjectManagerRepo.findAll()).thenReturn(mockVMSList);
        when(remoteProjectManagerRepo.findOne(any(Long.class))).thenReturn(TEST_VMS1);
        when(remoteProjectManagerRepo.create(any(RemoteProjectManager.class))).thenReturn(TEST_VMS1);
        when(remoteProjectManagerRepo.update(any(RemoteProjectManager.class))).thenReturn(TEST_MODIFIED_VSC);
        doNothing().when(remoteProjectManagerRepo).delete(any(RemoteProjectManager.class));
    }

    @Test
    public void testGetAllRemoteProjectManager() {
        response = remoteProjectManagerController.getAll();
        assertEquals("Not successful at getting requested Remote Project Managers", SUCCESS, response.getMeta().getStatus());
        @SuppressWarnings("unchecked")
        List<RemoteProjectManager> vmses = (List<RemoteProjectManager>) response.getPayload().get("ArrayList<RemoteProjectManager>");
        assertEquals("Did not get the expected Remote Project Managers", mockVMSList, vmses);
    }

    @Test
    public void testGetRemoteProjectManagerById() {
        response = remoteProjectManagerController.getOne(1L);
        assertEquals("Not successful at getting requested Remote Project Managers", SUCCESS, response.getMeta().getStatus());
        RemoteProjectManager vms = (RemoteProjectManager) response.getPayload().get("RemoteProjectManager");
        assertEquals("Did not get the expected Remote Project Manager", TEST_VMS1, vms);
    }

    @Test
    public void testCreate() {
        response = remoteProjectManagerController.createRemoteProjectManager(TEST_VMS1);
        assertEquals("Not successful at creating Remote Project Manager", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdate() {
        response = remoteProjectManagerController.updateRemoteProjectManager(TEST_MODIFIED_VSC);
        assertEquals("Note successful at updating Remote Project Manager", SUCCESS, response.getMeta().getStatus());
        RemoteProjectManager vms = (RemoteProjectManager) response.getPayload().get("RemoteProjectManager");
        assertEquals("Remote Project Manager title was not properly updated", TEST_MODIFIED_VSC.getName(), vms.getName());
    }

    @Test
    public void testDelete() {
        response = remoteProjectManagerController.deleteRemoteProjectManager(TEST_VMS1);
        assertEquals("Not successful at deleting Remote Project Manager", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testGetTypes() {
        response = remoteProjectManagerController.getTypes();
        assertEquals("Not successful at getting service types", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testGetScaffolding() {
        response = remoteProjectManagerController.getTypeScaffolding(ServiceType.VERSION_ONE.toString());
        assertEquals("Not successful at getting scaffolding", SUCCESS, response.getMeta().getStatus());
    }
}
