package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ProductTest extends ModelTest {

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        VersionOneService versionOneService = mock(VersionOneService.class);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);

        remoteProjectManagerRepo.create(TEST_REMOTE_PROJECT_MANAGER1);
    }

    @Test
    public void testCreate() {
        productRepo.create(TEST_PRODUCT);
        assertEquals("Product repo had incorrect number of products!", 1, productRepo.count());
    }

    @Test
    public void testRead() {
        productRepo.create(TEST_PRODUCT);
        Optional<Product> product = productRepo.findByName(TEST_PRODUCT_NAME);
        assertTrue("Could not read product!", product.isPresent());
        assertEquals("Product read did not have the correct name!", TEST_PRODUCT_NAME, product.get().getName());
    }

    @Test
    public void testUpdate() {
        Product product = productRepo.create(TEST_PRODUCT);
        String newScope = "123456";

        RemoteProjectInfo newRemoteProjectInfo = new RemoteProjectInfo(newScope, TEST_REMOTE_PROJECT_MANAGER1);
        List<RemoteProjectInfo> newRemoteProjectInfoList = new ArrayList<RemoteProjectInfo>(Arrays.asList(newRemoteProjectInfo));

        product.setName(TEST_ALTERNATE_PRODUCT_NAME);
        product.setRemoteProductInfo(newRemoteProjectInfoList);
        product = productRepo.update(product);

        assertEquals("Product name was not updated!", TEST_ALTERNATE_PRODUCT_NAME, product.getName());
        assertEquals("Product remote project info was not updated!", newScope, product.getRemoteProjectInfo().get(0).getScopeId());
    }

    @Test
    public void testDelete() {
        Product createdProduct = productRepo.create(TEST_PRODUCT);
        assertEquals("Product not created!", 1, productRepo.count());
        productRepo.delete(createdProduct.getId());
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
        Product createdProduct = productRepo.create(TEST_PRODUCT);

        assertEquals("Product has the incorrect name!", TEST_PRODUCT_NAME, createdProduct.getName());
        assertEquals("Product has the incorrect Remote Project Info!", TEST_PRODUCT.getRemoteProjectInfo().size(), createdProduct.getRemoteProjectInfo().size());

        productRepo.delete(createdProduct);

        assertEquals("Product repo had incorrect number of products!", 0, productRepo.count());
    }

}
