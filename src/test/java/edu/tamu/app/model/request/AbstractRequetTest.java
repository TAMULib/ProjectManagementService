package edu.tamu.app.model.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AbstractRequetTest {

    private static final String TEST_TITLE = "Test Title";
    private static final String TEST_DESCRIPTION = "Test Description";

    private static AbstractRequest request = Mockito.mock(AbstractRequest.class, Mockito.CALLS_REAL_METHODS);

    @Test
    public void testSetTitle() {
        assertNull("Title was already set", request.getTitle());
        request.setTitle(TEST_TITLE);
        assertEquals("Title was not set correctly", TEST_TITLE, request.getTitle());
    }

    @Test
    public void testSetDescription() {
        assertNull("Description was already set", request.getDescription());
        request.setDescription(TEST_DESCRIPTION);
        assertEquals("Description was not set correctly", TEST_DESCRIPTION, request.getDescription());
    }
}
