package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.enums.Role;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.model.Credentials;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class UserTest {

    @Autowired
    private UserRepo userRepo;

    protected static final Credentials TEST_CREDENTIALS = new Credentials();
    {
        TEST_CREDENTIALS.setUin("123456789");
        TEST_CREDENTIALS.setEmail("aggieJack@tamu.edu");
        TEST_CREDENTIALS.setFirstName("Aggie");
        TEST_CREDENTIALS.setLastName("Jack");
        TEST_CREDENTIALS.setRole("ROLE_USER");
    }

    @Before
    public void setUp() {
        userRepo.deleteAll();
    }

    @Test
    public void testMethod() {

        // Test create user
        User testUser1 = userRepo.create(TEST_CREDENTIALS.getUin(), TEST_CREDENTIALS.getEmail(), TEST_CREDENTIALS.getFirstName(), TEST_CREDENTIALS.getLastName(), Role.valueOf(TEST_CREDENTIALS.getRole()));
        Optional<User> assertUser = userRepo.findByUsername("123456789");

        assertEquals("Test User1 was not added.", testUser1.getUsername(), assertUser.get().getUsername());

        // Test disallow duplicate UINs
        userRepo.create(TEST_CREDENTIALS.getUin(), TEST_CREDENTIALS.getEmail(), TEST_CREDENTIALS.getFirstName(), TEST_CREDENTIALS.getLastName(), Role.valueOf(TEST_CREDENTIALS.getRole()));
        List<User> allUsers = (List<User>) userRepo.findAll();
        assertEquals("Duplicate UIN found.", 1, allUsers.size());

        // Test delete user
        userRepo.delete(testUser1);
        allUsers = (List<User>) userRepo.findAll();
        assertEquals("Test User1 was not removed.", 0, allUsers.size());

    }

    @After
    public void cleanUp() {
        userRepo.deleteAll();
    }

}
