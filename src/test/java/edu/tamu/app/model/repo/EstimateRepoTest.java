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
import edu.tamu.app.model.Estimate;
import edu.tamu.app.service.manager.GitHubService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class EstimateRepoTest extends RepoTest {

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
        Estimate estimate = estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        assertNotNull("Unable to create estimate!", estimate);
        assertEquals("Estimate repo had incorrect number of estimates!", 1, estimateRepo.count());
        assertEquals("Estimate had incorrect identifier!", 1.0f, estimate.getIdentifier(), 0);
        assertEquals("Estimate had incorrect number of mappings!", 2, estimate.getMapping().size());
    }

    @Test
    public void testRead() {
        estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        assertNotNull("Unable to find estimate by identifier!", estimateRepo.findByIdentifier(1.0f));
        assertTrue("Unable to find estimate by mapping!", estimateRepo.findByMapping("Small").isPresent());
        assertTrue("Unable to find estimate by mapping!", estimateRepo.findByMapping("small").isPresent());
    }

    @Test
    public void testUpdate() {
        Estimate estimate = estimateRepo.create(new Estimate(2.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        estimate.setIdentifier(5.0f);
        estimate.setMapping(new HashSet<String>(Arrays.asList(new String[] { "Large", "large", "lg" })));
        estimate = estimateRepo.update(estimate);
        assertEquals("Estimate had incorrect identifier!", 5.0f, estimate.getIdentifier(), 0);
        assertEquals("Estimate had incorrect number of mappings!", 3, estimate.getMapping().size());
    }

    @Test
    public void testDelete() {
        Estimate estimate = estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        estimateRepo.delete(estimate);
        assertNull("Unable to delete estimate!", estimateRepo.findByIdentifier(1.0f));
        assertEquals("Estimate repo had incorrect number of estimates!", 0, estimateRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicate() {
        estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small" }))));
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @After
    public void cleanup() {
        cleanupRepos();
    }

}
