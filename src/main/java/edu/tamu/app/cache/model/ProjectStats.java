package edu.tamu.app.cache.model;

import java.io.Serializable;

public class ProjectStats implements Serializable {

    private static final long serialVersionUID = -1622544796949909087L;

    private final String id;

    private final String name;

    private final int requestCount;

    private final int issueCount;

    private final int featureCount;

    private final int defectCount;

    public ProjectStats() {
        super();
        id = "";
        name = "";
        requestCount = 0;
        issueCount = 0;
        featureCount = 0;
        defectCount = 0;
    }

    public ProjectStats(String id, String name, int requestCount, int issueCount, int featureCount, int defectCount) {
        super();
        this.id = id;
        this.name = name;
        this.requestCount = requestCount;
        this.issueCount = issueCount;
        this.featureCount = featureCount;
        this.defectCount = defectCount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public int getFeatureCount() {
        return featureCount;
    }

    public int getDefectCount() {
        return defectCount;
    }

    public int getBacklogItemCount() {
        return featureCount + defectCount;
    }

}
