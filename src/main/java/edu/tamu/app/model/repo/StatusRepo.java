package edu.tamu.app.model.repo;

import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.custom.StatusRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface StatusRepo extends WeaverRepo<Status>, ServiceMappingRepo<String, Status>, StatusRepoCustom {

}
