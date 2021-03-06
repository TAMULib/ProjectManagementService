package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.annotation.WeaverCredentials;
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

    /**
     * Get credentials.
     * 
     * @param credentials
     * @WeaverCredentials Credentials
     * @return ApiResponse
     */
    @RequestMapping("/credentials")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse credentials(@WeaverCredentials Credentials credentials) {
        return new ApiResponse(SUCCESS, credentials);
    }

    /**
     * Get all users.
     * 
     * @return ApiResponse
     */
    @RequestMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse allUsers() {
        return new ApiResponse(SUCCESS, userRepo.findAll());
    }

    /**
     * Update user.
     * 
     * @param user
     * @RequestBody User
     * @return ApiResponse
     */
    @RequestMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse updateUser(@RequestBody User user) {
        user = userRepo.update(user);
        return new ApiResponse(SUCCESS, user);
    }

    /**
     * Delete user.
     * 
     * @param user
     * @RequestBody User
     * @return ApiResponse
     */
    @RequestMapping("/delete")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse delete(@RequestBody User user) {
        userRepo.delete(user);
        return new ApiResponse(SUCCESS);
    }

}