package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;

import java.util.Optional;

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
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/remote-project-manager")
public class RemoteProjectManagerController {

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, remoteProjectManagerRepo.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getOne(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, remoteProjectManagerRepo.findOne(id));
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
        remoteProjectManagerRepo.delete(remoteProjectManager);
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

    @GetMapping("/{remoteProjectManagerId}/remote-projects")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllRemoteProjects(@PathVariable Long remoteProjectManagerId) {
        Optional<RemoteProjectManager> remoteProjectManager = Optional.ofNullable(remoteProjectManagerRepo.findOne(remoteProjectManagerId));
        ApiResponse response;
        if (remoteProjectManager.isPresent()) {
            RemoteProjectManagerBean remoteProjectManagerBean = (RemoteProjectManagerBean) managementBeanRegistry.getService(remoteProjectManager.get().getName());
            try {
                response = new ApiResponse(SUCCESS, remoteProjectManagerBean.getRemoteProject());
            } catch (Exception e) {
                response = new ApiResponse(ERROR, "Error fetching remote projects from " + remoteProjectManager.get().getName() + "!");
            }
        } else {
            response = new ApiResponse(ERROR, "Remote Project Manager with id " + remoteProjectManagerId + " not found!");
        }
        return response;
    }

    @GetMapping("/{remoteProjectManagerId}/remote-projects/{scopeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getRemoteProjectByScopeId(@PathVariable Long remoteProjectManagerId, @PathVariable String scopeId) {
        Optional<RemoteProjectManager> remoteProjectManager = Optional.ofNullable(remoteProjectManagerRepo.findOne(remoteProjectManagerId));
        ApiResponse response;
        if (remoteProjectManager.isPresent()) {
            RemoteProjectManagerBean remoteProjectManagerBean = (RemoteProjectManagerBean) managementBeanRegistry.getService(remoteProjectManager.get().getName());
            try {
                response = new ApiResponse(SUCCESS, remoteProjectManagerBean.getRemoteProjectByScopeId(scopeId));
            } catch (Exception e) {
                response = new ApiResponse(ERROR, "Error fetching remote project with scope id " + scopeId + " from " + remoteProjectManager.get().getName() + "!");
            }
        } else {
            response = new ApiResponse(ERROR, "Remote Project Manager with id " + remoteProjectManagerId + " not found!");
        }
        return response;
    }
}