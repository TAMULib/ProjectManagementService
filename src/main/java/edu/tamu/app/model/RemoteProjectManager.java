package edu.tamu.app.model;

import java.util.Map;

import javax.persistence.Entity;

import edu.tamu.app.enums.ServiceType;

@Entity
public class RemoteProjectManager extends ManagementService {

    public RemoteProjectManager() {
        super();
    }

    public RemoteProjectManager(String name, ServiceType type) {
        super(name, type);
    }

    public RemoteProjectManager(String name, ServiceType type, Map<String, String> settings) {
        super(name, type, settings);
    }

}
