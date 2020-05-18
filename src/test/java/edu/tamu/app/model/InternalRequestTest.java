package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.service.RemoteProductsScheduledCacheService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class InternalRequestTest extends ModelTest {

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RemoteProductsScheduledCacheService remoteProductsScheduledCacheService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        VersionOneService versionOneService = mock(VersionOneService.class);
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);

        remoteProductManagerRepo.create(TEST_REMOTE_PRODUCT_MANAGER1);
        productRepo.create(TEST_PRODUCT);
    }

    @Test
    public void testCreate() {
        Product product = productRepo.findAll().get(0);
        InternalRequest internalRequest = new InternalRequest(TEST_INTERNAL_REQUEST1.getTitle(), TEST_INTERNAL_REQUEST1.getDescription(), product, TEST_INTERNAL_REQUEST1.getCreatedOn());
        InternalRequest createdInternalRequest = internalRequestRepo.create(internalRequest);
        assertEquals("Internal request had incorrect title!", TEST_INTERNAL_REQUEST_TITLE1, createdInternalRequest.getTitle());
    }

    @Test
    public void testRead() {
        Product product = productRepo.findAll().get(0);
        InternalRequest internalRequest = new InternalRequest(TEST_INTERNAL_REQUEST1.getTitle(), TEST_INTERNAL_REQUEST1.getDescription(), product, TEST_INTERNAL_REQUEST1.getCreatedOn());

        internalRequestRepo.create(internalRequest);
        assertEquals("Could not read all internal requests!", 1, internalRequestRepo.findAll().size());
    }

    @Test
    public void testUpdate() {
        Product product = productRepo.findAll().get(0);
        InternalRequest internalRequest = new InternalRequest(TEST_INTERNAL_REQUEST1.getTitle(), TEST_INTERNAL_REQUEST1.getDescription(), product, TEST_INTERNAL_REQUEST1.getCreatedOn());
        InternalRequest createdInternalRequest = internalRequestRepo.create(internalRequest);

        createdInternalRequest.setTitle(TEST_INTERNAL_REQUEST_TITLE2);
        createdInternalRequest = internalRequestRepo.update(internalRequest);
        assertEquals("Internal request did not update title!", TEST_INTERNAL_REQUEST_TITLE2, createdInternalRequest.getTitle());
    }

    @Test
    public void testDelete() {
        Product product = productRepo.findAll().get(0);
        InternalRequest internalRequest = new InternalRequest(TEST_INTERNAL_REQUEST1.getTitle(), TEST_INTERNAL_REQUEST1.getDescription(), product, TEST_INTERNAL_REQUEST1.getCreatedOn());
        InternalRequest createdInternalRequest = internalRequestRepo.create(internalRequest);

        internalRequestRepo.delete(createdInternalRequest);
        assertEquals("Internal request was not deleted!", 0, internalRequestRepo.count());
    }
}
