package edu.tamu.app.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.weaver.response.ApiView;

@Embeddable
public class RemoteProductInfo {

    @JsonView(ApiView.Partial.class)
    private String scopeId;

    @JsonView(ApiView.Partial.class)
    @ManyToOne(targetEntity = RemoteProductManager.class, fetch = EAGER, cascade = { DETACH, REFRESH, MERGE }, optional = true)
    private RemoteProductManager remoteProductManager;

    public RemoteProductInfo() {}

    public RemoteProductInfo(String scopeId, RemoteProductManager remoteProductManager) {
        this.scopeId = scopeId;
        this.remoteProductManager = remoteProductManager;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public RemoteProductManager getRemoteProductManager() {
        return remoteProductManager;
    }

    public void setRemoteProductManager(RemoteProductManager remoteProductManager) {
        this.remoteProductManager = remoteProductManager;
    }
}