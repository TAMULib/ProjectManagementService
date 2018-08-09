package edu.tamu.app.cache.service;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.tamu.app.cache.ProjectsStatsCache;
import edu.tamu.app.cache.model.ProjectStats;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.weaver.response.ApiResponse;

@Service
public class ProjectsStatsScheduledCacheService extends AbstractScheduledCacheService<List<ProjectStats>, ProjectsStatsCache> {

    private static final Logger logger = Logger.getLogger(ProjectsStatsScheduledCacheService.class);

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    public ProjectsStatsScheduledCacheService() {
        super(new ProjectsStatsCache());
    }

    @Override
    @Scheduled(initialDelayString = "${app.cache.projects-stats.delay}", fixedDelayString = "${app.cache.projects-stats.interval}")
    public void schedule() {
        super.schedule();
    }

    public void update() {
        logger.info("Caching projects stats...");
        List<ProjectStats> projectsStats = new ArrayList<ProjectStats>();
        projectRepo.findAll().forEach(project -> {

            String id = project.getId().toString();
            String name = project.getName();
            int requestCount = 0;
            int issueCount = 0;
            int featureCount = 0;
            int defectCount = 0;

            // NOTE: if and when project can be associated to multiple remote projects, loop here

            Optional<RemoteProjectManager> remoteProjectManager = Optional.ofNullable(project.getRemoteProjectManager());
            Optional<String> scopeId = Optional.ofNullable(project.getScopeId());
            if (remoteProjectManager.isPresent() && scopeId.isPresent()) {
                Optional<RemoteProject> remoteProject = remoteProjectsScheduledCacheService.getRemoteProject(remoteProjectManager.get().getId(), scopeId.get());
                if (remoteProject.isPresent()) {
                    requestCount += remoteProject.get().getRequestCount();
                    issueCount += remoteProject.get().getIssueCount();
                    featureCount += remoteProject.get().getFeatureCount();
                    defectCount += remoteProject.get().getDefectCount();
                }
            }

            projectsStats.add(new ProjectStats(id, name, requestCount, issueCount, featureCount, defectCount));
        });
        set(projectsStats);
        logger.info("Finished caching projects stats");
    }

    public void broadcast() {
        logger.info("Broadcasting cached projects stats");
        simpMessagingTemplate.convertAndSend("/channel/projects/stats", new ApiResponse(SUCCESS, get()));
    }

}
