package edu.tamu.app.service.manager;

import java.util.List;

import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.service.registry.ManagementBean;

public interface RemoteProductManagerBean extends ManagementBean {

    public List<RemoteProduct> getRemoteProduct() throws Exception;

    public RemoteProduct getRemoteProductByScopeId(final String scopeId) throws Exception;

    public List<Sprint> getActiveSprintsByProductId(final String productScopeId) throws Exception;

    public List<Sprint> getAdditionalActiveSprints() throws Exception;

    public Object push(final FeatureRequest request) throws Exception;

}
