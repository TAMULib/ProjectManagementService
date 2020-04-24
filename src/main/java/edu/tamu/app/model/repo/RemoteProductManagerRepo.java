package edu.tamu.app.model.repo;

import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.repo.custom.RemoteProductManagerRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface RemoteProductManagerRepo extends WeaverRepo<RemoteProductManager>, RemoteProductManagerRepoCustom {

}
