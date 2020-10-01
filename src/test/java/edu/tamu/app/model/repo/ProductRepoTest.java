package edu.tamu.app.model.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.service.manager.GitHubProjectService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.ticketing.SugarService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductRepoTest extends AbstractRepoTest {
    
    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER1 = new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO2 = new RemoteProjectInfo(TEST_PROJECT_SCOPE2, TEST_REMOTE_PROJECT_MANAGER1);

    private static final List<RemoteProjectInfo> TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1 = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1, TEST_REMOTE_PROJECT_INFO2));

    private static final List<String> TEST_OTHER_URLS1 = new ArrayList<String>(Arrays.asList(TEST_OTHER_URL1, TEST_OTHER_URL2));

    private static final Product TEST_PRODUCT1 = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, TEST_DEV_URL1, TEST_PRE_URL1, TEST_PROD_URL1, TEST_WIKI_URL1, TEST_OTHER_URLS1);

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @MockBean
    private SugarService sugarService;

    @MockBean
    private GitHubProjectService gitHubService;

    @MockBean
    private VersionOneService versionOneService;

    @MockBean
    private ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService;

    @MockBean
    private ProductsStatsScheduledCacheService productsStatsScheduledCacheService;

    @MockBean
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @MockBean
    private GitHubBuilder ghBuilder;

    @MockBean
    private GitHub github;

    // @After and @Before cannot be safely specified inside a parent class.
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockSugarService(sugarService);
        mockGitHubService(gitHubService, ghBuilder);
        mockVersionOneService(versionOneService);
        mockActiveSprintsScheduledCacheService(activeSprintsScheduledCacheService);
        mockProductsStatsScheduledCacheService(productsStatsScheduledCacheService);
        mockRemoteProjectsScheduledCacheService(remoteProjectsScheduledCacheService);

        remoteProjectManagerRepo.create(TEST_REMOTE_PROJECT_MANAGER1);
    }

    @Test
    public void testCreate() {
        productRepo.create(TEST_PRODUCT1);
        assertEquals("Product repo had incorrect number of products!", 1, productRepo.count());
    }

    @Test
    public void testRead() {
        productRepo.create(TEST_PRODUCT1);
        Optional<Product> product = productRepo.findByName(TEST_PRODUCT_NAME1);
        assertTrue("Could not read product!", product.isPresent());
        assertEquals("Product read did not have the correct name!", TEST_PRODUCT_NAME1, product.get().getName());
    }

    @Test
    public void testUpdate() {
        Product product = productRepo.create(TEST_PRODUCT1);
        String newScope = "123456";

        RemoteProjectInfo newRemoteProjectInfo = new RemoteProjectInfo(newScope, TEST_REMOTE_PROJECT_MANAGER1);
        List<RemoteProjectInfo> newRemoteProjectInfoList = new ArrayList<RemoteProjectInfo>(Arrays.asList(newRemoteProjectInfo));

        product.setName(TEST_PRODUCT_NAME_ALTERNATE1);
        product.setRemoteProductInfo(newRemoteProjectInfoList);
        product = productRepo.update(product);

        assertEquals("Product name was not updated!", TEST_PRODUCT_NAME_ALTERNATE1, product.getName());
        assertEquals("Product remote project info was not updated!", newScope, product.getRemoteProjectInfo().get(0).getScopeId());
    }

    @Test
    public void testDelete() {
        Product createdProduct = productRepo.create(TEST_PRODUCT1);
        assertEquals("Product not created!", 1, productRepo.count());
        productRepo.delete(createdProduct.getId());
        assertEquals("Product was not deleted!", 0, productRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicate() {
        productRepo.create(new Product(TEST_PRODUCT_NAME1));
        productRepo.create(new Product(TEST_PRODUCT_NAME1));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testNameNotNull() {
        productRepo.create(new Product(null));
    }

    @Test
    public void testSetRemoteProductInfo() {
        Product createdProduct = productRepo.create(TEST_PRODUCT1);

        assertEquals("Product has the incorrect name!", TEST_PRODUCT_NAME1, createdProduct.getName());
        assertEquals("Product has the incorrect Remote Project Info!", TEST_PRODUCT1.getRemoteProjectInfo().size(), createdProduct.getRemoteProjectInfo().size());

        productRepo.delete(createdProduct);

        assertEquals("Product repo had incorrect number of products!", 0, productRepo.count());
    }

    @Test
    public void testOtherUrlsCanBeSet() {
        productRepo.create(TEST_PRODUCT1);
        Optional<Product> product = productRepo.findByName(TEST_PRODUCT_NAME1);
        assertTrue("Could not read product!", product.isPresent());
        assertEquals("Product read did not have the correct name!", TEST_PRODUCT_NAME1, product.get().getName());
        assertEquals("Product did not have the expected other URLs", 2, product.get().getOtherUrls().size());
        assertEquals("First other URL does not match", TEST_OTHER_URL1, product.get().getOtherUrls().get(0));
        assertEquals("Second other URL does not match", TEST_OTHER_URL2, product.get().getOtherUrls().get(1));
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @After
    public void cleanup() {
        cleanupRepos();
    }

}
