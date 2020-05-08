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
public class RemoteProductManagerTest extends ModelTest {

    @Test
    public void testCreate() {
        Map<String, String> settings = getMockSettings();
        RemoteProductManager remoteProductManager1 = remoteProductManagerRepo.create(new RemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER1_NAME, ServiceType.VERSION_ONE, settings));
        RemoteProductManager remoteProductManager2 = remoteProductManagerRepo.create(new RemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER2_NAME, ServiceType.GITHUB, settings));
        assertEquals("Remote product manager repo had incorrect number of remote product managers!", 2, remoteProductManagerRepo.count());
        assertEquals("Remote product manager had incorrect name!", TEST_REMOTE_PRODUCT_MANAGER1_NAME, remoteProductManager1.getName());
        assertEquals("Remote product manager had incorrect service type!", ServiceType.VERSION_ONE, remoteProductManager1.getType());
        assertEquals("Remote product manager had incorrect settings!", settings, remoteProductManager1.getSettings());
        assertEquals("Remote product manager had incorrect name!", TEST_REMOTE_PRODUCT_MANAGER2_NAME, remoteProductManager2.getName());
        assertEquals("Remote product manager had incorrect service type!", ServiceType.GITHUB, remoteProductManager2.getType());
        assertEquals("Remote product manager had incorrect settings!", settings, remoteProductManager2.getSettings());
    }

    @Test
    public void testRead() {
        remoteProductManagerRepo.create(new RemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        assertEquals("Could not read all remote product managers!", 1, remoteProductManagerRepo.findAll().size());
    }

    @Test
    public void testUpdate() {
        RemoteProductManager remoteProductManager = remoteProductManagerRepo.create(new RemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        remoteProductManager.setName(TEST_ALTERNATE_REMOTE_PRODUCT_MANAGER_NAME);
        remoteProductManager = remoteProductManagerRepo.update(remoteProductManager);
        assertEquals("Remote product manager did not update name!", TEST_ALTERNATE_REMOTE_PRODUCT_MANAGER_NAME, remoteProductManager.getName());
    }

    @Test
    public void testDelete() {
        RemoteProductManager remoteProductManager = remoteProductManagerRepo.create(new RemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        remoteProductManagerRepo.delete(remoteProductManager);
        assertEquals("Remote product manager was note deleted!", 0, remoteProductManagerRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDeleteWhenAssociatedToProduct() {
        RemoteProductManager remoteProductManager = remoteProductManagerRepo.create(new RemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        RemoteProductInfo remoteProductInfo = new RemoteProductInfo("3000", remoteProductManager);
        List<RemoteProductInfo> remoteProductInfoList = new ArrayList<RemoteProductInfo>(Arrays.asList(remoteProductInfo));

        productRepo.create(new Product(TEST_PRODUCT_NAME, remoteProductInfoList));
        remoteProductManagerRepo.delete(remoteProductManager);
    }

}
