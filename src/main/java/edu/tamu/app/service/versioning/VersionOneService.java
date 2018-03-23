package edu.tamu.app.service.versioning;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.request.ProjectRequest;
import edu.tamu.app.rest.BasicAuthRestTemplate;
import edu.tamu.app.service.TemplateService;

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
    public Object push(ProjectRequest request) {
        request.setId(getProjectScopeId(request.getProject()));
        return restTemplate.postForObject(craftDataRequestUrl(), templateService.templateRequest(request), Object.class);
    }

    private String getProjectScopeId(String projectName) {
        String scopeQueryUrl = craftScopeQueryUrl(projectName);
        JsonNode scope = (JsonNode) restTemplate.getForObject(scopeQueryUrl, ObjectNode.class);
        Optional<String> id = getId(scope);
        if (id.isPresent()) {
            return id.get().replace("Scope:", "");
        }
        throw new RuntimeException("Unable to fetch scope id for project " + projectName);
    }

    private String craftScopeQueryUrl(String projectName) {
        return getUrl() + "/rest-1.v1/Data/Scope?Accept=application/json&sel=Name&find=" + projectName + "&findin=Name";
    }

    private String craftDataRequestUrl() {
        return getUrl() + "/rest-1.v1/Data/Request";
    }

    private Optional<String> getId(JsonNode scope) {
        Optional<String> id = Optional.empty();
        Optional<JsonNode> assets = Optional.ofNullable(scope.get("Assets"));
        if (assets.isPresent() && assets.get().isArray()) {
            id = Optional.ofNullable(assets.get().get(0).get("id").asText());
        }
        return id;
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
        return hasSettingValues(key) ? managementService.getSettingValues(key).get(0) : "";
    }

    private boolean hasSettingValues(String key) {
        return managementService.getSettingValues(key) != null && managementService.getSettingValues(key).size() > 0;
    }

}
