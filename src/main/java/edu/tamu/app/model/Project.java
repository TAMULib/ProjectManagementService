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

import edu.tamu.app.model.validation.ProjectValidator;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class Project extends ValidatingBaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @JsonInclude(Include.NON_NULL)
    @Column(nullable = true)
    private String scopeId;

    @JsonInclude(Include.NON_NULL)
    @ManyToOne(fetch = EAGER, cascade = { DETACH, REFRESH, MERGE }, optional = true)
    private VersionManagementSoftware versionManagementSoftware;

    public Project() {
        this.modelValidator = new ProjectValidator();
    }

    public Project(String name) {
        this();
        this.name = name;
    }

    public Project(String name, VersionManagementSoftware versionManagementSoftware) {
        this(name);
        this.versionManagementSoftware = versionManagementSoftware;
    }

    public Project(String name, String scopeId, VersionManagementSoftware versionManagementSoftware) {
        this(name, versionManagementSoftware);
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

    public VersionManagementSoftware getVersionManagementSoftware() {
        return versionManagementSoftware;
    }

    public void setVersionManagementSoftware(VersionManagementSoftware versionManagementSoftware) {
        this.versionManagementSoftware = versionManagementSoftware;
    }

}
