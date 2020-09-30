package edu.tamu.app.model.repo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.InternalRequest;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.service.manager.GitHubService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class InternalRequestRepoTest extends AbstractRepoTest {

    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER1 = new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO2 = new RemoteProjectInfo(TEST_PROJECT_SCOPE2, TEST_REMOTE_PROJECT_MANAGER1);

    private static final List<RemoteProjectInfo> TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1 = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1, TEST_REMOTE_PROJECT_INFO2));

    private static final List<String> TEST_OTHER_URLS1 = new ArrayList<String>(Arrays.asList(TEST_OTHER_URL1, TEST_OTHER_URL2));

    private static final Product TEST_PRODUCT1 = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, TEST_DEV_URL1, TEST_PRE_URL1, TEST_PROD_URL1, TEST_WIKI_URL1, TEST_OTHER_URLS1);

    private static final InternalRequest TEST_INTERNAL_REQUEST1 = new InternalRequest(TEST_INTERNAL_REQUEST_TITLE1, TEST_INTERNAL_REQUEST_DESCRIPTION1, TEST_PRODUCT1, TEST_INTERNAL_REQUEST_CREATED_ON1);

    @MockBean
    private SugarService sugarService;

    @MockBean
    private GitHubService gitHubService;

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
        productRepo.create(TEST_PRODUCT1);
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

    // @After and @Before cannot be safely specified inside a parent class.
    @After
    public void cleanup() {
        cleanupRepos();
    }

}
