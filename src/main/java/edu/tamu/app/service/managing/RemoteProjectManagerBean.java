package edu.tamu.app.service.managing;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.response.RemoteProject;
import edu.tamu.app.service.registry.ManagementBean;

public interface RemoteProjectManagerBean extends ManagementBean {

    public List<RemoteProject> getRemoteProjects();

    public RemoteProject getRemoteProjectByScopeId(String scopeId);

    public JsonNode push(FeatureRequest request);

}
