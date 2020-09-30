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
import edu.tamu.app.model.CardType;
import edu.tamu.app.service.manager.GitHubService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class CardTypeRepoTest extends AbstractRepoTest {

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
        CardType cardType = cardTypeRepo.create(newCardType("Feature", "Story", "Feature"));
        assertNotNull("Unable to create card type!", cardType);
        assertEquals("Card type repo had incorrect number of card types!", 1, cardTypeRepo.count());
        assertEquals("Card type had incorrect identifier!", "Feature", cardType.getIdentifier());
        assertEquals("Card type had incorrect number of mappings!", 2, cardType.getMapping().size());
    }

    @Test
    public void testRead() {
        cardTypeRepo.create(newCardType("Feature", "None", "Future"));
        assertNotNull("Unable to find card type by identifier!", cardTypeRepo.findByIdentifier("Feature"));
        assertTrue("Unable to find card type by mapping!", cardTypeRepo.findByMapping("None").isPresent());
        assertTrue("Unable to find card type by mapping!", cardTypeRepo.findByMapping("Future").isPresent());
    }

    @Test
    public void testUpdate() {
        CardType cardType = cardTypeRepo.create(newCardType("Story", "Story"));
        cardType.setIdentifier("Feature");
        cardType.setMapping(new HashSet<String>(Arrays.asList(new String[] { "Feature", "Story", "Task" })));
        cardType = cardTypeRepo.update(cardType);
        assertEquals("Card type had incorrect identifier!", "Feature", cardType.getIdentifier());
        assertEquals("Card type had incorrect number of mappings!", 3, cardType.getMapping().size());
    }

    @Test
    public void testDelete() {
        CardType cardType = cardTypeRepo.create(newCardType("Feature", "Story", "Feature"));
        cardTypeRepo.delete(cardType);
        assertNull("Unable to delete card type!", cardTypeRepo.findByIdentifier("Feature"));
        assertEquals("Card type repo had incorrect number of card types!", 0, cardTypeRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicate() {
        cardTypeRepo.create(newCardType("Feature", "Story", "Feature"));
        cardTypeRepo.create(newCardType("Feature", "Story"));
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @After
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
