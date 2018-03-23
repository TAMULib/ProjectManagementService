package edu.tamu.app.service.versioning;

import org.springframework.beans.factory.annotation.Autowired;

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
        return restTemplate.postForObject(craftDataRequestUrl(), templateService.templateRequest(request), Object.class);
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
        return hasSettingValues(key) ? managementService.getSettingValues(key).get(0) : "";
    }

    private boolean hasSettingValues(String key) {
        return managementService.getSettingValues(key) != null && managementService.getSettingValues(key).size() > 0;
    }

}
