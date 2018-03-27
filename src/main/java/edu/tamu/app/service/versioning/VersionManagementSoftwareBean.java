package edu.tamu.app.service.versioning;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.response.VersionProject;
import edu.tamu.app.service.registry.ManagementBean;

public interface VersionManagementSoftwareBean extends ManagementBean {

    public List<VersionProject> getVersionProjects();

    public VersionProject getVersionProjectByScopeId(String scopeId);

    public JsonNode push(FeatureRequest request);

}
