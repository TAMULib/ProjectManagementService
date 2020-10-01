package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.service.manager.GitHubProjectService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

@RunWith(SpringRunner.class)
public class RemoteProjectInfoTest extends AbstractModelTest {

    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER1 = new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);
    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER2 = new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME2, ServiceType.GITHUB_PROJECT, TEST_PROJECT_URL2, TEST_PROJECT_TOKEN2);

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
    }

    @Test
    public void testGetScopeId() {
        RemoteProjectInfo rpi = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
        assertEquals("RemoteProjectInfo did not return the correct scope id!", TEST_PROJECT_SCOPE1, rpi.getScopeId());
    }

    @Test
    public void testGetRemoteProjectManager() {
        RemoteProjectInfo rpi = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
        assertEquals("RemoteProjectInfo did not return the correct remote project manager!", TEST_REMOTE_PROJECT_MANAGER1, rpi.getRemoteProjectManager());
    }

    @Test
    public void testSetScopeId() {
        RemoteProjectInfo rpi = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
        rpi.setScopeId(TEST_PROJECT_SCOPE2);
        assertEquals("RemoteProjectInfo did not correctly update the scope id!", TEST_PROJECT_SCOPE2, rpi.getScopeId());
    }

    @Test
    public void testSetRemoteProductManager() {
        RemoteProjectInfo rpi = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
        rpi.setRemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER2);
        assertEquals("RemoteProjectInfo did not return the correct remote project manager!", TEST_REMOTE_PROJECT_MANAGER2, rpi.getRemoteProjectManager());
    }

}
