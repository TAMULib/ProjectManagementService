package edu.tamu.app.model.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AbstractRequetTest {

    private static final String TEST_TITLE = "Test Title";
    private static final String TEST_DESCRIPTION = "Test Description";

    private AbstractRequest request;

    @BeforeEach
    public void setup() {
        request = Mockito.mock(AbstractRequest.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testTitle() {
        assertNull(request.getTitle(), "Title was already set");
        request.setTitle(TEST_TITLE);
        assertEquals(TEST_TITLE, request.getTitle(), "Title was not set correctly");
    }

    @Test
    public void testDescription() {
        assertNull(request.getDescription(), "Description was already set");
        request.setDescription(TEST_DESCRIPTION);
        assertEquals(TEST_DESCRIPTION, request.getDescription(), "Description was not set correctly");
    }

}
