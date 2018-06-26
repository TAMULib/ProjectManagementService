package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.repo.custom.RemoteProjectManagerRepoCustom;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class RemoteProjectManagerRepoImpl extends AbstractWeaverRepoImpl<RemoteProjectManager, RemoteProjectManagerRepo> implements RemoteProjectManagerRepoCustom {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Override
    public RemoteProjectManager create(RemoteProjectManager remoteProjectManager) {
        remoteProjectManager = super.create(remoteProjectManager);
        managementBeanRegistry.register(remoteProjectManager);
        return remoteProjectManager;
    }

    @Override
    protected String getChannel() {
        return "/channel/remote-project-manager";
    }

}
