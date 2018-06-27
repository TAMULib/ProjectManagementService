package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.managing.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.ticketing.SugarService;
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

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Autowired
    private SugarService sugarService;

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
    public ApiResponse submitIssueRequest(@RequestBody TicketRequest request) {
        return new ApiResponse(SUCCESS, sugarService.submit(request));
    }

    @RequestMapping(value = "/feature", method = RequestMethod.POST)
    @PreAuthorize("hasRole('MANAGER') or @whitelist.isAllowed(#req)")
    public ApiResponse pushRequest(HttpServletRequest req, @RequestBody FeatureRequest request) {
        Optional<Project> project = Optional.ofNullable(projectRepo.findOne(request.getProjectId()));
        ApiResponse response;
        if (project.isPresent()) {
            Optional<RemoteProjectManager> remoteProjectManager = Optional.ofNullable(project.get().getRemoteProjectManager());
            if (remoteProjectManager.isPresent()) {
                RemoteProjectManagerBean remoteProjectManagerBean = (RemoteProjectManagerBean) managementBeanRegistry.getService(remoteProjectManager.get().getName());
                request.setScopeId(project.get().getScopeId());
                try {
                    response = new ApiResponse(SUCCESS, remoteProjectManagerBean.push(request));
                } catch (Exception e) {
                    response = new ApiResponse(ERROR, "Error pushing request to " + remoteProjectManager.get().getName() + " for project " + project.get().getName() + "!");
                }
            } else {
                response = new ApiResponse(ERROR, project.get().getName() + " project does not have a Remote Project Manager!");
            }
        } else {
            response = new ApiResponse(ERROR, "Project with id " + request.getProjectId() + " not found!");
        }
        return response;
    }

    @RequestMapping(value = "/{remoteProjectManagerId}/remote-projects", method = RequestMethod.GET)
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getAllRemoteProjects(@PathVariable Long remoteProjectManagerId) {
        Optional<RemoteProjectManager> remoteProjectManager = Optional.ofNullable(remoteProjectManagerRepo.findOne(remoteProjectManagerId));
        ApiResponse response;
        if (remoteProjectManager.isPresent()) {
            RemoteProjectManagerBean remoteProjectManagerBean = (RemoteProjectManagerBean) managementBeanRegistry.getService(remoteProjectManager.get().getName());
            try {
                response = new ApiResponse(SUCCESS, remoteProjectManagerBean.getRemoteProjects());
            } catch (Exception e) {
                response = new ApiResponse(ERROR, "Error fetching remote projects from " + remoteProjectManager.get().getName() + "!");
            }
        } else {
            response = new ApiResponse(ERROR, "Remote Project Manager with id " + remoteProjectManagerId + " not found!");
        }
        return response;
    }

    @RequestMapping(value = "/{remoteProjectManagerId}/remote-projects/{scopeId}", method = RequestMethod.GET)
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