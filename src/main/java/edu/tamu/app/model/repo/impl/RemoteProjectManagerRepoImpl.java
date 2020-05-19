package edu.tamu.app.model.repo.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.cache.service.ScheduledCache;
import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.repo.RemoteProductManagerRepo;
import edu.tamu.app.model.repo.custom.RemoteProductManagerRepoCustom;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class RemoteProductManagerRepoImpl extends AbstractWeaverRepoImpl<RemoteProductManager, RemoteProductManagerRepo> implements RemoteProductManagerRepoCustom {

    private static final Logger logger = Logger.getLogger(RemoteProductManagerRepoImpl.class);

    @Autowired
    private RemoteProductManagerRepo remoteProductManagerRepo;

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private List<ScheduledCache<?, ?>> scheduledCaches;

    @Override
    public RemoteProductManager create(RemoteProductManager remoteProductManager) {
        remoteProductManager = super.create(remoteProductManager);
        managementBeanRegistry.register(remoteProductManager);
        updateCache();
        return remoteProductManager;
    }

    @Override
    public RemoteProductManager update(RemoteProductManager remoteProductManager) {
        RemoteProductManager existingRemoteProductManager = remoteProductManagerRepo.findOne(remoteProductManager.getId());
        managementBeanRegistry.unregister(existingRemoteProductManager);
        remoteProductManager = super.update(remoteProductManager);
        managementBeanRegistry.register(remoteProductManager);
        updateCache();
        return remoteProductManager;
    }

    @Override
    public void delete(RemoteProductManager remoteProductManager) {
        managementBeanRegistry.unregister(remoteProductManager);
        super.delete(remoteProductManager);
        updateCache();
    }

    @Override
    protected String getChannel() {
        return "/channel/remote-product-manager";
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
