package edu.tamu.app.cache.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.Sprint;

@RunWith(SpringRunner.class)
public class SprintTest {

    @Test
    public void testNewSprint() {
        List<Member> assignees = Arrays.asList(new Member[] { new Member("1", "Bob Boring", "http://gravatar.com/bborring") });
        List<Card> cards = Arrays.asList(new Card[] { new Card("1", "B-00001", "Feature", "Do the thing", "Do it with these requirements", "In Progress", 1.0f, assignees) });
        Sprint sprint = new Sprint("1", "Sprint 1", "Application", cards);
        assertEquals("1", sprint.getId());
        assertEquals("Sprint 1", sprint.getName());
        assertEquals("Application", sprint.getProduct());
        assertFalse(sprint.getCards().isEmpty());
        assertEquals(1, sprint.getCards().size());
        assertEquals("1", sprint.getCards().get(0).getId());
        assertEquals("B-00001", sprint.getCards().get(0).getNumber());
        assertEquals("Feature", sprint.getCards().get(0).getType());
        assertEquals("Do the thing", sprint.getCards().get(0).getName());
        assertEquals("Do it with these requirements", sprint.getCards().get(0).getDescription());
        assertEquals("In Progress", sprint.getCards().get(0).getStatus());
        assertEquals(1.0, sprint.getCards().get(0).getEstimate(), 0);
        assertFalse(sprint.getCards().get(0).getAssignees().isEmpty());
        assertEquals(1, sprint.getCards().get(0).getAssignees().size());
    }

}
