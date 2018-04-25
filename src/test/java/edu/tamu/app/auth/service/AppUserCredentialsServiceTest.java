package edu.tamu.app.auth.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.enums.Role;
import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.auth.service.UserCredentialsService;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class AppUserCredentialsServiceTest {
    
    private static final Credentials TEST_CREDENTIALS_1 = new Credentials();
    static {
        TEST_CREDENTIALS_1.setUin("123456789");
        TEST_CREDENTIALS_1.setEmail("aggieJack@tamu.edu");
        TEST_CREDENTIALS_1.setFirstName("Aggie");
        TEST_CREDENTIALS_1.setLastName("Jack");
        TEST_CREDENTIALS_1.setRole("ROLE_USER");
    }
    
    private static final Credentials TEST_CREDENTIALS_2 = new Credentials();
    static {
        TEST_CREDENTIALS_2.setUin("987654321");
        TEST_CREDENTIALS_2.setEmail("aggieJack@tamu.edu");
        TEST_CREDENTIALS_2.setFirstName("Aggie");
        TEST_CREDENTIALS_2.setLastName("Jack");
        TEST_CREDENTIALS_2.setRole("ROLE_USER");
    }
    
    private User testUser1 = new User(TEST_CREDENTIALS_1.getUin(), TEST_CREDENTIALS_1.getEmail(), TEST_CREDENTIALS_1.getFirstName(), TEST_CREDENTIALS_1.getLastName(), Role.valueOf(TEST_CREDENTIALS_1.getRole()));
    private User testUser2 = new User(TEST_CREDENTIALS_2.getUin(), TEST_CREDENTIALS_2.getEmail(), TEST_CREDENTIALS_2.getFirstName(), TEST_CREDENTIALS_2.getLastName(), Role.valueOf(TEST_CREDENTIALS_2.getRole()));
    
    private static final String[] testAdmins = {"123456789", "987654321"};
    
    private Optional<User> optionalUser1 = Optional.of(testUser1);
    private Optional<User> optionalUser2 = Optional.of(testUser2);
    
    @Mock
    private UserRepo userRepo;
    
    @InjectMocks
    private AppUserCredentialsService credentialsService;
    
    @Before
    public void setUp() {
        when(userRepo.findByUsername(TEST_CREDENTIALS_1.getUin())).thenReturn(optionalUser1);
        when(userRepo.findByUsername(TEST_CREDENTIALS_2.getUin())).thenReturn(Optional.empty());
        when(userRepo.create(any(String.class), any(String.class), any(String.class), any(String.class), any(Role.class))).thenReturn(testUser2);
    }

    @Test
    public void testUpdateUserByCredentials() {
        setField(credentialsService, "admins", testAdmins);
        User foundUser = credentialsService.updateUserByCredentials(TEST_CREDENTIALS_1);
        assertEquals("Unable to find user", testUser1, foundUser);
        User unfoundUser = credentialsService.updateUserByCredentials(TEST_CREDENTIALS_2);
        assertEquals("Unable to find user", testUser2, unfoundUser);
    }
    
    @Test
    public void testGetAnonymousRole() {
        String anonRole = credentialsService.getAnonymousRole();
        assertEquals("Anonymous Role not set correctly", Role.ROLE_ANONYMOUS.toString(), anonRole);
    }
}
