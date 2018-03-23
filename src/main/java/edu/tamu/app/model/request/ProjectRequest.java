package edu.tamu.app.model.request;

import java.io.Serializable;

public class ProjectRequest implements Serializable {

    private static final long serialVersionUID = -7150986466522854974L;

    private String title;

    private String description;

    private String project;

    private String id;

    public ProjectRequest() {
        super();
    }

    public ProjectRequest(String title, String description, String project) {
        this();
        this.title = title;
        this.description = description;
        this.project = project;
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

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
