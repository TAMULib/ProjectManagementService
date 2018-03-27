package edu.tamu.app.model;

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

    public VersionManagementSoftware(String name, ServiceType type, Map<String, String> settings) {
        super(name, type, settings);
    }

}
