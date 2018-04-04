package edu.tamu.app.model.request;

public class FeatureRequest extends AbstractRequest {

    private static final long serialVersionUID = -7150986466522854974L;

    private Long projectId;

    private String scopeId;

    public FeatureRequest() {
        super();
    }

    public FeatureRequest(String title, String description, Long projectId) {
        super(title, description);
        this.projectId = projectId;
    }

    public FeatureRequest(String title, String description, Long projectId, String scopeId) {
        this(title, description, projectId);
        this.scopeId = scopeId;
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
