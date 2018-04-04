package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.VersionManagementSoftwareRepo;
import edu.tamu.app.model.repo.custom.VersionManagementSoftwareRepoCustom;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class VersionManagementSoftwareRepoImpl extends AbstractWeaverRepoImpl<VersionManagementSoftware, VersionManagementSoftwareRepo> implements VersionManagementSoftwareRepoCustom {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Override
    public VersionManagementSoftware create(VersionManagementSoftware versionManagementSoftware) {
        versionManagementSoftware = super.create(versionManagementSoftware);
        managementBeanRegistry.register(versionManagementSoftware);
        return versionManagementSoftware;
    }

    @Override
    protected String getChannel() {
        return "/channel/version-management-software";
    }

}
