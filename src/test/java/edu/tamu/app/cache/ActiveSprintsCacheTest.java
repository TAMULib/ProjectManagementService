package edu.tamu.app.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.Sprint;

@RunWith(SpringRunner.class)
public class ActiveSprintsCacheTest {

    @Test
    public void testNewActiveSprintsCache() {
        ActiveSprintsCache cache = new ActiveSprintsCache();
        assertNotNull("New active sprint cache was not created!", cache);
        assertNotNull("New active sprint cache sprints were not created!", cache.get());
    }

    @Test
    public void testSetCache() {
        ActiveSprintsCache cache = new ActiveSprintsCache();
        Sprint sprint = getMockSprint();
        assertTrue("Cached active sprints was not empty!", cache.get().isEmpty());
        cache.set(Arrays.asList(new Sprint[] { sprint }));
        assertFalse("Cached active sprints was empty!", cache.get().isEmpty());
    }

    @Test
    public void testGetCache() {
        ActiveSprintsCache cache = new ActiveSprintsCache();
        cache.set(Arrays.asList(new Sprint[] { getMockSprint() }));
        List<Sprint> sprints = cache.get();
        assertFalse("Cached active sprints was empty!", sprints.isEmpty());
        assertEquals("Cached active sprints had incorrect number of sprints!", 1, sprints.size());
        assertEquals("Cached active sprint had incorrect id!", "1", sprints.get(0).getId());
        assertEquals("Cached active sprint had incorrect name!", "Sprint 1", sprints.get(0).getName());
        assertEquals("Cached active sprint had incorrect project!", "Application", sprints.get(0).getProject());

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
        return new Sprint("1", "Sprint 1", "Application", cards);
    }

}
