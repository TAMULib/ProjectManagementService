package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.auth.AuthMockTests;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.model.Credentials;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class UserTest extends AuthMockTests {

    @Autowired
    private UserRepo userRepo;

    private Credentials mockCredentials;

    @Before
    public void setUp() throws JsonParseException, JsonMappingException, IOException {
        mockCredentials = getMockAggieJackCredentials();
    }

    @Test
    public void testCreate() {
        User user = userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        assertTrue("User was not created!", userRepo.findByUsername(mockCredentials.getUin()).isPresent());
        assertEquals("User repo had incorrect number of users!", 1, userRepo.count());

        assertEquals("User had incorrect username!", mockCredentials.getUin(), user.getUsername());
        assertEquals("User had incorrect email!", mockCredentials.getEmail(), user.getEmail());
        assertEquals("User had incorrect first name!", mockCredentials.getFirstName(), user.getFirstName());
        assertEquals("User had incorrect last name!", mockCredentials.getLastName(), user.getLastName());
        assertEquals("User had incorrect role!", Role.valueOf(mockCredentials.getRole()), user.getRole());
    }

    @Test
    public void testUpdate() {
        User user = userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));

        user.setRole(Role.valueOf("ROLE_MANAGER"));
        user.setEmail("jaggie@tamu.edu");
        user.setFirstName("John");
        user.setLastName("Agriculture");
        user.setUsername("123456781");

        user = userRepo.update(user);

        assertEquals("User had incorrect username!", "123456781", user.getUsername());
        assertEquals("User had incorrect email!", "jaggie@tamu.edu", user.getEmail());
        assertEquals("User had incorrect first name!", "John", user.getFirstName());
        assertEquals("User had incorrect last name!", "Agriculture", user.getLastName());
        assertEquals("User had incorrect role!", Role.valueOf("ROLE_MANAGER"), user.getRole());
    }

    @Test
    public void testDuplicate() {
        userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        assertEquals("Duplicate user was created!", 1, userRepo.count());
    }

    @Test
    public void testDelete() {
        User user = userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        assertEquals("User repo had incorrect number of users!", 1, userRepo.count());
        userRepo.delete(user);
        assertFalse("User was not deleted!", userRepo.findByUsername(mockCredentials.getUin()).isPresent());
        assertEquals("User repo had incorrect number of users!", 0, userRepo.count());
    }

    @Test
    public void testGetAuthorities() {
        User user = userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals("User details had the incorrect number of authorities!", 1, authorities.size());
        assertEquals("User details had the incorrect authority!", mockCredentials.getRole(), authorities.toArray(new GrantedAuthority[authorities.size()])[0].getAuthority());
    }

    @Test
    public void testUserDetailsMethods() {
        User user = userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        assertFalse("Account non expired was not false!", user.isAccountNonExpired());
        assertFalse("Account non locked was not false!", user.isAccountNonLocked());
        assertFalse("Credentials non expired was not false!", user.isCredentialsNonExpired());
        assertTrue("Enabled was not true!", user.isEnabled());
        assertNull("Password was not null!", user.getPassword());
    }

    @After
    public void cleanUp() {
        userRepo.deleteAll();
    }

}
