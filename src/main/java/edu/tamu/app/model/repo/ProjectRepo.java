package edu.tamu.app.model.repo;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.custom.ProjectRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

public interface ProjectRepo extends WeaverRepo<Project>, ProjectRepoCustom {

}
