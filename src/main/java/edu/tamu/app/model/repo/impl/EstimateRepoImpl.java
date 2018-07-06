package edu.tamu.app.model.repo.impl;

import edu.tamu.app.model.Estimate;
import edu.tamu.app.model.repo.EstimateRepo;
import edu.tamu.app.model.repo.custom.EstimateRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class EstimateRepoImpl extends AbstractWeaverRepoImpl<Estimate, EstimateRepo> implements EstimateRepoCustom {

    @Override
    protected String getChannel() {
        return "/channel/estimate";
    }

}
