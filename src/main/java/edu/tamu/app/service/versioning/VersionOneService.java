package edu.tamu.app.service.versioning;

import java.util.Optional;

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
        return restTemplate.postForObject(craftDataRequestUrl(), templateService.craftVersionOneXmlRequestBody(request), Object.class);
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
