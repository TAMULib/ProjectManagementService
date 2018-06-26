package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/remote-project-manager")
public class RemoteProjectManagerController {

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, remoteProjectManagerRepo.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getOne(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, remoteProjectManagerRepo.findOne(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createRemoteProjectManager(@WeaverValidatedModel RemoteProjectManager remoteProjectManager) {
        logger.info("Model: " + remoteProjectManager.toString());
        logger.info("Creating Version Management Software: " + remoteProjectManager.getName());
        return new ApiResponse(SUCCESS, remoteProjectManagerRepo.create(remoteProjectManager));
    }

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateRemoteProjectManager(@WeaverValidatedModel RemoteProjectManager remoteProjectManager) {
        logger.info("Updating Version Management Software: " + remoteProjectManager.getName());
        return new ApiResponse(SUCCESS, remoteProjectManagerRepo.update(remoteProjectManager));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteRemoteProjectManager(@WeaverValidatedModel RemoteProjectManager remoteProjectManager) {
        logger.info("Deleting Version Management Software: " + remoteProjectManager.getName());
        remoteProjectManagerRepo.delete(remoteProjectManager);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping(value = "/types", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getTypes() {
        return new ApiResponse(SUCCESS, ServiceType.map());
    }

    @RequestMapping(value = "/scaffolding/{type}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getTypeScaffolding(@PathVariable String type) {
        ServiceType serviceType = ServiceType.valueOf(type);
        return new ApiResponse(SUCCESS, serviceType.getScaffold());
    }

}
