package edu.tamu.app.cache.service;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.tamu.app.cache.ActiveSprintsCache;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.weaver.response.ApiResponse;

@Service
public class ActiveSprintsScheduledCacheService extends AbstractScheduledCacheService<List<Sprint>, ActiveSprintsCache> {

    private static final Logger logger = Logger.getLogger(ActiveSprintsScheduledCacheService.class);

    @Autowired
    private ProjectRepo projectRepo;

    public ActiveSprintsScheduledCacheService() {
        super(new ActiveSprintsCache());
    }

    @Override
    @Scheduled(initialDelayString = "${app.cache.active-sprints.delay}", fixedDelayString = "${app.cache.active-sprints.interval}")
    public void schedule() {
        super.schedule();
    }

    public void update() {
        logger.info("Caching active sprints...");
        List<Sprint> activeSprints = new ArrayList<Sprint>();
        projectRepo.findAll().forEach(project -> {
            Optional<RemoteProjectManager> remoteProjectManager = Optional.ofNullable(project.getRemoteProjectManager());
            if (remoteProjectManager.isPresent()) {
                RemoteProjectManagerBean remoteProjectManagerBean = (RemoteProjectManagerBean) managementBeanRegistry.getService(remoteProjectManager.get().getName());
                try {
                    activeSprints.addAll(remoteProjectManagerBean.getActiveSprintsByProjectId(project.getScopeId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        set(activeSprints);
        logger.info("Finished caching active sprints");
    }

    public void broadcast() {
        logger.info("Broadcasting cached active sprints");
        simpMessagingTemplate.convertAndSend("/channel/active-sprints", new ApiResponse(SUCCESS, get()));
    }

}
