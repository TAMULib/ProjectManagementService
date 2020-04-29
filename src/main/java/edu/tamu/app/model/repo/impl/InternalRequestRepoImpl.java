package edu.tamu.app.model.repo.impl;

import edu.tamu.app.model.InternalRequest;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.custom.InternalRequestRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class InternalRequestRepoImpl extends AbstractWeaverRepoImpl<InternalRequest, InternalRequestRepo> implements InternalRequestRepoCustom {

    @Override
    protected String getChannel() {
        return "/channel/internal/request";
    }

}
