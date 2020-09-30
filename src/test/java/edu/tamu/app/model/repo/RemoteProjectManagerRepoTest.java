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
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.service.manager.GitHubService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RemoteProjectManagerRepoTest extends AbstractRepoTest {

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
    }

    @Test
    public void testCreate() {
        RemoteProjectManager remoteProjectManager1 = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        RemoteProjectManager remoteProjectManager2 = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME2, ServiceType.GITHUB, TEST_PROJECT_URL2, TEST_PROJECT_TOKEN2));
        assertEquals("Remote project manager repo had incorrect number of remote project managers!", 2, remoteProjectManagerRepo.count());
        assertEquals("Remote project manager had incorrect name!", TEST_REMOTE_PROJECT_MANAGER_NAME1, remoteProjectManager1.getName());
        assertEquals("Remote project manager had incorrect service type!", ServiceType.VERSION_ONE, remoteProjectManager1.getType());
        assertEquals("Remote project manager had incorrect url!", TEST_PROJECT_URL1, remoteProjectManager1.getUrl());
        assertEquals("Remote project manager had incorrect token!", TEST_PROJECT_TOKEN1, remoteProjectManager1.getToken());
        assertEquals("Remote project manager had incorrect name!", TEST_REMOTE_PROJECT_MANAGER_NAME2, remoteProjectManager2.getName());
        assertEquals("Remote project manager had incorrect service type!", ServiceType.GITHUB, remoteProjectManager2.getType());
        assertEquals("Remote project manager had incorrect url!", TEST_PROJECT_URL2, remoteProjectManager2.getUrl());
        assertEquals("Remote project manager had incorrect token!", TEST_PROJECT_TOKEN2, remoteProjectManager2.getToken());
    }

    @Test
    public void testRead() {
        remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        assertEquals("Could not read all remote project managers!", 1, remoteProjectManagerRepo.findAll().size());
    }

    @Test
    public void testUpdate() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        remoteProjectManager.setName(TEST_REMOTE_PROJECT_MANAGER_NAME_ALTERNATE1);
        remoteProjectManager = remoteProjectManagerRepo.update(remoteProjectManager);
        assertEquals("Remote project manager did not update name!", TEST_REMOTE_PROJECT_MANAGER_NAME_ALTERNATE1, remoteProjectManager.getName());
    }

    @Test
    public void testDelete() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        remoteProjectManagerRepo.delete(remoteProjectManager);
        assertEquals("Remote project manager was not deleted!", 0, remoteProjectManagerRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDeleteWhenAssociatedToProduct() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        RemoteProjectInfo remoteProjectInfo = new RemoteProjectInfo("3000", remoteProjectManager);
        List<RemoteProjectInfo> remoteProductInfoList = new ArrayList<RemoteProjectInfo>(Arrays.asList(remoteProjectInfo));

        productRepo.create(new Product(TEST_PRODUCT_NAME1, remoteProductInfoList));
        remoteProjectManagerRepo.delete(remoteProjectManager);
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @After
    public void cleanup() {
        cleanupRepos();
    }

}
