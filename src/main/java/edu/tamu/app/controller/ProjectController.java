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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.request.ProjectRequest;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.versioning.VersionManagementSoftwareBean;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, projectRepo.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse getOne(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, projectRepo.findOne(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createProject(@WeaverValidatedModel Project project) {
        logger.info("Creating Project: " + project.getName());
        return new ApiResponse(SUCCESS, projectRepo.create(project));
    }

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateProject(@WeaverValidatedModel Project project) {
        logger.info("Updating Project: " + project.getName());
        return new ApiResponse(SUCCESS, projectRepo.update(project));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteProject(@WeaverValidatedModel Project project) {
        logger.info("Deleting Project: " + project.getName());
        projectRepo.delete(project);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping(value = "/issue", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse submitIssueRequest(@RequestBody ProjectRequest request) {
        // Project project = projectRepo.findOne(request.getProject());
        // TODO: push directly to Ticket Managment Software
        ApiResponse response;
        response = new ApiResponse(SUCCESS, "Feature not implemented yet!");
        return response;
    }

    @RequestMapping(value = "/feature", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse submitFeatureRequest(@RequestBody ProjectRequest request) {
        // Project project = projectRepo.findOne(request.getProject());
        // TODO: persist as an idea
        ApiResponse response;
        response = new ApiResponse(SUCCESS, "Feature not implemented yet!");
        return response;
    }

    @RequestMapping(value = "/request", method = RequestMethod.POST)
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse pushRequest(@RequestBody ProjectRequest request) {
        Project project = projectRepo.findOne(request.getProject());
        Optional<VersionManagementSoftware> versionManagementSoftware = Optional.ofNullable(project.getVersionManagementSoftware());
        ApiResponse response;
        if (versionManagementSoftware.isPresent()) {
            VersionManagementSoftwareBean versionManagementSoftwareBean = (VersionManagementSoftwareBean) managementBeanRegistry.getService(versionManagementSoftware.get().getName());
            response = new ApiResponse(SUCCESS, versionManagementSoftwareBean.push(request));
        } else {
            response = new ApiResponse(ERROR, project.getName() + " project does not have a version management software!");
        }
        return response;
    }

}
