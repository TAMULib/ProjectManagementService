package edu.tamu.app.model.repo.impl;

import edu.tamu.app.model.SprintBlacklist;
import edu.tamu.app.model.repo.SprintBlacklistRepo;
import edu.tamu.app.model.repo.custom.SprintBlacklistRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class SprintBlacklistRepoImpl extends AbstractWeaverRepoImpl<SprintBlacklist, SprintBlacklistRepo> implements SprintBlacklistRepoCustom {
    
    @Override
    protected String getChannel() {
        return "/channel/sprint-blacklist";
    }
}