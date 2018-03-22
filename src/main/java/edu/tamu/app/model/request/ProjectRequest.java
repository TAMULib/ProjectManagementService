package edu.tamu.app.model.request;

import java.io.Serializable;

import edu.tamu.app.model.Project;

public class ProjectRequest implements Serializable {

    private static final long serialVersionUID = -7150986466522854974L;

    private Project project;

    private String title;

    private String description;

    public ProjectRequest() {
        super();
    }

    public ProjectRequest(Project project, String title, String description) {
        this();
        this.project = project;
        this.title = title;
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
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

}
