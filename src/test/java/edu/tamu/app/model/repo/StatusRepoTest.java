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
import edu.tamu.app.model.Status;
import edu.tamu.app.service.manager.GitHubProjectService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

@ExtendWith(SpringExtension.class)
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
        Status status = statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        assertNotNull(status, "Unable to create status!");
        assertEquals(1, statusRepo.count(), "Status repo had incorrect number of statuses!");
        assertEquals("None", status.getIdentifier(), "Status had incorrect identifier!");
        assertEquals(2, status.getMapping().size(), "Status had incorrect number of mappings!");
    }

    @Test
    public void testRead() {
        statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        assertNotNull(statusRepo.findByIdentifier("None"), "Unable to find status by identifier!");
        assertTrue(statusRepo.findByMapping("None").isPresent(), "Unable to find status by mapping!");
        assertTrue(statusRepo.findByMapping("Future").isPresent(), "Unable to find status by mapping!");
    }

    @Test
    public void testUpdate() {
        Status status = statusRepo.create(new Status("Unaivable", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        status.setIdentifier("None");
        status.setMapping(new HashSet<String>(Arrays.asList(new String[] { "None", "Future", "NA" })));
        status = statusRepo.update(status);
        assertEquals("None", status.getIdentifier(), "Status had incorrect identifier!");
        assertEquals(3, status.getMapping().size(), "Status had incorrect number of mappings!");
    }

    @Test
    public void testDelete() {
        Status status = statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        statusRepo.delete(status);
        assertNull(statusRepo.findByIdentifier("None"), "Unable to delete status!");
        assertEquals(0, statusRepo.count(), "Status repo had incorrect number of statuses!");
    }

    @Test
    public void testDuplicate() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
            statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        });
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @AfterEach
    public void cleanup() {
        cleanupRepos();
    }
}
