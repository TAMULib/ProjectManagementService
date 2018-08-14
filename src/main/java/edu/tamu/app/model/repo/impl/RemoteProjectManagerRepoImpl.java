package edu.tamu.app.model.repo.impl;

import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.repo.custom.RemoteProjectManagerRepoCustom;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class RemoteProjectManagerRepoImpl extends AbstractWeaverRepoImpl<RemoteProjectManager, RemoteProjectManagerRepo> implements RemoteProjectManagerRepoCustom {

    private static final Logger logger = Logger.getLogger(RemoteProjectManagerRepoImpl.class);

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @Override
    public RemoteProjectManager create(RemoteProjectManager remoteProjectManager) {
        remoteProjectManager = super.create(remoteProjectManager);
        managementBeanRegistry.register(remoteProjectManager);
        CompletableFuture.runAsync(() -> {
            remoteProjectsScheduledCacheService.schedule();
        }).thenRun(() -> {
            logger.info("Finished asynchronous cache update and brodcast of remote projects");
        });
        return remoteProjectManager;
    }

    @Override
    protected String getChannel() {
        return "/channel/remote-project-manager";
    }

}
