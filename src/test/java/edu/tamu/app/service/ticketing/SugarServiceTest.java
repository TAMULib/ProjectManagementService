package edu.tamu.app.service.ticketing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import javax.mail.MessagingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.email.service.EmailSender;

@TestPropertySource
@ExtendWith(MockitoExtension.class)
public class SugarServiceTest {

    private static final Credentials TEST_CREDENTIALS_1 = new Credentials();
    static {
        TEST_CREDENTIALS_1.setUin("123456789");
        TEST_CREDENTIALS_1.setEmail("aggieJack@tamu.edu");
        TEST_CREDENTIALS_1.setFirstName("Aggie");
        TEST_CREDENTIALS_1.setLastName("Jack");
        TEST_CREDENTIALS_1.setRole("ROLE_USER");
    }

    private static final String TEST_REQUEST_TITLE = "Test Ticket Request Title";
    private static final String TEST_REQUEST_DESCRIPTION = "Test Ticket Request Description";
    private static final String TEST_REQUEST_SERVICE = "Test Ticket Request Service";

    private static final String SUCCESSFUL_SUBMIT_TICKET_MESSAGE = "Successfully submitted issue for Test Ticket Request Service!";
    private static final String SUBMIT_TICKET_ERROR_MESSAGE = "Unable to submit ticket to sugar at this time!";

    private static TicketRequest TEST_REQUEST = new TicketRequest(TEST_REQUEST_TITLE, TEST_REQUEST_DESCRIPTION, TEST_REQUEST_SERVICE, TEST_CREDENTIALS_1);

    @Mock
    private EmailSender emailService;

    @InjectMocks
    private SugarService sugarService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(sugarService, "sugarEmail", "helpdesk@library.tamu.edu");
    }

    @Test
    public void testSubmit() throws MessagingException {
        doNothing().when(emailService).sendEmail(any(String.class), any(String.class), any(String.class));
        String result = sugarService.submit(TEST_REQUEST);
        assertEquals(SUCCESSFUL_SUBMIT_TICKET_MESSAGE, result, "Results were not as expected!");
    }

    @Test
    public void testInvalidEmail() throws MessagingException {
        doThrow(MessagingException.class).when(emailService).sendEmail(any(String.class), any(String.class), any(String.class));
        String result = sugarService.submit(TEST_REQUEST);
        assertEquals(SUBMIT_TICKET_ERROR_MESSAGE, result, "Results were not as expected!");
    }
}
