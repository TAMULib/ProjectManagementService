package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class RemoteProductInfoModelTest extends ModelTest {

    @Test
    public void testGetScopeId() {
        RemoteProductInfo rpi = new RemoteProductInfo(TEST_PRODUCT_SCOPE1, TEST_REMOTE_PRODUCT_MANAGER1);
        assertEquals("RemoteProductInfo did not return the correct scope id!", TEST_PRODUCT_SCOPE1, rpi.getScopeId());
    }

    @Test
    public void testGetRemoteProductManager() {
        RemoteProductInfo rpi = new RemoteProductInfo(TEST_PRODUCT_SCOPE1, TEST_REMOTE_PRODUCT_MANAGER1);
        assertEquals("RemoteProductInfo did not return the correct remote product manager!", TEST_REMOTE_PRODUCT_MANAGER1, rpi.getRemoteProductManager());
    }

    @Test
    public void testSetScopeId() {
        RemoteProductInfo rpi = new RemoteProductInfo(TEST_PRODUCT_SCOPE1, TEST_REMOTE_PRODUCT_MANAGER1);
        rpi.setScopeId(TEST_PRODUCT_SCOPE2);
        assertEquals("RemoteProductInfo did not correctly update the scope id!", TEST_PRODUCT_SCOPE2, rpi.getScopeId());
    }

    @Test
    public void testSetRemoteProductManager() {
        RemoteProductInfo rpi = new RemoteProductInfo(TEST_PRODUCT_SCOPE1, TEST_REMOTE_PRODUCT_MANAGER1);
        rpi.setRemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER2);
        assertEquals("RemoteProductInfo did not return the correct remote product manager!", TEST_REMOTE_PRODUCT_MANAGER2, rpi.getRemoteProductManager());
    }

}
