package edu.tamu.app.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import edu.tamu.app.model.validation.ProjectValidator;
import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@Entity
public class Project extends ValidatingBaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { DETACH, REFRESH, MERGE, PERSIST }, optional = true)
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VersionManagementSoftware getVersionManagementSoftware() {
        return versionManagementSoftware;
    }

    public void setVersionManagementSoftware(VersionManagementSoftware versionManagementSoftware) {
        this.versionManagementSoftware = versionManagementSoftware;
    }

}
