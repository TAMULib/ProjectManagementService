package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class RemoteProjectManagerTest extends ModelTest {

    @Test
    public void testCreate() {
        Map<String, String> settings = getMockSettings();
        RemoteProjectManager remoteProjectManager1 = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, settings));
        RemoteProjectManager remoteProjectManager2 = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER2_NAME, ServiceType.GITHUB, settings));
        assertEquals("Remote project manager repo had incorrect number of remote project managers!", 2, remoteProjectManagerRepo.count());
        assertEquals("Remote project manager had incorrect name!", TEST_REMOTE_PROJECT_MANAGER1_NAME, remoteProjectManager1.getName());
        assertEquals("Remote project manager had incorrect service type!", ServiceType.VERSION_ONE, remoteProjectManager1.getType());
        assertEquals("Remote project manager had incorrect settings!", settings, remoteProjectManager1.getSettings());
        assertEquals("Remote project manager had incorrect name!", TEST_REMOTE_PROJECT_MANAGER2_NAME, remoteProjectManager2.getName());
        assertEquals("Remote project manager had incorrect service type!", ServiceType.GITHUB, remoteProjectManager2.getType());
        assertEquals("Remote project manager had incorrect settings!", settings, remoteProjectManager2.getSettings());
    }

    @Test
    public void testRead() {
        remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        assertEquals("Could not read all remote project managers!", 1, remoteProjectManagerRepo.findAll().size());
    }

    @Test
    public void testUpdate() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        remoteProjectManager.setName(TEST_ALTERNATE_REMOTE_PROJECT_MANAGER_NAME);
        remoteProjectManager = remoteProjectManagerRepo.update(remoteProjectManager);
        assertEquals("Remote project manager did not update name!", TEST_ALTERNATE_REMOTE_PROJECT_MANAGER_NAME, remoteProjectManager.getName());
    }

    @Test
    public void testDelete() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        remoteProjectManagerRepo.delete(remoteProjectManager);
        assertEquals("Remote project manager was not deleted!", 0, remoteProjectManagerRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDeleteWhenAssociatedToProduct() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        RemoteProjectInfo remoteProjectInfo = new RemoteProjectInfo("3000", remoteProjectManager);
        List<RemoteProjectInfo> remoteProductInfoList = new ArrayList<RemoteProjectInfo>(Arrays.asList(remoteProjectInfo));

        productRepo.create(new Product(TEST_PRODUCT_NAME, remoteProductInfoList));
        remoteProjectManagerRepo.delete(remoteProjectManager);
    }

}
