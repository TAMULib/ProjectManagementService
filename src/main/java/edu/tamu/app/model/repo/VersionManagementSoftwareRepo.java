package edu.tamu.app.model.repo;

import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.custom.VersionManagementSoftwareRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface VersionManagementSoftwareRepo extends WeaverRepo<VersionManagementSoftware>, VersionManagementSoftwareRepoCustom {

}
