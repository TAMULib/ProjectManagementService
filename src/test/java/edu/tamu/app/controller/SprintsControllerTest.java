package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.model.Assignee;
import edu.tamu.app.model.Card;
import edu.tamu.app.model.Sprint;
import edu.tamu.app.service.SprintsCacheService;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class SprintsControllerTest {
    private static final String TEST_PROJECT1_NAME = "Test Project 1 Name";
    private static final String TEST_PROJECT2_NAME = "Test Project 2 Name";

    private static final String TEST_SPRINT1_IDENTIFIER = "Test Sprint 1 Identifier";
    private static final String TEST_SPRINT1_NAME = "Test Sprint 1 Name";
    private static final String TEST_SPRINT2_IDENTIFIER = "Test Sprint 2 Identifier";
    private static final String TEST_SPRINT2_NAME = "Test Sprint 2 Name";

    private static final String TEST_ASSIGNEE1_NAME = "Test Assignee 1 Name";
    private static final String TEST_ASSIGNEE2_NAME = "Test Assignee 2 Name";

    private static final Assignee TEST_ASSIGNEE1 = new Assignee(TEST_ASSIGNEE1_NAME);
    private static final Assignee TEST_ASSIGNEE2 = new Assignee(TEST_ASSIGNEE2_NAME);

    private static final String TEST_CARD1_NAME = "Test Card 1 Name";
    private static final String TEST_CARD1_TITLE= "Test Card 1 Title";
    private static final String TEST_CARD1_BODY = "Test Card 1 Body";
    private static final String TEST_CARD1_STATUS = "Test Card 1 Status";
    private static final String TEST_CARD1_CARDTYPE = "Test Card 1 CardType";

    private static final String TEST_CARD2_NAME = "Test Card 2 Name";
    private static final String TEST_CARD2_TITLE= "Test Card 2 Title";
    private static final String TEST_CARD2_BODY = "Test Card 2 Body";
    private static final String TEST_CARD2_STATUS = "Test Card 2 Status";
    private static final String TEST_CARD2_CARDTYPE = "Test Card 2 CardType";

    private static final List<Assignee> TEST_CARD1_ASSIGNEES = new ArrayList<Assignee>(Arrays.asList(new Assignee[] { TEST_ASSIGNEE1, TEST_ASSIGNEE2 }));
    private static final List<Assignee> TEST_CARD2_ASSIGNEES = new ArrayList<Assignee>();

    private static final Card TEST_CARD1= new Card(TEST_CARD1_NAME, TEST_CARD1_TITLE, TEST_CARD1_BODY, TEST_CARD1_ASSIGNEES, TEST_CARD1_STATUS, TEST_CARD1_CARDTYPE);
    private static final Card TEST_CARD2= new Card(TEST_CARD2_NAME, TEST_CARD2_TITLE, TEST_CARD2_BODY, TEST_CARD2_ASSIGNEES, TEST_CARD2_STATUS, TEST_CARD2_CARDTYPE);

    private static final List<Card> TEST_SPRINT1_CARDS = new ArrayList<Card>(Arrays.asList(new Card[] { TEST_CARD1, TEST_CARD2 }));

    private static Sprint TEST_SPRINT1 = new Sprint(TEST_SPRINT1_IDENTIFIER, TEST_SPRINT1_NAME, TEST_PROJECT1_NAME, TEST_SPRINT1_CARDS);
    private static Sprint TEST_SPRINT2 = new Sprint(TEST_SPRINT2_IDENTIFIER, TEST_SPRINT2_NAME, TEST_PROJECT2_NAME);

    private static List<Sprint> mockSprintList = new ArrayList<Sprint>(Arrays.asList(new Sprint[] { TEST_SPRINT1, TEST_SPRINT2 }));

    private static ApiResponse apiResponse;

    @Autowired
    private SprintsCacheService sprintsCache;

    @InjectMocks
    private SprintController sprintController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(sprintsCache.getActiveSprints()).thenReturn(mockSprintList);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindAllActive() {
        apiResponse = sprintController.findAllActive();
        assertEquals("Not successful at getting requested Project", SUCCESS, apiResponse.getMeta().getStatus());
        List<Sprint> sprints = (List<Sprint>) apiResponse.getPayload().get("ArrayList<Sprint>");
        assertEquals("Did not get the expected Sprints", sprintsCache, sprints);
    }
}