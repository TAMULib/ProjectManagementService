package edu.tamu.app.model.repo.impl;

import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.VersionManagementSoftwareRepo;
import edu.tamu.app.model.repo.custom.VersionManagementSoftwareRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class VersionManagementSoftwareRepoImpl extends AbstractWeaverRepoImpl<VersionManagementSoftware, VersionManagementSoftwareRepo> implements VersionManagementSoftwareRepoCustom {

    @Override
    protected String getChannel() {
        return "/channel/version-management-software";
    }

}
