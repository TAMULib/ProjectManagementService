package edu.tamu.app.cache.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CardTest {

    @Test
    public void testNewCard() {
        List<Member> assignees = Arrays.asList(new Member[] { new Member("1", "Bob Boring", "http://gravatar.com/bborring") });
        Card card = new Card("1", "B-00001", "Feature", "Do the thing", "Do it with these requirements", "In Progress", 1.0f, assignees);
        assertEquals("1", card.getId());
        assertEquals("B-00001", card.getNumber());
        assertEquals("Feature", card.getType());
        assertEquals("Do the thing", card.getName());
        assertEquals("Do it with these requirements", card.getDescription());
        assertEquals("In Progress", card.getStatus());
        assertEquals(1.0, card.getEstimate(), 0);
        assertFalse(card.getAssignees().isEmpty());
        assertEquals(1, card.getAssignees().size());
        assertEquals("1", card.getAssignees().get(0).getId());
        assertEquals("Bob Boring", card.getAssignees().get(0).getName());
        assertEquals("http://gravatar.com/bborring", card.getAssignees().get(0).getAvatar());
    }

}
