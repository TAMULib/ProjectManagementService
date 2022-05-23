package edu.tamu.app.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.Estimate;
import edu.tamu.app.service.manager.GitHubProjectService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class EstimateRepoTest extends AbstractRepoTest {

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
        Estimate estimate = estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        assertNotNull(estimate, "Unable to create estimate!");
        assertEquals(1, estimateRepo.count(), "Estimate repo had incorrect number of estimates!");
        assertEquals(1.0f, estimate.getIdentifier(), 0, "Estimate had incorrect identifier!");
        assertEquals(2, estimate.getMapping().size(), "Estimate had incorrect number of mappings!");
    }

    @Test
    public void testRead() {
        estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        assertNotNull(estimateRepo.findByIdentifier(1.0f), "Unable to find estimate by identifier!");
        assertTrue(estimateRepo.findByMapping("Small").isPresent(), "Unable to find estimate by mapping!");
        assertTrue(estimateRepo.findByMapping("small").isPresent(), "Unable to find estimate by mapping!");
    }

    @Test
    public void testUpdate() {
        Estimate estimate = estimateRepo.create(new Estimate(2.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        estimate.setIdentifier(5.0f);
        estimate.setMapping(new HashSet<String>(Arrays.asList(new String[] { "Large", "large", "lg" })));
        estimate = estimateRepo.update(estimate);
        assertEquals(5.0f, estimate.getIdentifier(), 0, "Estimate had incorrect identifier!");
        assertEquals(3, estimate.getMapping().size(), "Estimate had incorrect number of mappings!");
    }

    @Test
    public void testDelete() {
        Estimate estimate = estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        estimateRepo.delete(estimate);
        assertNull(estimateRepo.findByIdentifier(1.0f), "Unable to delete estimate!");
        assertEquals(0, estimateRepo.count(), "Estimate repo had incorrect number of estimates!");
    }

    @Test
    public void testDuplicate() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
            estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small" }))));
        });
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @AfterEach
    public void cleanup() {
        cleanupRepos();
    }

}
