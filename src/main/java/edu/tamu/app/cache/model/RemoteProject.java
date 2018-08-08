package edu.tamu.app.cache.model;

import java.io.Serializable;

public class RemoteProject implements Serializable {

    private static final long serialVersionUID = 8384046327331854613L;

    private final String scopeId;

    private final String name;

    private final int requestCount;

    private final int issueCount;

    private final int storyCount;

    private final int defectCount;

    public RemoteProject() {
        super();
        scopeId = "";
        name = "";
        requestCount = 0;
        issueCount = 0;
        storyCount = 0;
        defectCount = 0;
    }

    public RemoteProject(String scopeId, String name, int requestCount, int issueCount, int storyCount, int defectCount) {
        super();
        this.scopeId = scopeId;
        this.name = name;
        this.requestCount = requestCount;
        this.issueCount = issueCount;
        this.storyCount = storyCount;
        this.defectCount = defectCount;
    }

    public String getScopeId() {
        return scopeId;
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

    public int getStoryCount() {
        return storyCount;
    }

    public int getDefectCount() {
        return defectCount;
    }

    public int getBacklogItemCount() {
        return storyCount + defectCount;
    }

}
