package edu.tamu.app.model;

import java.util.Map;

import javax.persistence.Entity;

@Entity
public class RemoteProductManager extends ManagementService {

    public RemoteProductManager() {
        super();
    }

    public RemoteProductManager(String name, ServiceType type) {
        super(name, type);
    }

    public RemoteProductManager(String name, ServiceType type, Map<String, String> settings) {
        super(name, type, settings);
    }

}
