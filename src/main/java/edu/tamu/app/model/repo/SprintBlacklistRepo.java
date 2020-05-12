package edu.tamu.app.model.repo;

import edu.tamu.app.model.SprintBlacklist;
import edu.tamu.app.model.repo.custom.SprintBlacklistRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface SprintBlacklistRepo extends WeaverRepo<SprintBlacklist>, SprintBlacklistRepoCustom {

}
