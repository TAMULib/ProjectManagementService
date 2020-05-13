package edu.tamu.app.model;

import java.io.Serializable;

public class InternalStats implements Serializable {

    private static final long serialVersionUID = -1622544796949909087L;

    private final long internalCount;

    public InternalStats() {
        super();
        internalCount = 0;
    }

    public InternalStats(long internalCount) {
        super();
        this.internalCount = internalCount;
    }

    public long getInternalCount() {
        return internalCount;
    }

}
