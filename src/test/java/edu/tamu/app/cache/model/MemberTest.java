package edu.tamu.app.cache.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MemberTest {

    @Test
    public void testNewAssignee() {
        Member member = new Member("1", "Bob Boring", "http://gravatar.com/bborring");
        assertEquals("1", member.getId());
        assertEquals("Bob Boring", member.getName());
        assertEquals("http://gravatar.com/bborring", member.getAvatar());
    }

}
