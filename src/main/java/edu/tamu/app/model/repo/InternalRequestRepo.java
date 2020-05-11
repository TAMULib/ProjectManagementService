package edu.tamu.app.model.repo;

import edu.tamu.app.model.InternalRequest;
import edu.tamu.app.model.repo.custom.InternalRequestRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface InternalRequestRepo extends WeaverRepo<InternalRequest>, InternalRequestRepoCustom {

}
