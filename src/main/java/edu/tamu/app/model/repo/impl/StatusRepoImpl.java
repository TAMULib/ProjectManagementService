package edu.tamu.app.model.repo.impl;

import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.StatusRepo;
import edu.tamu.app.model.repo.custom.StatusRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class StatusRepoImpl extends AbstractWeaverRepoImpl<Status, StatusRepo> implements StatusRepoCustom {

    @Override
    protected String getChannel() {
        return "/channel/status";
    }

}
