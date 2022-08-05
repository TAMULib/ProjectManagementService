package edu.tamu.app.auth.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.app.auth.AuthMockTests;
import edu.tamu.app.model.User;
import edu.tamu.weaver.auth.model.Credentials;

@ExtendWith(SpringExtension.class)
public class AppUserDetailsTest extends AuthMockTests {

    @Test
    public void testNewAppUserDetails() throws JsonParseException, JsonMappingException, IOException {
        Credentials credentials = getMockAggieJackCredentials();
        User user = new User(credentials);
        AppUserDetails appUserDetails = new AppUserDetails(user);
        assertEquals(credentials.getLastName(), appUserDetails.getLastName(), "App user details had the incorrect last name!");
        assertEquals(credentials.getFirstName(), appUserDetails.getFirstName(), "App user details had the incorrect first name!");
        assertEquals(credentials.getUin(), appUserDetails.getUsername(), "App user details had the incorrect username!");
        assertEquals(credentials.getEmail(), appUserDetails.getEmail(), "App user details had the incorrect email!");
        assertEquals(credentials.getRole(), appUserDetails.getRole().toString(), "App user details had the incorrect role!");
    }

}
