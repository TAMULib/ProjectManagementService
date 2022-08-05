package edu.tamu.app.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.auth.AuthMockTests;
import edu.tamu.app.model.Role;
import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.model.Credentials;

@ExtendWith(SpringExtension.class)
public class AppUserCredentialsServiceTest extends AuthMockTests {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private AppUserCredentialsService credentialsService;

    private Credentials aggiejackCredentials;

    private Credentials aggiejackCredentialsWithoutRole;

    private Credentials aggiejackCredentialsUpdated;

    private User aggiejackUser;

    @BeforeEach
    public void setUp() throws JsonParseException, JsonMappingException, IOException {
        MockitoAnnotations.openMocks(this);

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
        assertEquals(aggiejackUser, user, "Unable to update user with credentials!");
    }

    @Test
    public void testGetAnonymousRole() {
        assertEquals(Role.ROLE_ANONYMOUS.toString(), credentialsService.getAnonymousRole(), "Incorrect anonymous role returned from credentials service!");
    }

    @Test
    public void testUpdateUserByCredentialsWithoutRole() {
        when(userRepo.findByUsername(any(String.class))).thenReturn(Optional.of(aggiejackUser));
        when(userRepo.save(any(User.class))).thenReturn(aggiejackUser);
        User userWithDefaultRole = credentialsService.updateUserByCredentials(aggiejackCredentialsWithoutRole);
        assertEquals(Role.ROLE_ADMIN, userWithDefaultRole.getRole(), "User had incorrect default role!");
    }

    @Test
    public void testChangedUser() {
        when(userRepo.findByUsername(any(String.class))).thenReturn(Optional.of(aggiejackUser));
        when(userRepo.save(any(User.class))).thenReturn(new User(aggiejackCredentialsUpdated));
        User userUpdate = credentialsService.updateUserByCredentials(aggiejackCredentialsUpdated);
        assertEquals(aggiejackCredentialsUpdated.getLastName(), userUpdate.getLastName(), "User had the incorrect last name!");
        assertEquals(aggiejackCredentialsUpdated.getFirstName(), userUpdate.getFirstName(), "User had the incorrect first name!");
        assertEquals(aggiejackCredentialsUpdated.getUin(), userUpdate.getUsername(), "User had the incorrect username!");
        assertEquals(aggiejackCredentialsUpdated.getEmail(), userUpdate.getEmail(), "User had the incorrect email!");
        assertEquals(Role.valueOf(aggiejackCredentialsUpdated.getRole()), userUpdate.getRole(), "User had the incorrect role!");
    }

}
