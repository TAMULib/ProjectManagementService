package edu.tamu.app.service.versioning;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.model.Card;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.Sprint;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.response.VersionProject;
import edu.tamu.app.service.registry.ManagementBean;

public interface VersionManagementSoftwareBean extends ManagementBean {

    public List<VersionProject> getVersionProjects() throws Exception;

    public VersionProject getVersionProjectByScopeId(String scopeId) throws Exception;

    public FeatureRequest push(FeatureRequest request) throws Exception;

    public List<Sprint> getActiveSprintsByProject(Project project) throws Exception;

}
