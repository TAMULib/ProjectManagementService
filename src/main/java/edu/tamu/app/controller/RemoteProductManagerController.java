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

import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.RemoteProductManagerRepo;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/remote-product-manager")
public class RemoteProductManagerController {

    @Autowired
    private RemoteProductManagerRepo remoteProductManagerRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, remoteProductManagerRepo.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getOne(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, remoteProductManagerRepo.findOne(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createRemoteProductManager(@WeaverValidatedModel RemoteProductManager remoteProductManager) {
        logger.info("Model: " + remoteProductManager.toString());
        logger.info("Creating Remote Product Manager: " + remoteProductManager.getName());
        return new ApiResponse(SUCCESS, remoteProductManagerRepo.create(remoteProductManager));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateRemoteProductManager(@WeaverValidatedModel RemoteProductManager remoteProductManager) {
        logger.info("Updating Remote Product Manager: " + remoteProductManager.getName());
        return new ApiResponse(SUCCESS, remoteProductManagerRepo.update(remoteProductManager));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteRemoteProductManager(@WeaverValidatedModel RemoteProductManager remoteProductManager) {
        logger.info("Deleting Remote Product Manager: " + remoteProductManager.getName());
        remoteProductManagerRepo.delete(remoteProductManager);
        return new ApiResponse(SUCCESS);
    }

    @GetMapping("/types")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getTypes() {
        return new ApiResponse(SUCCESS, ServiceType.map());
    }

    @GetMapping("/scaffolding/{type}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getTypeScaffolding(@PathVariable String type) {
        ServiceType serviceType = ServiceType.valueOf(type);
        return new ApiResponse(SUCCESS, serviceType.getScaffold());
    }

}