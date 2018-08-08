package edu.tamu.app.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

import edu.tamu.app.model.validation.ProjectValidator;
import edu.tamu.weaver.response.ApiView;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class Project extends ValidatingBaseEntity {

    @Column(unique = true, nullable = false)
    @JsonView(ApiView.Partial.class)
    private String name;

    @JsonInclude(Include.NON_NULL)
    @Column(nullable = true)
    @JsonView(ApiView.Partial.class)
    private String scopeId;

    @JsonInclude(Include.NON_NULL)
    @ManyToOne(fetch = EAGER, cascade = { DETACH, REFRESH, MERGE }, optional = true)
    @JsonView(ApiView.Partial.class)
    private RemoteProjectManager remoteProjectManager;

    public Project() {
        this.modelValidator = new ProjectValidator();
    }

    public Project(String name) {
        this();
        this.name = name;
    }

    public Project(String name, RemoteProjectManager remoteProjectManager) {
        this(name);
        this.remoteProjectManager = remoteProjectManager;
    }

    public Project(String name, String scopeId, RemoteProjectManager remoteProjectManager) {
        this(name, remoteProjectManager);
        this.scopeId = scopeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
