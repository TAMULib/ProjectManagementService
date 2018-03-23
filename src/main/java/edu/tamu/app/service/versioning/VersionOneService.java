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
        String scopeQueryUrl = getUrl() + "/rest-1.v1/Data/Scope?Accept=application/json&sel=Name&find=" + request.getProject().getName() + "&findin=Name";

        System.out.println("\n\n" + scopeQueryUrl + "\n\n");

        JsonNode scope = (JsonNode) restTemplate.getForObject(scopeQueryUrl, ObjectNode.class);

        System.out.println("\n\n" + scope + "\n\n");

        Optional<String> id = getId(scope);

        if (id.isPresent()) {

            request.setId(id.get().replace("Scope:", ""));
            
            System.out.println("\n\n" + templateService.templateRequest(request) + "\n\n");

            String requestUrl = getUrl() + "/rest-1.v1/Data/Request";
            
            System.out.println("\n\n" + requestUrl + "\n\n");
            
            return restTemplate.postForObject(requestUrl, templateService.templateRequest(request), Object.class);

        }
        throw new RuntimeException("Unable to fetch scope for project " + request.getProject().getName());
    }

    public String getUrl() {
        return getSettingValue("url");
    }

    public String getUsername() {
        return getSettingValue("username");
    }

    public String getPassword() {
        return getSettingValue("password");
    }

    private Optional<String> getId(JsonNode scope) {
        Optional<String> id = Optional.empty();
        Optional<JsonNode> assets = Optional.ofNullable(scope.get("Assets"));
        if (assets.isPresent() && assets.get().isArray()) {
            id = Optional.ofNullable(assets.get().get(0).get("id").asText());
        }
        return id;
    }
    
    private String getSettingValue(String key) {
        return hasSettingValues(key) ? managementService.getSettingValues(key).get(0) : "";
    }

    private boolean hasSettingValues(String key) {
        return managementService.getSettingValues(key) != null && managementService.getSettingValues(key).size() > 0;
    }

}
