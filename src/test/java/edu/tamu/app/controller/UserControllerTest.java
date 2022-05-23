package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.app.auth.AuthMockTests;
import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiResponse;

@ExtendWith(SpringExtension.class)
public class UserControllerTest extends AuthMockTests {

    private User aggiejackUser;

    private User aggiejaneUser;

    private Credentials aggiejackCredentials;

    private Credentials aggiejaneCredentials;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() throws JsonParseException, JsonMappingException, IOException {
        MockitoAnnotations.openMocks(this);
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
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Unable to get user credentials");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllUsers() {
        ApiResponse apiResponse = userController.allUsers();
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Request for users was unsuccessful");
        assertEquals(2, ((ArrayList<User>) apiResponse.getPayload().get("ArrayList<User>")).size(), "Number of users was not correct");
    }

    @Test
    public void testUpdateUser() {
        ApiResponse apiResponse = userController.updateUser(aggiejackUser);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "User was not successfully updated");
    }

    @Test
    public void testDelete() {
        ApiResponse apiResponse = userController.delete(aggiejaneUser);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "User was not successfully deleted");
    }

}