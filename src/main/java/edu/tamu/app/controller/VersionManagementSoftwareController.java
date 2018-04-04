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
import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.VersionManagementSoftwareRepo;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/version-management-software")
public class VersionManagementSoftwareController {

    @Autowired
    private VersionManagementSoftwareRepo vmsRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, vmsRepo.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getOne(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, vmsRepo.findOne(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createVersionManagementSoftware(@WeaverValidatedModel VersionManagementSoftware vms) {
        logger.info("Model: " + vms.toString());
        logger.info("Creating Version Management Software: " + vms.getName());
        return new ApiResponse(SUCCESS, vmsRepo.create(vms));
    }

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateVersionManagementSoftware(@WeaverValidatedModel VersionManagementSoftware vms) {
        logger.info("Updating Version Management Software: " + vms.getName());
        return new ApiResponse(SUCCESS, vmsRepo.update(vms));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteVersionManagementSoftware(@WeaverValidatedModel VersionManagementSoftware vms) {
        logger.info("Deleting Version Management Software: " + vms.getName());
        vmsRepo.delete(vms);
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
