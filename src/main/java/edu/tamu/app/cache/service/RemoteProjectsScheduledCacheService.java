package edu.tamu.app.cache.service;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.tamu.app.cache.RemoteProjectsCache;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.service.manager.RemoteProjectManagerBean;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.response.ApiResponse;

@Service
public class RemoteProjectsScheduledCacheService extends AbstractScheduledCacheService<Map<Long, List<RemoteProject>>, RemoteProjectsCache> {

    private static final Logger logger = Logger.getLogger(RemoteProjectsScheduledCacheService.class);

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    public RemoteProjectsScheduledCacheService() {
        super(new RemoteProjectsCache());
    }

    @Override
    @Scheduled(initialDelayString = "${app.cache.remote-projects.delay}", fixedDelayString = "${app.cache.remote-projects.interval}")
    public void schedule() {
        super.schedule();
    }

    public void update() {
        logger.info("Caching remote projects...");
        Map<Long, List<RemoteProject>> remoteProjects = new HashMap<Long, List<RemoteProject>>();
        for (RemoteProjectManager remoteProjectManager : remoteProjectManagerRepo.findAll()) {
            RemoteProjectManagerBean remoteProjectManagerBean = (RemoteProjectManagerBean) managementBeanRegistry.getService(remoteProjectManager.getName());
            try {
                remoteProjects.put(remoteProjectManager.getId(), remoteProjectManagerBean.getRemoteProjects());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        set(remoteProjects);
        logger.info("Finished caching remote projects");
    }

    public void broadcast() {
        logger.info("Broadcasting cached remote projects");
        simpMessagingTemplate.convertAndSend("/channel/projects/remote", new ApiResponse(SUCCESS, get()));
    }

    public Optional<RemoteProject> getRemoteProject(Long remoteProjectManagerId, String scopeId) {
        Optional<RemoteProject> remoteProject = Optional.empty();
        Optional<List<RemoteProject>> remoteProjects = Optional.ofNullable(get().get(remoteProjectManagerId));
        if (remoteProjects.isPresent()) {
            for (RemoteProject rp : remoteProjects.get()) {
                if (rp.getId().equals(scopeId)) {
                    remoteProject = Optional.of(rp);
                    break;
                }
            }
        }
        return remoteProject;
    }

}
