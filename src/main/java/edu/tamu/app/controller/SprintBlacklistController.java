package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.SprintBlacklist;
import edu.tamu.app.model.repo.SprintBlacklistRepo;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/sprint-blacklist")
public class SprintBlacklistController {

    @Autowired
    private SprintBlacklistRepo sprintBlacklistRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, sprintBlacklistRepo.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getOne(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, sprintBlacklistRepo.findOne(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createSprintBlacklist(@WeaverValidatedModel SprintBlacklist sprintBlacklist) {
        logger.info("Creating SprintBlacklist for scopeId: " + sprintBlacklist.getRemoteProductInfo().getScopeId());
        return new ApiResponse(SUCCESS, sprintBlacklistRepo.create(sprintBlacklist));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateSprintBlacklist(@WeaverValidatedModel SprintBlacklist sprintBlacklist) {
        logger.info("Updating SprintBlacklist for scopeId: " + sprintBlacklist.getRemoteProductInfo().getScopeId());
        return new ApiResponse(SUCCESS, sprintBlacklistRepo.update(sprintBlacklist));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteSprintBlacklist(@WeaverValidatedModel SprintBlacklist sprintBlacklist) {
        logger.info("Deleting SprintBlacklist with scopeId: " + sprintBlacklist.getRemoteProductInfo().getScopeId());
        return new ApiResponse(SUCCESS);
    }
}
