package edu.tamu.app.model.request;

public class FeatureRequest extends AbstractRequest {

    private static final long serialVersionUID = -7150986466522854974L;

    private Long productId;

    private String scopeId;

    public FeatureRequest() {
        super();
    }

    public FeatureRequest(String title, String description, Long productId, String scopeId) {
        super(title, description);
        this.productId = productId;
        this.scopeId = scopeId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

}
