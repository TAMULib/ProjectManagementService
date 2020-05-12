package edu.tamu.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import edu.tamu.app.model.validation.SprintBlacklistValidator;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class SprintBlacklist extends ValidatingBaseEntity {
    
    @Column(nullable = false)
    private RemoteProductInfo remoteProductInfo;

    public SprintBlacklist() {
        super();
        this.modelValidator = new SprintBlacklistValidator();
    }

    public SprintBlacklist(RemoteProductInfo remoteProductInfo) {
        this.remoteProductInfo = remoteProductInfo;
    }

    public RemoteProductInfo getRemoteProductInfo() {
        return remoteProductInfo;
    }

    public void setRemoteProductInfo(RemoteProductInfo remoteProductInfo) {
        this.remoteProductInfo = remoteProductInfo;
    }
}
