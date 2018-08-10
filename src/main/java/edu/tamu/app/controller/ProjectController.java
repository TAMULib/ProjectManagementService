package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.app.cache.service.ProjectScheduledCache;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.ticketing.SugarService;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiView;
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

    @Autowired
    private List<ProjectScheduledCache<?, ?>> projectSceduledCaches;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    @JsonView(ApiView.Partial.class)
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, projectRepo.findAll());
    }

    @GetMapping("/{id}")
    @JsonView(ApiView.Partial.class)
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse getOne(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, projectRepo.findOne(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createProject(@WeaverValidatedModel Project project) {
        logger.info("Creating Project: " + project.getName());
        reifyProjectRemoteProjectManager(project);
        project = projectRepo.create(project);
        for (ProjectScheduledCache<?, ?> projectSceduledCache : projectSceduledCaches) {
            projectSceduledCache.addProject(project);
        }
        return new ApiResponse(SUCCESS, project);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateProject(@WeaverValidatedModel Project project) {
        logger.info("Updating Project: " + project.getName());
        reifyProjectRemoteProjectManager(project);
        project = projectRepo.update(project);
        for (ProjectScheduledCache<?, ?> projectSceduledCache : projectSceduledCaches) {
            projectSceduledCache.updateProject(project);
        }
        return new ApiResponse(SUCCESS, project);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE) })
    public ApiResponse deleteProject(@WeaverValidatedModel Project project) {
        logger.info("Deleting Project: " + project.getName());
        reifyProjectRemoteProjectManager(project);
        projectRepo.delete(project);
        for (ProjectScheduledCache<?, ?> projectSceduledCache : projectSceduledCaches) {
            projectSceduledCache.removeProject(project);
        }
        return new ApiResponse(SUCCESS);
    }

    @PostMapping("/issue")
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ApiResponse submitIssueRequest(@RequestBody TicketRequest request) {
        return new ApiResponse(SUCCESS, sugarService.submit(request));
    }

    @PostMapping("/feature")
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

    @GetMapping("/{remoteProjectManagerId}/remote-projects")
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

    private void reifyProjectRemoteProjectManager(Project project) {
        Optional<RemoteProjectManager> remoteProjectManager = Optional.ofNullable(project.getRemoteProjectManager());
        if (remoteProjectManager.isPresent()) {
            Long remoteProjectManagerId = remoteProjectManager.get().getId();
            project.setRemoteProjectManager(remoteProjectManagerRepo.findOne(remoteProjectManagerId));
        }
    }

}