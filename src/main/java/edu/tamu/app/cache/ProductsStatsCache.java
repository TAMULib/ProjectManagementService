package edu.tamu.app.cache;

import java.util.ArrayList;
import java.util.List;

import edu.tamu.app.cache.model.ProjectStats;

public class ProjectsStatsCache extends AbstractCache<List<ProjectStats>> {

    public ProjectsStatsCache() {
        set(new ArrayList<ProjectStats>());
    }

}
