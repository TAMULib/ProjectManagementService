package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        RemoteProjectManager remoteProjectManager1 = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        RemoteProjectManager remoteProjectManager2 = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER2_NAME, ServiceType.GITHUB_PROJECT, TEST_PROJECT_URL2, TEST_PROJECT_TOKEN2));
        assertEquals("Remote project manager repo had incorrect number of remote project managers!", 2, remoteProjectManagerRepo.count());
        assertEquals("Remote project manager had incorrect name!", TEST_REMOTE_PROJECT_MANAGER1_NAME, remoteProjectManager1.getName());
        assertEquals("Remote project manager had incorrect service type!", ServiceType.VERSION_ONE, remoteProjectManager1.getType());
        assertEquals("Remote project manager had incorrect url!", TEST_PROJECT_URL1, remoteProjectManager1.getUrl());
        assertEquals("Remote project manager had incorrect token!", TEST_PROJECT_TOKEN1, remoteProjectManager1.getToken());
        assertEquals("Remote project manager had incorrect name!", TEST_REMOTE_PROJECT_MANAGER2_NAME, remoteProjectManager2.getName());
        assertEquals("Remote project manager had incorrect service type!", ServiceType.GITHUB_PROJECT, remoteProjectManager2.getType());
        assertEquals("Remote project manager had incorrect url!", TEST_PROJECT_URL2, remoteProjectManager2.getUrl());
        assertEquals("Remote project manager had incorrect token!", TEST_PROJECT_TOKEN2, remoteProjectManager2.getToken());
    }

    @Test
    public void testRead() {
        remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        assertEquals("Could not read all remote project managers!", 1, remoteProjectManagerRepo.findAll().size());
    }

    @Test
    public void testUpdate() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        remoteProjectManager.setName(TEST_ALTERNATE_REMOTE_PROJECT_MANAGER_NAME);
        remoteProjectManager = remoteProjectManagerRepo.update(remoteProjectManager);
        assertEquals("Remote project manager did not update name!", TEST_ALTERNATE_REMOTE_PROJECT_MANAGER_NAME, remoteProjectManager.getName());
    }

    @Test
    public void testDelete() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        remoteProjectManagerRepo.delete(remoteProjectManager);
        assertEquals("Remote project manager was not deleted!", 0, remoteProjectManagerRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDeleteWhenAssociatedToProduct() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        RemoteProjectInfo remoteProjectInfo = new RemoteProjectInfo("3000", remoteProjectManager);
        List<RemoteProjectInfo> remoteProductInfoList = new ArrayList<RemoteProjectInfo>(Arrays.asList(remoteProjectInfo));

        productRepo.create(new Product(TEST_PRODUCT_NAME, remoteProductInfoList));
        remoteProjectManagerRepo.delete(remoteProjectManager);
    }

}
