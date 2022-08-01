package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
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

import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/remote-project-manager")
public class RemoteProjectManagerController {

    private static final Logger logger = LoggerFactory.getLogger(RemoteProjectManagerController.class);

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Autowired
    private ProductRepo productRepo;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, remoteProjectManagerRepo.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getOne(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, remoteProjectManagerRepo.findById(id).get());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createRemoteProjectManager(@WeaverValidatedModel RemoteProjectManager remoteProjectManager) {
        logger.info("Model: " + remoteProjectManager.toString());
        logger.info("Creating Remote Project Manager: " + remoteProjectManager.getName());
        return new ApiResponse(SUCCESS, remoteProjectManagerRepo.create(remoteProjectManager));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateRemoteProjectManager(@WeaverValidatedModel RemoteProjectManager remoteProjectManager) {
        logger.info("Updating Remote Project Manager: " + remoteProjectManager.getName());
        return new ApiResponse(SUCCESS, remoteProjectManagerRepo.update(remoteProjectManager));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteRemoteProjectManager(@WeaverValidatedModel RemoteProjectManager remoteProjectManager) {
        logger.info("Deleting Remote Project Manager: " + remoteProjectManager.getName());

        if (productRepo.countByRemoteProjectInfoRemoteProjectManagerId(remoteProjectManager.getId()) > 0) {
            return new ApiResponse(ERROR, "Cannot delete Remote Project Manager " + remoteProjectManager.getName() + " because it has one or more associated Products.");
        }

        remoteProjectManagerRepo.delete(remoteProjectManager);
        return new ApiResponse(SUCCESS);
    }

    @GetMapping("/types")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getTypes() {
        return new ApiResponse(SUCCESS, ServiceType.map());
    }
}