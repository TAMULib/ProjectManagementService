package edu.tamu.app.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.ServiceType;

@ExtendWith(SpringExtension.class)
public class ActiveSprintsCacheTest {

    @Test
    public void testNewActiveSprintsCache() {
        ActiveSprintsCache cache = new ActiveSprintsCache();
        assertNotNull(cache, "New active sprint cache was not created!");
        assertNotNull(cache.get(), "New active sprint cache sprints were not created!");
    }

    @Test
    public void testSetCache() {
        ActiveSprintsCache cache = new ActiveSprintsCache();
        Sprint sprint = getMockSprint();
        assertTrue(cache.get().isEmpty(), "Cached active sprints was not empty!");
        cache.set(Arrays.asList(new Sprint[] { sprint }));
        assertFalse(cache.get().isEmpty(), "Cached active sprints was empty!");
    }

    @Test
    public void testGetCache() {
        ActiveSprintsCache cache = new ActiveSprintsCache();
        cache.set(Arrays.asList(new Sprint[] { getMockSprint() }));
        List<Sprint> sprints = cache.get();
        assertFalse(sprints.isEmpty(), "Cached active sprints was empty!");
        assertEquals(1, sprints.size(), "Cached active sprints had incorrect number of sprints!");
        assertEquals("1", sprints.get(0).getId(), "Cached active sprint had incorrect id!");
        assertEquals("Sprint 1", sprints.get(0).getName(), "Cached active sprint had incorrect name!");
        assertEquals("Application", sprints.get(0).getProduct(), "Cached active sprint had incorrect product!");

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

    private Sprint getMockSprint() {
        List<Member> assignees = Arrays.asList(new Member[] { new Member("1", "Bob Boring", "http://gravatar.com/bborring") });
        List<Card> cards = Arrays.asList(new Card[] { new Card("1", "B-00001", "Feature", "Do the thing", "Do it with these requirements", "In Progress", 1.0f, assignees) });
        return new Sprint("1", "Sprint 1", "Application", ServiceType.GITHUB_MILESTONE.toString(), cards);
    }

}
