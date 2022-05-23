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
import edu.tamu.app.model.CardType;
import edu.tamu.app.service.manager.GitHubProjectService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class CardTypeRepoTest extends AbstractRepoTest {

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
        CardType cardType = cardTypeRepo.create(newCardType("Feature", "Story", "Feature"));
        assertNotNull(cardType, "Unable to create card type!");
        assertEquals(1, cardTypeRepo.count(), "Card type repo had incorrect number of card types!");
        assertEquals("Feature", cardType.getIdentifier(), "Card type had incorrect identifier!");
        assertEquals(2, cardType.getMapping().size(), "Card type had incorrect number of mappings!");
    }

    @Test
    public void testRead() {
        cardTypeRepo.create(newCardType("Feature", "None", "Future"));
        assertNotNull(cardTypeRepo.findByIdentifier("Feature"), "Unable to find card type by identifier!");
        assertTrue(cardTypeRepo.findByMapping("None").isPresent(), "Unable to find card type by mapping!");
        assertTrue(cardTypeRepo.findByMapping("Future").isPresent(), "Unable to find card type by mapping!");
    }

    @Test
    public void testUpdate() {
        CardType cardType = cardTypeRepo.create(newCardType("Story", "Story"));
        cardType.setIdentifier("Feature");
        cardType.setMapping(new HashSet<String>(Arrays.asList(new String[] { "Feature", "Story", "Task" })));
        cardType = cardTypeRepo.update(cardType);
        assertEquals("Feature", cardType.getIdentifier(), "Card type had incorrect identifier!");
        assertEquals(3, cardType.getMapping().size(), "Card type had incorrect number of mappings!");
    }

    @Test
    public void testDelete() {
        CardType cardType = cardTypeRepo.create(newCardType("Feature", "Story", "Feature"));
        cardTypeRepo.delete(cardType);
        assertNull(cardTypeRepo.findByIdentifier("Feature"), "Unable to delete card type!");
        assertEquals(0, cardTypeRepo.count(), "Card type repo had incorrect number of card types!");
    }

    @Test
    public void testDuplicate() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            cardTypeRepo.create(newCardType("Feature", "Story", "Feature"));
            cardTypeRepo.create(newCardType("Feature", "Story"));
        });
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @AfterEach
    public void cleanup() {
        cleanupRepos();
    }

    private CardType newCardType(String identifier, String match1) {
        return new CardType(identifier, new HashSet<String>(Arrays.asList(new String[] { match1 })));
    }

    private CardType newCardType(String identifier, String match1, String match2) {
        return new CardType(identifier, new HashSet<String>(Arrays.asList(new String[] { match1, match2 })));
    }

}
