package edu.tamu.app.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tamu.app.cache.model.RemoteProduct;

public class RemoteProductsCache extends AbstractCache<Map<Long, List<RemoteProduct>>> {

    public RemoteProductsCache() {
        set(new HashMap<Long, List<RemoteProduct>>());
    }

}
