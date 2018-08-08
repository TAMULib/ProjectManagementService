package edu.tamu.app.auth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.app.auth.AuthMockTests;
import edu.tamu.app.model.User;
import edu.tamu.weaver.auth.model.Credentials;

@RunWith(SpringRunner.class)
public class AppUserDetailsServiceTest extends AuthMockTests {

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @Test
    public void testBuildUserDetails() throws JsonParseException, JsonMappingException, IOException {
        Credentials credentials = getMockAggieJackCredentials();
        User user = new User(credentials);
        UserDetails userDetails = appUserDetailsService.buildUserDetails(user);
        assertEquals("User details had the incorrect username!", credentials.getUin(), userDetails.getUsername());
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals("User details had the incorrect number of authorities!", 1, authorities.size());
        assertEquals("User details had the incorrect authority!", credentials.getRole(), authorities.toArray(new GrantedAuthority[authorities.size()])[0].getAuthority());
    }

}
