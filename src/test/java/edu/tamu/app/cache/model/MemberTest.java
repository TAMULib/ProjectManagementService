package edu.tamu.app.cache.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class MemberTest {

    @Test
    public void testNewAssignee() {
        Member member = new Member("1", "Bob Boring", "http://gravatar.com/bborring");
        assertEquals("1", member.getId());
        assertEquals("Bob Boring", member.getName());
        assertEquals("http://gravatar.com/bborring", member.getAvatar());
    }

}
