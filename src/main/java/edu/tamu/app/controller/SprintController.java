package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.service.SprintsCacheService;
import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/sprints")
public class SprintController {

    @Autowired
    private SprintsCacheService sprintsCache;

    @GetMapping(value = "/active")
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse findAllActive() {
        return new ApiResponse(SUCCESS, sprintsCache.getActiveSprints());
    }
}
