package edu.tamu.app.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

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
        assertEquals(1, productRepo.count(), "Product repo had incorrect number of products!");
    }

    @Test
    public void testRead() {
        productRepo.create(TEST_PRODUCT1);
        Optional<Product> product = productRepo.findByName(TEST_PRODUCT_NAME1);
        assertTrue(product.isPresent(), "Could not read product!");
        assertEquals(TEST_PRODUCT_NAME1, product.get().getName(), "Product read did not have the correct name!");
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

        assertEquals(TEST_PRODUCT_NAME_ALTERNATE1, product.getName(), "Product name was not updated!");
        assertEquals(newScope, product.getRemoteProjectInfo().get(0).getScopeId(), "Product remote project info was not updated!");
    }

    @Test
    public void testDelete() {
        Product createdProduct = productRepo.create(TEST_PRODUCT1);
        assertEquals(1, productRepo.count(), "Product not created!");
        productRepo.deleteById(createdProduct.getId());
        assertEquals(0, productRepo.count(), "Product was not deleted!");
    }

    @Test
    public void testDuplicate() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            productRepo.create(new Product(TEST_PRODUCT_NAME1));
            productRepo.create(new Product(TEST_PRODUCT_NAME1));
        });
    }

    @Test
    public void testNameNotNull() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            productRepo.create(new Product(null));
        });
    }

    @Test
    public void testSetRemoteProductInfo() {
        Product createdProduct = productRepo.create(TEST_PRODUCT1);

        assertEquals(TEST_PRODUCT_NAME1, createdProduct.getName(), "Product has the incorrect name!");
        assertEquals(TEST_PRODUCT1.getRemoteProjectInfo().size(), createdProduct.getRemoteProjectInfo().size(), "Product has the incorrect Remote Project Info!");

        productRepo.delete(createdProduct);

        assertEquals(0, productRepo.count(), "Product repo had incorrect number of products!");
    }

    @Test
    public void testOtherUrlsCanBeSet() {
        productRepo.create(TEST_PRODUCT1);
        Optional<Product> product = productRepo.findByName(TEST_PRODUCT_NAME1);
        assertTrue(product.isPresent(), "Could not read product!");
        assertEquals(TEST_PRODUCT_NAME1, product.get().getName(), "Product read did not have the correct name!");
        assertEquals(2, product.get().getOtherUrls().size(), "Product did not have the expected other URLs");
        assertEquals(TEST_OTHER_URL1, product.get().getOtherUrls().get(0), "First other URL does not match");
        assertEquals(TEST_OTHER_URL2, product.get().getOtherUrls().get(1), "Second other URL does not match");
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @AfterEach
    public void cleanup() {
        cleanupRepos();
    }

}
