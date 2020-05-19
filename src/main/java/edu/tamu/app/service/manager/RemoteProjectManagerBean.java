package edu.tamu.app.service.manager;

import java.util.List;

import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.service.registry.ManagementBean;

public interface RemoteProjectManagerBean extends ManagementBean {

    public List<RemoteProject> getRemoteProject() throws Exception;

    public RemoteProject getRemoteProjectByScopeId(final String scopeId) throws Exception;

    public List<Sprint> getActiveSprintsByScopeId(final String scopeId) throws Exception;

    public List<Sprint> getAdditionalActiveSprints() throws Exception;

    public Object push(final FeatureRequest request) throws Exception;

}
