package edu.tamu.app.cache.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.model.ServiceType;

@ExtendWith(SpringExtension.class)
public class SprintTest {

    @Test
    public void testNewSprint() {
        List<Member> assignees = Arrays.asList(new Member[] { new Member("1", "Bob Boring", "http://gravatar.com/bborring") });
        List<Card> cards = Arrays.asList(new Card[] { new Card("1", "B-00001", "Feature", "Do the thing", "Do it with these requirements", "In Progress", 1.0f, assignees) });
        Sprint sprint = new Sprint("1", "Sprint 1", "Application", ServiceType.GITHUB_MILESTONE.toString(), cards);
        assertEquals(sprint.getId(), "1");
        assertEquals(sprint.getName(), "Sprint 1");
        assertEquals(sprint.getProduct(), "Application");
        assertEquals(ServiceType.GITHUB_MILESTONE.toString(), sprint.getType());
        assertFalse(sprint.getCards().isEmpty());
        assertEquals(1, sprint.getCards().size());
        assertEquals(sprint.getCards().get(0).getId(), "1");
        assertEquals(sprint.getCards().get(0).getNumber(), "B-00001");
        assertEquals(sprint.getCards().get(0).getType(), "Feature");
        assertEquals(sprint.getCards().get(0).getName(), "Do the thing");
        assertEquals(sprint.getCards().get(0).getDescription(), "Do it with these requirements");
        assertEquals(sprint.getCards().get(0).getStatus(), "In Progress");
        assertEquals(1.0, sprint.getCards().get(0).getEstimate(), 0);
        assertFalse(sprint.getCards().get(0).getAssignees().isEmpty());
        assertEquals(1, sprint.getCards().get(0).getAssignees().size());
    }

}
