package edu.tamu.app.service;

import java.util.ArrayList;
import java.util.List;

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

    @Scheduled(fixedDelay = (10 * 60 * 1000))
    public void cacheActiveSprints() {
        String projectName = "";

        try {
            List<Project> projects = projectRepo.findAll();

            if (!projects.isEmpty()) {
                for (Project project : projects) {
                    projectName = project.getVersionManagementSoftware().getName();
                    VersionManagementSoftwareBean vmsBean = (VersionManagementSoftwareBean) managementBeanRegistry.getService(projectName);

                    List<Sprint> projectSprints = vmsBean.getActiveSprintsByProject(project);

                    if (projectSprints != null) {
                        activeSprints.addAll(projectSprints);
                    }
                }
            }

            cacheActiveSprints(activeSprints);
        } catch (Exception e) {
            System.out.println("Error while fetching sprints from projects, therefore cache will not be rebuilt.");
            System.out.println(e.getStackTrace());
        }
    }

    public synchronized List<Sprint> getActiveSprints() {
        return activeSprints;
    }

    private synchronized void cacheActiveSprints(List<Sprint> activeSprints) {
        SprintsCacheService.activeSprints.clear();
        SprintsCacheService.activeSprints.addAll(activeSprints);
    }
}