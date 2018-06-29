package edu.tamu.app.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.Sprint;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.response.VersionProject;
import edu.tamu.app.service.registry.ManagementBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.versioning.VersionManagementSoftwareBean;

@Service
public class SprintsCacheService {

    private final static List<Sprint> activeSprints = new ArrayList<Sprint>();
    private final static List<VersionProject> versionProjects = new ArrayList<VersionProject>();

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Scheduled(fixedDelay = (10 * 60 * 1000))
    public void updateCaches() {
        logger.info("Updating Cache");
        cacheActiveSprints();
        cacheVersionProjects();
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void cacheActiveSprints() {

        try {
            List<Project> projects = projectRepo.findAll();

            if (!projects.isEmpty()) {
                for (Project project : projects) {
                    String vmsName = "";
                    if (project.getVersionManagementSoftware() != null) {
                        vmsName = project.getVersionManagementSoftware().getName();
                    }
                    if (vmsName.equals("")) {
                        logger.info("Project " + project.getName() + " has no associated vms");
                        continue;
                    }
                    VersionManagementSoftwareBean vmsBean = (VersionManagementSoftwareBean) managementBeanRegistry.getService(vmsName);
                    if (vmsBean != null) {
                        List<Sprint> projectSprints = vmsBean.getActiveSprintsByProject(project);

                        if (projectSprints != null) {
                            activeSprints.addAll(projectSprints);
                        }
                    }
                }
            }

            cacheActiveSprints(activeSprints);
        } catch (Exception e) {
            logger.warn("Error while fetching sprints from projects, therefore cache will not be rebuilt.");
            e.printStackTrace();
        }
    }

    public void cacheVersionProjects() {
        List<VersionProject> projects = new ArrayList<VersionProject>();

        try {
            for (ManagementBean vmb : managementBeanRegistry.getAllServices().values()) {
                VersionManagementSoftwareBean vmsb = (VersionManagementSoftwareBean) vmb;
                projects.addAll(vmsb.getVersionProjects());
            }
            cacheVersionProjects(projects);
        } catch (Exception e) {
            logger.warn("Error while fetching version projects, therefore cache will not be rebuilt.");
            logger.warn(e.getStackTrace().toString());
        }
    }

    public synchronized List<Sprint> getActiveSprints() {
        return activeSprints;
    }

    private synchronized void cacheActiveSprints(List<Sprint> activeSprints) {
        SprintsCacheService.activeSprints.clear();
        SprintsCacheService.activeSprints.addAll(activeSprints);
    }

    public synchronized List<VersionProject> getVersionProjects() {
        return versionProjects;
    }

    private synchronized void cacheVersionProjects(List<VersionProject> versionProjects) {
        SprintsCacheService.versionProjects.clear();
        SprintsCacheService.versionProjects.addAll(versionProjects);
    }
}