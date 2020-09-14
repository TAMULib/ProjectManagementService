package edu.tamu.app.model;

import javax.persistence.Entity;

@Entity
public class RemoteProjectManager extends ManagementService {

    public RemoteProjectManager() {
        super();
    }

    public RemoteProjectManager(String name, ServiceType type, String url, String token) {
        super(name, type, url, token);
    }

}
