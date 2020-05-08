package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ProductTest extends ModelTest {

    @Test
    public void testCreate() {
        productRepo.create(new Product(TEST_PRODUCT_NAME));
        assertEquals("Product repo had incorrect number of products!", 1, productRepo.count());
    }

    @Test
    public void testRead() {
        productRepo.create(new Product(TEST_PRODUCT_NAME));
        Optional<Product> product = productRepo.findByName(TEST_PRODUCT_NAME);
        assertTrue("Could not read product!", product.isPresent());
        assertEquals("Product read did not have the correct name!", TEST_PRODUCT_NAME, product.get().getName());
    }

    @Test
    public void testUpdate() {
        Product product = productRepo.create(new Product(TEST_PRODUCT_NAME));
        String newScope = "123456";

        RemoteProductInfo newRemoteProductInfo = new RemoteProductInfo(newScope, TEST_REMOTE_PRODUCT_MANAGER1);
        List<RemoteProductInfo> newRemoteProductInfoList = new ArrayList<RemoteProductInfo>(Arrays.asList(newRemoteProductInfo));

        product.setName(TEST_ALTERNATE_PRODUCT_NAME);
        product.setRemoteProducts(newRemoteProductInfoList);
        product = productRepo.update(product);

        assertEquals("Product name was not updated!", TEST_ALTERNATE_PRODUCT_NAME, product.getName());
        assertEquals("Product remote product info was not updated!", newScope, product.getRemoteProducts().get(0).getScopeId());
    }

    @Test
    public void testDelete() {
        Product product = productRepo.create(new Product(TEST_ALTERNATE_PRODUCT_NAME));
        assertEquals("Product not created!", 1, productRepo.count());
        productRepo.delete(product);
        assertEquals("Product was not deleted!", 0, productRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicate() {
        productRepo.create(new Product(TEST_PRODUCT_NAME));
        productRepo.create(new Product(TEST_PRODUCT_NAME));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testNameNotNull() {
        productRepo.create(new Product(null));
    }

    @Test
    public void testSetRemoteProductInfo() {
        remoteProductManagerRepo.create(TEST_REMOTE_PRODUCT_MANAGER1);

        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST1);
        Product createdProduct = productRepo.create(product);

        assertEquals("Product has the incorrect name!", TEST_PRODUCT_NAME, createdProduct.getName());
        assertEquals("Product has the incorrect Remote Product Info!", TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST1, createdProduct.getRemoteProducts());

        productRepo.delete(createdProduct);

        assertEquals("Product repo had incorrect number of products!", 0, productRepo.count());
        assertEquals("Remote product manager was deleted when product was deleted!", 1, remoteProductManagerRepo.count());
    }

}
