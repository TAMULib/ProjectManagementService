package edu.tamu.app.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tamu.app.cache.model.RemoteProject;

public class RemoteProjectsCache extends AbstractCache<Map<Long, List<RemoteProject>>> {

    public RemoteProjectsCache() {
        set(new HashMap<Long, List<RemoteProject>>());
    }

}
