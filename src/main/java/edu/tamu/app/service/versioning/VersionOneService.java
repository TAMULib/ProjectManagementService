package edu.tamu.app.service.versioning;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.request.ProjectRequest;
import edu.tamu.app.model.response.VersionProject;
import edu.tamu.app.rest.BasicAuthRestTemplate;
import edu.tamu.app.service.TemplateService;
import edu.tamu.app.utility.JsonNodeUtility;

public class VersionOneService implements VersionManagementSoftwareBean {

    private ManagementService managementService;

    private BasicAuthRestTemplate restTemplate;

    @Autowired
    private TemplateService templateService;

    public VersionOneService(ManagementService managementService) {
        this.managementService = managementService;
        this.restTemplate = new BasicAuthRestTemplate(getUsername(), getPassword());
    }

    @Override
    public List<VersionProject> getVersionProjects() {
        JsonNode response = restTemplate.getForObject(craftProjectsQueryUrl(), JsonNode.class);
        return JsonNodeUtility.getVersionProjects(response.get("Assets"));
    }

    @Override
    public VersionProject getVersionProjectByScopeId(String scopeId) {
        JsonNode asset = restTemplate.getForObject(craftProjectByScopeIdQueryUrl(scopeId), JsonNode.class);
        String name = JsonNodeUtility.getVersionProjectName(asset);
        return new VersionProject(name, scopeId);
    }

    @Override
    public JsonNode push(ProjectRequest request) {
        return restTemplate.postForObject(craftDataRequestUrl(), templateService.craftVersionOneXmlRequestBody(request), JsonNode.class);
    }

    private String craftProjectsQueryUrl() {
        return getUrl() + "/rest-1.v1/Data/Scope?Accept=application/json&sel=Name";
    }

    private String craftProjectByScopeIdQueryUrl(String scopeId) {
        return getUrl() + "/rest-1.v1/Data/Scope/" + scopeId + "?Accept=application/json&sel=Name";
    }

    private String craftDataRequestUrl() {
        return getUrl() + "/rest-1.v1/Data/Request";
    }

    private String getUrl() {
        return getSettingValue("url");
    }

    private String getUsername() {
        return getSettingValue("username");
    }

    private String getPassword() {
        return getSettingValue("password");
    }

    private String getSettingValue(String key) {
        Optional<String> setting = managementService.getSettingValue(key);
        if (setting.isPresent()) {
            return setting.get();
        }
        throw new RuntimeException("No setting " + key + " found in settings for service " + managementService.getName());
    }

}
