package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.annotation.WeaverCredentials;
import edu.tamu.weaver.auth.annotation.WeaverUser;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiResponse;

/**
 * User Controller
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Websocket endpoint to request credentials.
     * 
     * @param credentials
     * @WeaverCredentials Credentials
     * @return ApiResponse
     */
    @RequestMapping("/credentials")
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse credentials(@WeaverCredentials Credentials credentials) {
        if (credentials == null) {
            return new ApiResponse(ERROR, "Unable to retrieve credentials!");
        }
        return new ApiResponse(SUCCESS, credentials);
    }

    /**
     * Endpoint to get user.
     * 
     * @param user
     * @ApiModel AppUser
     * @return ApiResponse
     */
    @RequestMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getUser(@WeaverUser User user) {
        if (user == null) {
            return new ApiResponse(ERROR, "Unable to retrieve user!");
        }
        return new ApiResponse(SUCCESS, user);
    }

    /**
     * Endpoint to get all users.
     * 
     * @return ApiResponse
     */
    @RequestMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse allUsers() {
        return new ApiResponse(SUCCESS, userRepo.findAll());
    }

    /**
     * Endpoint to update user.
     * 
     * @param user
     * @ApiModel AppUser
     * @return ApiResponse
     */
    @RequestMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse updateUser(@RequestBody User user) {
        user = userRepo.save(user);
        simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userRepo.findAll()));
        return new ApiResponse(SUCCESS, user);
    }

    /**
     * Endpoint to delete user.
     * 
     * @param user
     * @ApiModel AppUser
     * @return ApiResponse
     */
    @RequestMapping("/delete")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse delete(@RequestBody User user) {
        userRepo.delete(user);
        return new ApiResponse(SUCCESS);
    }

}