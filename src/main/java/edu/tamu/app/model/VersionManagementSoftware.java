package edu.tamu.app.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import edu.tamu.app.enums.ServiceType;

@Entity
public class VersionManagementSoftware extends ManagementService {

    public VersionManagementSoftware() {
        super();
    }

    public VersionManagementSoftware(String name, ServiceType type) {
        super(name, type);
    }

    public VersionManagementSoftware(String name, ServiceType type, List<ManagementSetting> settings) {
        super(name, type, settings);
    }

    public Map<String, Object> getSettingsScaffold() {
        Map<String, Object> scaffold = new HashMap<String, Object>();
        switch(type) {
        case VERSION_ONE:
            scaffold.put("url", "String");
            scaffold.put("username", "String");
            scaffold.put("password", "String");
            break;
        default:
            break;
        
        }
        return scaffold;
    }

}
