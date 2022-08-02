package edu.tamu.app.model.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.auth.AuthMockTests;
import edu.tamu.weaver.auth.model.Credentials;

@ExtendWith(SpringExtension.class)
public class TicketRequetTest extends AuthMockTests {

    private Credentials aggieJackCredenitals;

    @BeforeEach
    public void setup() throws JsonParseException, JsonMappingException, IOException {
        aggieJackCredenitals = getMockAggieJackCredentials();
    }

    @Test
    public void testNewTicketRequest() throws JsonParseException, JsonMappingException, IOException {
        TicketRequest request = createTicketRequest();
        assertNotNull(request, "Could not instantiate ticket request!");
        assertEquals("Is This Right", request.getTitle(), "Ticket request had incorrect title!");
        assertEquals("Does this work as expected!", request.getDescription(), "Ticket request had incorrect description!");
        assertEquals("Test Service 1", request.getService(), "Ticket request had incorrect service!");
        assertEquals(aggieJackCredenitals, request.getCredentials(), "Ticket request had incorrect credentials!");
    }

    @Test
    public void testSetTitle() throws JsonParseException, JsonMappingException, IOException {
        TicketRequest request = createTicketRequest();
        request.setTitle("Is It Good");
        assertEquals("Is It Good", request.getTitle(), "Ticket request did not set title!");
    }

    @Test
    public void testSetDescription() throws JsonParseException, JsonMappingException, IOException {
        TicketRequest request = createTicketRequest();
        request.setDescription("It could be better.");
        assertEquals("It could be better.", request.getDescription(), "Ticket request did not set description!");
    }

    @Test
    public void testSetService() throws JsonParseException, JsonMappingException, IOException {
        TicketRequest request = createTicketRequest();
        request.setService("Test Service 2");
        assertEquals("Test Service 2", request.getService(), "Ticket request did not set service!");
    }

    @Test
    public void testSetCredentials() throws JsonParseException, JsonMappingException, IOException {
        Credentials aggieJaneCredenitals = getMockAggieJaneCredentials();
        TicketRequest request = createTicketRequest();
        request.setCredentials(aggieJaneCredenitals);
        assertEquals(aggieJaneCredenitals, request.getCredentials(), "Ticket request did not set credentials!");
    }

    private TicketRequest createTicketRequest() throws JsonParseException, JsonMappingException, IOException {
        return new TicketRequest("Is This Right", "Does this work as expected!", "Test Service 1", aggieJackCredenitals);
    }

}
