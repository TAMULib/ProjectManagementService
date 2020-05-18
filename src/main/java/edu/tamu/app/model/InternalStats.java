package edu.tamu.app.model;

import java.io.Serializable;

public class InternalStats implements Serializable {

    private static final long serialVersionUID = -1622544796949909087L;

    private final long unassignedCount;
    private final long totalCount;

    public InternalStats() {
        super();
        unassignedCount = 0;
        totalCount = 0;
    }

    public InternalStats(long unassignedCount, long totalCount) {
        super();
        this.unassignedCount = unassignedCount;
        this.totalCount = totalCount;
    }

    public long getAssignedCount() {
        return totalCount - unassignedCount;
    }

    public long getUnassignedCount() {
        return unassignedCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

}
