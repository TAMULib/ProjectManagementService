package edu.tamu.app.model.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.app.auth.AuthMockTests;
import edu.tamu.weaver.auth.model.Credentials;

@RunWith(SpringRunner.class)
public class TicketRequetTest extends AuthMockTests {

    private Credentials aggieJackCredenitals;

    @Before
    public void setup() throws JsonParseException, JsonMappingException, IOException {
        aggieJackCredenitals = getMockAggieJackCredentials();
    }

    @Test
    public void testNewTicketRequest() throws JsonParseException, JsonMappingException, IOException {
        TicketRequest request = createTicketRequest();
        assertNotNull("Could not instantiate ticket request!", request);
        assertEquals("Ticket request had incorrect title!", "Is This Right", request.getTitle());
        assertEquals("Ticket request had incorrect description!", "Does this work as expected!", request.getDescription());
        assertEquals("Ticket request had incorrect service!", "Test Service 1", request.getService());
        assertEquals("Ticket request had incorrect credentials!", aggieJackCredenitals, request.getCredentials());
    }

    @Test
    public void testSetTitle() throws JsonParseException, JsonMappingException, IOException {
        TicketRequest request = createTicketRequest();
        request.setTitle("Is It Good");
        assertEquals("Ticket request did not set title!", "Is It Good", request.getTitle());
    }

    @Test
    public void testSetDescription() throws JsonParseException, JsonMappingException, IOException {
        TicketRequest request = createTicketRequest();
        request.setDescription("It could be better.");
        assertEquals("Ticket request did not set description!", "It could be better.", request.getDescription());
    }

    @Test
    public void testSetService() throws JsonParseException, JsonMappingException, IOException {
        TicketRequest request = createTicketRequest();
        request.setService("Test Service 2");
        assertEquals("Ticket request did not set service!", "Test Service 2", request.getService());
    }

    @Test
    public void testSetCredentials() throws JsonParseException, JsonMappingException, IOException {
        Credentials aggieJaneCredenitals = getMockAggieJaneCredentials();
        TicketRequest request = createTicketRequest();
        request.setCredentials(aggieJaneCredenitals);
        assertEquals("Ticket request did not set credentials!", aggieJaneCredenitals, request.getCredentials());
    }

    private TicketRequest createTicketRequest() throws JsonParseException, JsonMappingException, IOException {
        return new TicketRequest("Is This Right", "Does this work as expected!", "Test Service 1", aggieJackCredenitals);
    }

}
