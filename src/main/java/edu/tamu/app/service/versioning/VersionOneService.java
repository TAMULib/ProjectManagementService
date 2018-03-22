package edu.tamu.app.service.versioning;

import java.util.Base64;

import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.request.ProjectRequest;

public class VersionOneService implements VersionManagementSoftwareBean {

    private ManagementService managementService;

    public VersionOneService(ManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public Object push(ProjectRequest request) {
        return null;
    }

    public String getUrl() {
        return getSettingValue("url");
    }

    public String getAuth() {
        String username = getSettingValue("username");
        String password = getSettingValue("password");
        String auth = username + ":" + password;
        return Base64.getEncoder().encodeToString(auth.getBytes());
    }

    private String getSettingValue(String key) {
        return hasSettingValues(key) ? managementService.getSettingValues(key).get(0) : "";
    }

    private boolean hasSettingValues(String key) {
        return managementService.getSettingValues(key) != null && managementService.getSettingValues(key).size() > 0;
    }

}
