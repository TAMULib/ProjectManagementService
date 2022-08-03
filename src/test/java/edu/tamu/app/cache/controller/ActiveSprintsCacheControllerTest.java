package edu.tamu.app.cache.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.model.ServiceType;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@ExtendWith(SpringExtension.class)
public class ActiveSprintsCacheControllerTest {

    @Mock
    private ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService;

    @InjectMocks
    private ActiveSprintsCacheController activeSprintsCacheController;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(activeSprintsScheduledCacheService.get()).thenReturn(new ArrayList<Sprint>(Arrays.asList(new Sprint[] { getMockSprint() })));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGet() {
        ApiResponse response = activeSprintsCacheController.get();
        assertNotNull(response, "Response was null!");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), "Response was not successful!");

        assertNotNull(response.getPayload().get("ArrayList<Sprint>"), "Response payload did not have expected property!");
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
        return new Sprint("1", "Sprint 1", "Application", ServiceType.GITHUB_MILESTONE.toString(), cards);
    }

    private void assertSprints(List<Sprint> sprints) {
        assertFalse(sprints.isEmpty());
        assertEquals(1, sprints.size());
        assertEquals(sprints.get(0).getId(), "1");
        assertEquals(sprints.get(0).getName(), "Sprint 1");
        assertEquals(sprints.get(0).getProduct(), "Application");
        assertEquals(ServiceType.GITHUB_MILESTONE.toString(), sprints.get(0).getType());
        assertFalse(sprints.get(0).getCards().isEmpty());
        assertEquals(1, sprints.get(0).getCards().size());
        assertEquals(sprints.get(0).getCards().get(0).getId(), "1");
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
