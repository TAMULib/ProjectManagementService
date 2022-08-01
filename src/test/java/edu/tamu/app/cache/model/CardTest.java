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
        assertEquals(card.getId(), "1");
        assertEquals(card.getNumber(), "B-00001");
        assertEquals(card.getType(), "Feature");
        assertEquals(card.getName(), "Do the thing");
        assertEquals(card.getDescription(), "Do it with these requirements");
        assertEquals(card.getStatus(), "In Progress");
        assertEquals(1.0, card.getEstimate(), 0);
        assertFalse(card.getAssignees().isEmpty());
        assertEquals(1, card.getAssignees().size());
        assertEquals(card.getAssignees().get(0).getId(), "1");
        assertEquals(card.getAssignees().get(0).getName(), "Bob Boring");
        assertEquals(card.getAssignees().get(0).getAvatar(), "http://gravatar.com/bborring");
    }

}
