package edu.tamu.app.cache.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.versionone.apiclient.exceptions.APIException;
import com.versionone.apiclient.exceptions.ConnectionException;
import com.versionone.apiclient.exceptions.OidException;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@RunWith(SpringRunner.class)
public class ActiveSprintsScheduledCacheServiceTest {

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService;

    @Before
    public void setup() throws ConnectionException, APIException, OidException, IOException {
        MockitoAnnotations.initMocks(this);
        VersionOneService versionOneService = mock(VersionOneService.class);
        when(projectRepo.findAll()).thenReturn(Arrays.asList(new Project[] { getMockProject() }));
        when(managementBeanRegistry.getService(any(String.class))).thenReturn(versionOneService);
        when(versionOneService.getActiveSprintsByProjectId(any(String.class))).thenReturn(Arrays.asList(new Sprint[] { getMockSprint() }));
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
    public void testAddProject() {
        activeSprintsScheduledCacheService.addProject(getMockProject());
        assertSprints(activeSprintsScheduledCacheService.get());
    }

    @Test
    public void testUpdateProject() {
        Project project = getMockProject();
        activeSprintsScheduledCacheService.addProject(project);
        project.setName("Another Project");
        project.setScopeId("1001");
        activeSprintsScheduledCacheService.updateProject(project);
        assertTrue(true);
    }

    @Test
    public void testRemoveProject() {
        Project project = getMockProject();
        activeSprintsScheduledCacheService.addProject(project);
        activeSprintsScheduledCacheService.removeProject(project);
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

    private Project getMockProject() {
        RemoteProjectManager remoteProjectManager = new RemoteProjectManager("Test Remote Project Manager", ServiceType.VERSION_ONE);
        return new Project("Test Project", "1000", remoteProjectManager);
    }

    private Sprint getMockSprint() {
        List<Member> assignees = Arrays.asList(new Member[] { new Member("1", "Bob Boring", "http://gravatar.com/bborring") });
        List<Card> cards = Arrays.asList(new Card[] { new Card("3000", "B-00001", "Feature", "Do the thing", "Do it with these requirements", "In Progress", 1.0f, assignees) });
        return new Sprint("2000", "Sprint 1", "Test Project", cards);
    }

    private void assertSprints(List<Sprint> sprints) {
        assertFalse(sprints.isEmpty());
        assertEquals(1, sprints.size());
        assertEquals("2000", sprints.get(0).getId());
        assertEquals("Sprint 1", sprints.get(0).getName());
        assertEquals("Test Project", sprints.get(0).getProject());
        assertFalse(sprints.get(0).getCards().isEmpty());
        assertEquals(1, sprints.get(0).getCards().size());
        assertEquals("3000", sprints.get(0).getCards().get(0).getId());
        assertEquals("B-00001", sprints.get(0).getCards().get(0).getNumber());
        assertEquals("Feature", sprints.get(0).getCards().get(0).getType());
        assertEquals("Do the thing", sprints.get(0).getCards().get(0).getName());
        assertEquals("Do it with these requirements", sprints.get(0).getCards().get(0).getDescription());
        assertEquals("In Progress", sprints.get(0).getCards().get(0).getStatus());
        assertEquals(1.0, sprints.get(0).getCards().get(0).getEstimate(), 0);
        assertFalse(sprints.get(0).getCards().get(0).getAssignees().isEmpty());
        assertEquals(1, sprints.get(0).getCards().get(0).getAssignees().size());
    }

}
