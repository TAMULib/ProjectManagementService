package edu.tamu.app.service.versioning;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.model.request.ProjectRequest;
import edu.tamu.app.service.registry.ManagementBean;

public interface VersionManagementSoftwareBean extends ManagementBean {

    public List<JsonNode> getProjects();

    public JsonNode push(ProjectRequest request);

}
