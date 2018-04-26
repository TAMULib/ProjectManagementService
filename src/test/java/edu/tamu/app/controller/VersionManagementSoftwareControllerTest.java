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
import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.VersionManagementSoftwareRepo;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class VersionManagementSoftwareControllerTest {

    private static final String TEST_VSC_NAME1 = "Test VSC 1";
    private static final String TEST_VSC_NAME2 = "Test VSC 2";
    private static final String TEST_MODIFIED_VSC_NAME = "Modified VSC";

    private static VersionManagementSoftware TEST_VMS1 = new VersionManagementSoftware(TEST_VSC_NAME1, ServiceType.VERSION_ONE);
    private static VersionManagementSoftware TEST_VMS2 = new VersionManagementSoftware(TEST_VSC_NAME2, ServiceType.VERSION_ONE);
    private static VersionManagementSoftware TEST_MODIFIED_VSC = new VersionManagementSoftware(TEST_MODIFIED_VSC_NAME, ServiceType.VERSION_ONE);
    private static List<VersionManagementSoftware> mockVMSList = new ArrayList<VersionManagementSoftware>(Arrays.asList(new VersionManagementSoftware[] { TEST_VMS1, TEST_VMS2 }));

    private static ApiResponse response;

    @Mock
    private VersionManagementSoftwareRepo vmsRepo;

    @InjectMocks
    private VersionManagementSoftwareController vmsController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(vmsRepo.findAll()).thenReturn(mockVMSList);
        when(vmsRepo.findOne(any(Long.class))).thenReturn(TEST_VMS1);
        when(vmsRepo.create(any(VersionManagementSoftware.class))).thenReturn(TEST_VMS1);
        when(vmsRepo.update(any(VersionManagementSoftware.class))).thenReturn(TEST_MODIFIED_VSC);
        doNothing().when(vmsRepo).delete(any(VersionManagementSoftware.class));
    }

    @Test
    public void testGetAllVersionManagementSoftware() {
        response = vmsController.getAll();
        assertEquals("Not successful at getting requested Version Management Statuses", SUCCESS, response.getMeta().getStatus());
        @SuppressWarnings("unchecked")
        List<VersionManagementSoftware> vmses = (List<VersionManagementSoftware>) response.getPayload().get("ArrayList<VersionManagementSoftware>");
        assertEquals("Did not get the expected Version Management Statuses", mockVMSList, vmses);
    }

    @Test
    public void testGetVersionManagementSoftwareById() {
        response = vmsController.getOne(1L);
        assertEquals("Not successful at getting requested Version Management Statuses", SUCCESS, response.getMeta().getStatus());
        VersionManagementSoftware vms = (VersionManagementSoftware) response.getPayload().get("VersionManagementSoftware");
        assertEquals("Did not get the expected Version Management Software", TEST_VMS1, vms);
    }

    @Test
    public void testCreate() {
        response = vmsController.createVersionManagementSoftware(TEST_VMS1);
        assertEquals("Not successful at creating Version Management Software", SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdate() {
        response = vmsController.updateVersionManagementSoftware(TEST_MODIFIED_VSC);
        assertEquals("Note successful at updating Version Management Software", SUCCESS, response.getMeta().getStatus());
        VersionManagementSoftware vms = (VersionManagementSoftware) response.getPayload().get("VersionManagementSoftware");
        assertEquals("Version Management Software title was not properly updated", TEST_MODIFIED_VSC.getName(), vms.getName());
    }

    @Test
    public void testDelete() {
        response = vmsController.deleteVersionManagementSoftware(TEST_VMS1);
        assertEquals("Not successful at deleting Version Management Software", SUCCESS, response.getMeta().getStatus());
    }
    
    @Test
    public void testGetTypes() {
        response = vmsController.getTypes();
        assertEquals("Not successful at getting service types", SUCCESS, response.getMeta().getStatus());
    }
    
    @Test
    public void testGetScaffolding() {
        response = vmsController.getTypeScaffolding(ServiceType.VERSION_ONE.toString());
        assertEquals("Not successful at getting scaffolding", SUCCESS, response.getMeta().getStatus());
    }
}
