package edu.tamu.app.auth.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.app.auth.AuthMockTests;
import edu.tamu.app.model.Role;
import edu.tamu.app.model.User;
import edu.tamu.weaver.auth.model.Credentials;

@RunWith(SpringRunner.class)
public class AppUserDetailsTest extends AuthMockTests {

    @Test
    public void testNewAppUserDetails() throws JsonParseException, JsonMappingException, IOException {
        Credentials credentials = getMockAggieJackCredentials();
        User user = new User(credentials);
        AppUserDetails appUserDetails = new AppUserDetails(user);
        assertEquals("App user details had the incorrect last name!", credentials.getLastName(), appUserDetails.getLastName());
        assertEquals("App user details had the incorrect first name!", credentials.getFirstName(), appUserDetails.getFirstName());
        assertEquals("App user details had the incorrect username!", credentials.getUin(), appUserDetails.getUsername());
        assertEquals("App user details had the incorrect email!", credentials.getEmail(), appUserDetails.getEmail());
        assertEquals("App user details had the incorrect role!", Role.valueOf(credentials.getRole()), appUserDetails.getRole());
    }

}
