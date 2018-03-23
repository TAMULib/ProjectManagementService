package edu.tamu.app.model.request;

import java.io.Serializable;

public class ProjectRequest implements Serializable {

    private static final long serialVersionUID = -7150986466522854974L;

    private String title;

    private String description;

    private Long projectId;

    private String scopeId;

    public ProjectRequest() {
        super();
    }

    public ProjectRequest(String title, String description, Long projectId) {
        this();
        this.title = title;
        this.description = description;
        this.projectId = projectId;
    }

    public ProjectRequest(String title, String description, Long projectId, String scopeId) {
        this(title, description, projectId);
        this.scopeId = scopeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

}
