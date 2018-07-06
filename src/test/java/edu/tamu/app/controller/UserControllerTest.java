package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class UserControllerTest extends AuthMockTests {

    private User aggiejackUser;

    private User aggiejaneUser;

    private Credentials aggiejackCredentials;

    private Credentials aggiejaneCredentials;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserController userController;

    @Before
    public void setup() throws JsonParseException, JsonMappingException, IOException {
        MockitoAnnotations.initMocks(this);
        aggiejackCredentials = getMockAggieJackCredentials();
        aggiejaneCredentials = getMockAggieJaneCredentials();
        aggiejackUser = new User(aggiejackCredentials);
        aggiejaneUser = new User(aggiejaneCredentials);
        when(userRepo.findAll()).thenReturn(new ArrayList<User>(Arrays.asList(new User[] { aggiejackUser, aggiejaneUser })));
        when(userRepo.update(any(User.class))).thenReturn(aggiejackUser);
        doNothing().when(userRepo).delete(any(User.class));
    }

    @Test
    public void testCredentials() {
        ApiResponse apiResponse = userController.credentials(aggiejackCredentials);
        assertEquals("Unable to get user credentials", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllUsers() {
        ApiResponse apiResponse = userController.allUsers();
        assertEquals("Request for users was unsuccessful", SUCCESS, apiResponse.getMeta().getStatus());
        assertEquals("Number of users was not correct", 2, ((ArrayList<User>) apiResponse.getPayload().get("ArrayList<User>")).size());
    }

    @Test
    public void testUpdateUser() {
        ApiResponse apiResponse = userController.updateUser(aggiejackUser);
        assertEquals("User was not successfully updated", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testDelete() {
        ApiResponse apiResponse = userController.delete(aggiejaneUser);
        assertEquals("User was not successfully deleted", SUCCESS, apiResponse.getMeta().getStatus());
    }

}
