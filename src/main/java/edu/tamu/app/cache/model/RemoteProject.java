package edu.tamu.app.cache.model;

import java.io.Serializable;

public class RemoteProject implements Serializable {

    private static final long serialVersionUID = 8384046327331854613L;

    private final String scopeId;

    private final String name;

    public RemoteProject() {
        super();
        this.scopeId = "";
        this.name = "";
    }

    public RemoteProject(String scopeId, String name) {
        super();
        this.scopeId = scopeId;
        this.name = name;

    }

    public String getScopeId() {
        return scopeId;
    }

    public String getName() {
        return name;
    }

}
