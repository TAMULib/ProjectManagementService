package edu.tamu.app.cache.service;

import edu.tamu.app.cache.Cache;
import edu.tamu.app.model.Project;

public interface ProjectScheduledCache<T, C extends Cache<T>> extends ScheduledCache<T, C> {

    public void addProject(Project project);

    public void updateProject(Project project);

    public void removeProject(Project project);

}
