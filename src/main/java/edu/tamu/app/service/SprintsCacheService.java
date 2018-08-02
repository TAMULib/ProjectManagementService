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
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.versioning.VersionManagementSoftwareBean;

@Service
public class SprintsCacheService {

    private final static List<Sprint> activeSprints = new ArrayList<Sprint>();

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedDelayString = "${app.sprint.cache.interval}", initialDelayString = "${app.sprint.cache.delay}")
    public void cacheActiveSprints() {
        logger.info("Updating Cache");
        try {
            List<Sprint> sprints = new ArrayList<Sprint>();
            List<Project> projects = projectRepo.findAll();

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

                    if (!projectSprints.isEmpty()) {
                        sprints.addAll(projectSprints);
                    }
                }
            }

            cacheActiveSprints(sprints);
        } catch (Exception e) {
            logger.warn("Error while fetching sprints from projects, therefore cache will not be rebuilt.");
            e.printStackTrace();
        }
        logger.info("Cache updated with " + SprintsCacheService.activeSprints.size() + " sprints");
    }

    public synchronized List<Sprint> getActiveSprints() {
        return activeSprints;
    }

    private synchronized void cacheActiveSprints(List<Sprint> activeSprints) {
        SprintsCacheService.activeSprints.clear();
        SprintsCacheService.activeSprints.addAll(activeSprints);
    }
}