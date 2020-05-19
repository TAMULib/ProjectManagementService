package edu.tamu.app.model.repo.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.cache.service.ScheduledCache;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.repo.custom.RemoteProjectManagerRepoCustom;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class RemoteProjectManagerRepoImpl extends AbstractWeaverRepoImpl<RemoteProjectManager, RemoteProjectManagerRepo> implements RemoteProjectManagerRepoCustom {

    private static final Logger logger = Logger.getLogger(RemoteProjectManagerRepoImpl.class);

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private List<ScheduledCache<?, ?>> scheduledCaches;

    @Override
    public RemoteProjectManager create(RemoteProjectManager remoteProjectManager) {
        remoteProjectManager = super.create(remoteProjectManager);
        managementBeanRegistry.register(remoteProjectManager);
        updateCache();
        return remoteProjectManager;
    }

    @Override
    public RemoteProjectManager update(RemoteProjectManager remoteProjectManager) {
        RemoteProjectManager existingRemoteProductManager = remoteProjectManagerRepo.findOne(remoteProjectManager.getId());
        managementBeanRegistry.unregister(existingRemoteProductManager);
        remoteProjectManager = super.update(remoteProjectManager);
        managementBeanRegistry.register(remoteProjectManager);
        updateCache();
        return remoteProjectManager;
    }

    @Override
    public void delete(RemoteProjectManager remoteProjectManager) {
        managementBeanRegistry.unregister(remoteProjectManager);
        super.delete(remoteProjectManager);
        updateCache();
    }

    @Override
    protected String getChannel() {
        return "/channel/remote-project-manager";
    }

    private void updateCache() {
        CompletableFuture.runAsync(() -> {
            for (ScheduledCache<?, ?> sceduledCache : scheduledCaches) {
                sceduledCache.schedule();
            }
        }).thenRun(() -> {
            logger.info("Finished asynchronous cache update and brodcast");
        });
    }

}
