package edu.tamu.app.auth.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.app.auth.AuthMockTests;
import edu.tamu.app.model.Role;
import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.model.Credentials;

@RunWith(SpringRunner.class)
public class AppUserCredentialsServiceTest extends AuthMockTests {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private AppUserCredentialsService credentialsService;

    private Credentials aggiejackCredentials;

    private Credentials aggiejackCredentialsWithoutRole;

    private Credentials aggiejackCredentialsUpdated;

    private User aggiejackUser;

    @Before
    public void setUp() throws JsonParseException, JsonMappingException, IOException {
        MockitoAnnotations.initMocks(this);

        setField(credentialsService, "admins", new String[] { "123456789", "987654321" });

        aggiejackCredentials = getMockAggieJackCredentials();

        aggiejackCredentialsWithoutRole = getMockAggieJackCredentials();
        aggiejackCredentialsWithoutRole.setRole(null);

        aggiejackCredentialsUpdated = getMockAggieJackCredentials();
        aggiejackCredentialsUpdated.setRole("ROLE_MANAGER");
        aggiejackCredentialsUpdated.setEmail("jaggie@tamu.edu");
        aggiejackCredentialsUpdated.setFirstName("John");
        aggiejackCredentialsUpdated.setLastName("Agriculture");
        aggiejackCredentialsUpdated.setUin("123456781");

        aggiejackUser = new User(aggiejackCredentials);
    }

    @Test
    public void testUpdateUserByCredentials() {
        when(userRepo.findByUsername(any(String.class))).thenReturn(Optional.empty());
        when(userRepo.create(any(String.class), any(String.class), any(String.class), any(String.class), any(Role.class))).thenReturn(aggiejackUser);
        User user = credentialsService.updateUserByCredentials(aggiejackCredentials);
        assertEquals("Unable to update user with credentials!", aggiejackUser, user);
    }

    @Test
    public void testGetAnonymousRole() {
        assertEquals("Incorrect anonymous role returned from credentials service!", Role.ROLE_ANONYMOUS.toString(), credentialsService.getAnonymousRole());
    }

    @Test
    public void testUpdateUserByCredentialsWithoutRole() {
        when(userRepo.findByUsername(any(String.class))).thenReturn(Optional.of(aggiejackUser));
        when(userRepo.save(any(User.class))).thenReturn(aggiejackUser);
        User userWithDefaultRole = credentialsService.updateUserByCredentials(aggiejackCredentialsWithoutRole);
        assertEquals("User had incorrect default role!", Role.ROLE_ADMIN, userWithDefaultRole.getRole());
    }

    @Test
    public void testChangedUser() {
        when(userRepo.findByUsername(any(String.class))).thenReturn(Optional.of(aggiejackUser));
        when(userRepo.save(any(User.class))).thenReturn(new User(aggiejackCredentialsUpdated));
        User userUpdate = credentialsService.updateUserByCredentials(aggiejackCredentialsUpdated);
        assertEquals("User had the incorrect last name!", aggiejackCredentialsUpdated.getLastName(), userUpdate.getLastName());
        assertEquals("User had the incorrect first name!", aggiejackCredentialsUpdated.getFirstName(), userUpdate.getFirstName());
        assertEquals("User had the incorrect username!", aggiejackCredentialsUpdated.getUin(), userUpdate.getUsername());
        assertEquals("User had the incorrect email!", aggiejackCredentialsUpdated.getEmail(), userUpdate.getEmail());
        assertEquals("User had the incorrect role!", Role.valueOf(aggiejackCredentialsUpdated.getRole()), userUpdate.getRole());
    }

}
