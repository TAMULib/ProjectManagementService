package edu.tamu.app.model.repo.impl;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.ProjectRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ProjectRepoImpl extends AbstractWeaverRepoImpl<Project, ProjectRepo> implements ProjectRepoCustom {

    @Override
    protected String getChannel() {
        return "/channel/projects";
    }

}
