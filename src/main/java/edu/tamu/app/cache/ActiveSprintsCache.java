package edu.tamu.app.cache;

import java.util.ArrayList;
import java.util.List;

import edu.tamu.app.cache.model.Sprint;

public class ActiveSprintsCache extends AbstractCache<List<Sprint>> {

    public ActiveSprintsCache() {
        set(new ArrayList<Sprint>());
    }

}
