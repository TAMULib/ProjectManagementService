package edu.tamu.app.cache.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@RunWith(SpringRunner.class)
public class ActiveSprintsCacheControllerTest {

    @Mock
    private ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService;

    @InjectMocks
    private ActiveSprintsCacheController activeSprintsCacheController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(activeSprintsScheduledCacheService.get()).thenReturn(new ArrayList<Sprint>(Arrays.asList(new Sprint[] { getMockSprint() })));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGet() {
        ApiResponse response = activeSprintsCacheController.get();
        assertNotNull("Reponse was null!", response);
        assertEquals("Reponse was not successfull!", ApiStatus.SUCCESS, response.getMeta().getStatus());

        assertNotNull("Reponse payload did not have expected property!", response.getPayload().get("ArrayList<Sprint>"));
        assertSprints((List<Sprint>) response.getPayload().get("ArrayList<Sprint>"));
    }

    @Test
    public void testUpdate() {
        ApiResponse response = activeSprintsCacheController.update();
        assertNotNull(response);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(activeSprintsScheduledCacheService, times(1)).update();
        verify(activeSprintsScheduledCacheService, times(1)).broadcast();
    }

    private Sprint getMockSprint() {
        List<Member> assignees = Arrays.asList(new Member[] { new Member("1", "Bob Boring", "http://gravatar.com/bborring") });
        List<Card> cards = Arrays.asList(new Card[] { new Card("1", "B-00001", "Feature", "Do the thing", "Do it with these requirements", "In Progress", 1.0f, assignees) });
        return new Sprint("1", "Sprint 1", "Application", cards);
    }

    private void assertSprints(List<Sprint> sprints) {
        assertFalse(sprints.isEmpty());
        assertEquals(1, sprints.size());
        assertEquals("1", sprints.get(0).getId());
        assertEquals("Sprint 1", sprints.get(0).getName());
        assertEquals("Application", sprints.get(0).getProject());
        assertFalse(sprints.get(0).getCards().isEmpty());
        assertEquals(1, sprints.get(0).getCards().size());
        assertEquals("1", sprints.get(0).getCards().get(0).getId());
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
