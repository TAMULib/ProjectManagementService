package edu.tamu.app.service.versioning;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.request.ProjectRequest;
import edu.tamu.app.rest.BasicAuthRestTemplate;
import edu.tamu.app.service.TemplateService;

public class VersionOneService implements VersionManagementSoftwareBean {

    private ManagementService managementService;

    private BasicAuthRestTemplate restTemplate;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private ObjectMapper objectMapper;

    public VersionOneService(ManagementService managementService) {
        this.managementService = managementService;
        this.restTemplate = new BasicAuthRestTemplate(getUsername(), getPassword());
    }

    @Override
    public List<JsonNode> getProjects() {
        JsonNode response = restTemplate.getForObject(craftProjectQueryUrl(), JsonNode.class);
        return objectMapper.convertValue(response.get("Assets"), new TypeReference<List<JsonNode>>() {});
    }

    @Override
    public JsonNode push(ProjectRequest request) {
        return restTemplate.postForObject(craftDataRequestUrl(), templateService.craftVersionOneXmlRequestBody(request), JsonNode.class);
    }

    private String craftProjectQueryUrl() {
        return getUrl() + "/rest-1.v1/Data/Scope?Accept=application/json&sel=Name";
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
