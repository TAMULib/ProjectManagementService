package edu.tamu.app.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.auth.AuthMockTests;
import edu.tamu.app.model.Role;
import edu.tamu.app.model.User;
import edu.tamu.weaver.auth.model.Credentials;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserRepoTest extends AuthMockTests {

    @Autowired
    private UserRepo userRepo;

    private Credentials mockCredentials;

    // @After and @Before cannot be safely specified inside a parent class.
    @BeforeEach
    public void setup() throws JsonParseException, JsonMappingException, IOException {
        mockCredentials = getMockAggieJackCredentials();
    }

    @Test
    public void testCreate() {
        User user = userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        assertTrue(userRepo.findByUsername(mockCredentials.getUin()).isPresent(), "User was not created!");
        assertEquals(1, userRepo.count(), "User repo had incorrect number of users!");

        assertEquals(mockCredentials.getUin(), user.getUsername(), "User had incorrect username!");
        assertEquals(mockCredentials.getEmail(), user.getEmail(), "User had incorrect email!");
        assertEquals(mockCredentials.getFirstName(), user.getFirstName(), "User had incorrect first name!");
        assertEquals(mockCredentials.getLastName(), user.getLastName(), "User had incorrect last name!");
        assertEquals(Role.valueOf(mockCredentials.getRole()), user.getRole(), "User had incorrect role!");
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

        assertEquals("123456781", user.getUsername(), "User had incorrect username!");
        assertEquals("jaggie@tamu.edu", user.getEmail(), "User had incorrect email!");
        assertEquals("John", user.getFirstName(), "User had incorrect first name!");
        assertEquals("Agriculture", user.getLastName(), "User had incorrect last name!");
        assertEquals(Role.valueOf("ROLE_MANAGER"), user.getRole(), "User had incorrect role!");
    }

    @Test
    public void testDuplicate() {
        userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        assertEquals(1, userRepo.count(), "Duplicate user was created!");
    }

    @Test
    public void testDelete() {
        User user = userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        assertEquals(1, userRepo.count(), "User repo had incorrect number of users!");
        userRepo.delete(user);
        assertFalse(userRepo.findByUsername(mockCredentials.getUin()).isPresent(), "User was not deleted!");
        assertEquals(0, userRepo.count(), "User repo had incorrect number of users!");
    }

    @Test
    public void testGetAuthorities() {
        User user = userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size(), "User details had the incorrect number of authorities!");
        assertEquals(mockCredentials.getRole(), authorities.toArray(new GrantedAuthority[authorities.size()])[0].getAuthority(), "User details had the incorrect authority!");
    }

    @Test
    public void testUserDetailsMethods() {
        User user = userRepo.create(mockCredentials.getUin(), mockCredentials.getEmail(), mockCredentials.getFirstName(), mockCredentials.getLastName(), Role.valueOf(mockCredentials.getRole()));
        assertFalse(user.isAccountNonExpired(), "Account non expired was not false!");
        assertFalse(user.isAccountNonLocked(), "Account non locked was not false!");
        assertFalse(user.isCredentialsNonExpired(), "Credentials non expired was not false!");
        assertTrue(user.isEnabled(), "Enabled was not true!");
        assertNull("Password was not null!", user.getPassword());
    }

    // @After and @Before cannot be safely specified inside a parent class.
    @AfterEach
    public void cleanup() {
        userRepo.deleteAll();
    }

}
