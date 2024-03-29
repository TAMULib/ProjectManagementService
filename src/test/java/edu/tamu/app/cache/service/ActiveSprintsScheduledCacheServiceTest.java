package edu.tamu.app.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.versionone.apiclient.exceptions.APIException;
import com.versionone.apiclient.exceptions.ConnectionException;
import com.versionone.apiclient.exceptions.OidException;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProjectInfo;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@ExtendWith(SpringExtension.class)
public class ActiveSprintsScheduledCacheServiceTest {
    private static final String TEST_PROJECT_SCOPE1 = "0010";
    private static final String TEST_PROJECT_SCOPE2 = "0020";

    private static final String TEST_PROJECT_URL1 = "http://localhost/1";
    private static final String TEST_PROJECT_URL2 = "http://localhost/2";

    private static final String TEST_PROJECT_TOKEN1 = "0123456789";
    private static final String TEST_PROJECT_TOKEN2 = "9876543210";

    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER1 = new RemoteProjectManager("Test Remote Project Manager 1", ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);
    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER2 = new RemoteProjectManager("Test Remote Project Manager 2", ServiceType.GITHUB_PROJECT, TEST_PROJECT_URL2, TEST_PROJECT_TOKEN2);

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO2 = new RemoteProjectInfo(TEST_PROJECT_SCOPE2, TEST_REMOTE_PROJECT_MANAGER2);

    private static final List<RemoteProjectInfo> TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST1 = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1));
    private static final List<RemoteProjectInfo> TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST2 = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO2));

    @Mock
    private ProductRepo productRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService;

    @BeforeEach
    public void setup() throws ConnectionException, APIException, OidException, IOException {
        MockitoAnnotations.openMocks(this);
        VersionOneService versionOneService = mock(VersionOneService.class);
        when(productRepo.findAll()).thenReturn(Arrays.asList(new Product[] { getMockProduct() }));
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);
        when(versionOneService.getActiveSprintsByScopeId(any(String.class))).thenReturn(Arrays.asList(new Sprint[] { getMockSprint() }));
    }

    @Test
    public void testSchedule() {
        activeSprintsScheduledCacheService.schedule();
        assertSprints(activeSprintsScheduledCacheService.get());
    }

    @Test
    public void testUpdate() {
        activeSprintsScheduledCacheService.update();
        assertSprints(activeSprintsScheduledCacheService.get());
    }

    @Test
    public void testBroadcast() {
        activeSprintsScheduledCacheService.broadcast();
        assertTrue(true);
    }

    @Test
    public void testAddProduct() {
        activeSprintsScheduledCacheService.addProduct(getMockProduct());
        assertSprints(activeSprintsScheduledCacheService.get());
    }

    @Test
    public void testUpdateProduct() {
        Product product = getMockProduct();
        activeSprintsScheduledCacheService.addProduct(product);
        product.setName("Another Product");
        product.setRemoteProductInfo(TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST2);
        activeSprintsScheduledCacheService.updateProduct(product);
        assertTrue(true);
    }

    @Test
    public void testRemoveProduct() {
        Product product = getMockProduct();
        activeSprintsScheduledCacheService.addProduct(product);
        activeSprintsScheduledCacheService.removeProduct(product);
        assertTrue(activeSprintsScheduledCacheService.get().isEmpty());
    }

    @Test
    public void testGet() {
        activeSprintsScheduledCacheService.schedule();
        assertSprints(activeSprintsScheduledCacheService.get());
    }

    @Test
    public void testSet() {
        activeSprintsScheduledCacheService.set(Arrays.asList(new Sprint[] { getMockSprint() }));
        assertSprints(activeSprintsScheduledCacheService.get());
    }

    private Product getMockProduct() {
        return new Product("Test Product", TEST_PRODUCT1_REMOTE_PROJECT_INFO_LIST1);
    }

    private Sprint getMockSprint() {
        List<Member> assignees = Arrays.asList(new Member[] { new Member("1", "Bob Boring", "http://gravatar.com/bborring") });
        List<Card> cards = Arrays.asList(new Card[] { new Card("3000", "B-00001", "Feature", "Do the thing", "Do it with these requirements", "In Progress", 1.0f, assignees) });
        return new Sprint("2000", "Sprint 1", "Test Product", ServiceType.GITHUB_MILESTONE.toString(), cards);
    }

    private void assertSprints(List<Sprint> sprints) {
        assertFalse(sprints.isEmpty());
        assertEquals(1, sprints.size());
        assertEquals(sprints.get(0).getId(), "2000");
        assertEquals(sprints.get(0).getName(), "Sprint 1");
        assertEquals(sprints.get(0).getProduct(), "Test Product");
        assertEquals(ServiceType.GITHUB_MILESTONE.toString(), sprints.get(0).getType());
        assertFalse(sprints.get(0).getCards().isEmpty());
        assertEquals(1, sprints.get(0).getCards().size());
        assertEquals(sprints.get(0).getCards().get(0).getId(), "3000");
        assertEquals(sprints.get(0).getCards().get(0).getNumber(), "B-00001");
        assertEquals(sprints.get(0).getCards().get(0).getType(), "Feature");
        assertEquals(sprints.get(0).getCards().get(0).getName(), "Do the thing");
        assertEquals(sprints.get(0).getCards().get(0).getDescription(), "Do it with these requirements");
        assertEquals(sprints.get(0).getCards().get(0).getStatus(), "In Progress");
        assertEquals(1.0, sprints.get(0).getCards().get(0).getEstimate(), 0);
        assertFalse(sprints.get(0).getCards().get(0).getAssignees().isEmpty());
        assertEquals(1, sprints.get(0).getCards().get(0).getAssignees().size());
    }

}
