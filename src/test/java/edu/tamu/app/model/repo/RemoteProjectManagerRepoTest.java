package edu.tamu.app.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

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
import edu.tamu.app.service.ticketing.SugarService;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RemoteProjectManagerRepoTest extends AbstractRepoTest {

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
    }

    @Test
    public void testCreate() {
        RemoteProjectManager remoteProjectManager1 = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        RemoteProjectManager remoteProjectManager2 = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME2, ServiceType.GITHUB_PROJECT, TEST_PROJECT_URL2, TEST_PROJECT_TOKEN2));
        assertEquals(2, remoteProjectManagerRepo.count(), "Remote project manager repo had incorrect number of remote project managers!");
        assertEquals(TEST_REMOTE_PROJECT_MANAGER_NAME1, remoteProjectManager1.getName(), "Remote project manager had incorrect name!");
        assertEquals(ServiceType.VERSION_ONE, remoteProjectManager1.getType(), "Remote project manager had incorrect service type!");
        assertEquals(TEST_PROJECT_URL1, remoteProjectManager1.getUrl(), "Remote project manager had incorrect url!");
        assertEquals(TEST_PROJECT_TOKEN1, remoteProjectManager1.getToken(), "Remote project manager had incorrect token!");
        assertEquals(TEST_REMOTE_PROJECT_MANAGER_NAME2, remoteProjectManager2.getName(), "Remote project manager had incorrect name!");
        assertEquals(ServiceType.GITHUB_PROJECT, remoteProjectManager2.getType(), "Remote project manager had incorrect service type!");
        assertEquals(TEST_PROJECT_URL2, remoteProjectManager2.getUrl(), "Remote project manager had incorrect url!");
        assertEquals(TEST_PROJECT_TOKEN2, remoteProjectManager2.getToken(), "Remote project manager had incorrect token!");
    }

    @Test
    public void testRead() {
        remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        assertEquals(1, remoteProjectManagerRepo.findAll().size(), "Could not read all remote project managers!");
    }

    @Test
    @Transactional
    public void testUpdate() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        remoteProjectManager.setName(TEST_REMOTE_PROJECT_MANAGER_NAME_ALTERNATE1);
        remoteProjectManager = remoteProjectManagerRepo.update(remoteProjectManager);
        assertEquals(TEST_REMOTE_PROJECT_MANAGER_NAME_ALTERNATE1, remoteProjectManager.getName(), "Remote project manager did not update name!");
    }

    @Test
    public void testDelete() {
        RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
        remoteProjectManagerRepo.delete(remoteProjectManager);
        assertEquals(0, remoteProjectManagerRepo.count(), "Remote project manager was not deleted!");
    }

    @Test
    public void testDeleteWhenAssociatedToProduct() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            RemoteProjectManager remoteProjectManager = remoteProjectManagerRepo.create(new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1));
            RemoteProjectInfo remoteProjectInfo = new RemoteProjectInfo("3000", remoteProjectManager);
            List<RemoteProjectInfo> remoteProductInfoList = new ArrayList<RemoteProjectInfo>(Arrays.asList(remoteProjectInfo));

            productRepo.create(new Product(TEST_PRODUCT_NAME1, remoteProductInfoList));
            remoteProjectManagerRepo.delete(remoteProjectManager);
        });
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @AfterEach
    public void cleanup() {
        cleanupRepos();
    }

}
