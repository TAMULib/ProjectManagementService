package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ProductModelTest extends ModelTest {

    @Test
    public void testGetName() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST1);
        assertEquals("Product did not return the correct name!", TEST_PRODUCT_NAME, product.getName());
    }

    @Test
    public void testGetRemoteProducts() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST1);
        assertEquals("Product did not return the correct remote product info!", TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST1, product.getRemoteProducts());
    }

    @Test
    public void testSetName() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST1);
        product.setName(TEST_PRODUCT_SCOPE2);
        assertEquals("RemoteProductInfo did not correctly update the scope id!", TEST_PRODUCT_SCOPE2, product.getName());
    }

    @Test
    public void testSetRemoteProducts() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST1);
        product.setRemoteProducts(TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST2);
        assertEquals("RemoteProductInfo did not return the correct remote product info!", TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST2, product.getRemoteProducts());
    }

    @Test
    public void testAddRemoteProduct() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST1);
        product.addRemoteProduct(TEST_REMOTE_PRODUCT_INFO3);
        List<RemoteProductInfo> remoteProducts = product.getRemoteProducts();
        assertEquals("RemoteProductInfo did not correctly add the remote product!", true, remoteProducts.contains(TEST_REMOTE_PRODUCT_INFO3));
    }

    @Test
    public void testRemoveRemoteProduct() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST1);
        product.removeRemoteProduct(TEST_REMOTE_PRODUCT_INFO1);
        List<RemoteProductInfo> remoteProducts = product.getRemoteProducts();
        assertEquals("RemoteProductInfo did not correctly add the remote product!", false, remoteProducts.contains(TEST_REMOTE_PRODUCT_INFO1));
    }

}
