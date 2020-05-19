package edu.tamu.app.model.repo;

import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.repo.custom.RemoteProjectManagerRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface RemoteProjectManagerRepo extends WeaverRepo<RemoteProjectManager>, RemoteProjectManagerRepoCustom {

}
