package edu.tamu.app.model.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

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
import edu.tamu.app.model.Status;
import edu.tamu.app.service.manager.GitHubProjectService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class StatusRepoTest extends AbstractRepoTest {

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
    public void testCreate() {
        Status status = statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        assertNotNull("Unable to create status!", status);
        assertEquals("Status repo had incorrect number of statuses!", 1, statusRepo.count());
        assertEquals("Status had incorrect identifier!", "None", status.getIdentifier());
        assertEquals("Status had incorrect number of mappings!", 2, status.getMapping().size());
    }

    @Test
    public void testRead() {
        statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        assertNotNull("Unable to find status by identifier!", statusRepo.findByIdentifier("None"));
        assertTrue("Unable to find status by mapping!", statusRepo.findByMapping("None").isPresent());
        assertTrue("Unable to find status by mapping!", statusRepo.findByMapping("Future").isPresent());
    }

    @Test
    public void testUpdate() {
        Status status = statusRepo.create(new Status("Unaivable", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        status.setIdentifier("None");
        status.setMapping(new HashSet<String>(Arrays.asList(new String[] { "None", "Future", "NA" })));
        status = statusRepo.update(status);
        assertEquals("Status had incorrect identifier!", "None", status.getIdentifier());
        assertEquals("Status had incorrect number of mappings!", 3, status.getMapping().size());
    }

    @Test
    public void testDelete() {
        Status status = statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        statusRepo.delete(status);
        assertNull("Unable to delete status!", statusRepo.findByIdentifier("None"));
        assertEquals("Status repo had incorrect number of statuses!", 0, statusRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicate() {
        statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @After
    public void cleanup() {
        cleanupRepos();
    }
}
