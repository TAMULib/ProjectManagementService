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
public class RemoteProjectInfo {

    @JsonView(ApiView.Partial.class)
    private String scopeId;

    @JsonView(ApiView.Partial.class)
    @ManyToOne(targetEntity = RemoteProjectManager.class, fetch = EAGER, cascade = { DETACH, REFRESH, MERGE }, optional = true)
    private RemoteProjectManager remoteProjectManager;

    public RemoteProjectInfo() {}

    public RemoteProjectInfo(String scopeId, RemoteProjectManager remoteProjectManager) {
        this.scopeId = scopeId;
        this.remoteProjectManager = remoteProjectManager;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public RemoteProjectManager getRemoteProjectManager() {
        return remoteProjectManager;
    }

    public void setRemoteProjectManager(RemoteProjectManager remoteProjectManager) {
        this.remoteProjectManager = remoteProjectManager;
    }
}