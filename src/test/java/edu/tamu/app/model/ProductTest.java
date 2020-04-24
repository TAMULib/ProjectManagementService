package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        RemoteProductManager remoteProductManager = remoteProductManagerRepo.create(new RemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings()));
        product.setName(TEST_ALTERNATE_PRODUCT_NAME);
        product.setScopeId("123456");
        product.setRemoteProductManager(remoteProductManager);
        product = productRepo.update(product);
        assertEquals("Product name was not updated!", TEST_ALTERNATE_PRODUCT_NAME, product.getName());
        assertEquals("Product scope id was not updated!", "123456", product.getScopeId());
        assertEquals("Product remote product manager was not updated!", TEST_REMOTE_PRODUCT_MANAGER1_NAME, product.getRemoteProductManager().getName());
        assertEquals("Product remote product manager settings were not updated!", "https://localhost:9101/TexasAMLibrary", product.getRemoteProductManager().getSettings().get("url"));
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
    public void testSetRemoteProductManager() {
        RemoteProductManager remoteProductManager = remoteProductManagerRepo.create(new RemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings()));

        Product product = productRepo.create(new Product(TEST_PRODUCT_NAME, "1000", remoteProductManager));
        assertEquals("Product has the incorrect name!", TEST_PRODUCT_NAME, product.getName());
        assertEquals("Product has the incorrect Remote Product Manager name!", TEST_REMOTE_PRODUCT_MANAGER1_NAME, product.getRemoteProductManager().getName());
        assertEquals("Product has the incorrect Remote Product Manager url setting value!", "https://localhost:9101/TexasAMLibrary", product.getRemoteProductManager().getSettings().get("url"));

        productRepo.delete(product);

        assertEquals("Product repo had incorrect number of products!", 0, productRepo.count());
        assertEquals("Remote product manager was deleted when product was deleted!", 1, remoteProductManagerRepo.count());
    }

}
