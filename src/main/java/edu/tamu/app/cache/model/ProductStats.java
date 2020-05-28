package edu.tamu.app.cache.model;

import java.io.Serializable;

public class ProductStats implements Serializable {

    private static final long serialVersionUID = -1622544796949909087L;

    private final String id;

    private final String name;

    private final long requestCount;

    private final long issueCount;

    private final long featureCount;

    private final long defectCount;

    private final long internalCount;

    public ProductStats() {
        super();
        id = "";
        name = "";
        requestCount = 0;
        issueCount = 0;
        featureCount = 0;
        defectCount = 0;
        internalCount = 0;
    }

    public ProductStats(String id, String name, long requestCount, long issueCount, long featureCount, long defectCount, long internalCount) {
        super();
        this.id = id;
        this.name = name;
        this.requestCount = requestCount;
        this.issueCount = issueCount;
        this.featureCount = featureCount;
        this.defectCount = defectCount;
        this.internalCount = internalCount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public long getIssueCount() {
        return issueCount;
    }

    public long getFeatureCount() {
        return featureCount;
    }

    public long getDefectCount() {
        return defectCount;
    }

    public long getInternalCount() {
        return internalCount;
    }

    public long getBacklogItemCount() {
        return featureCount + defectCount;
    }

}
