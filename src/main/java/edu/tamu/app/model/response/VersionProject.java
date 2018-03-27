package edu.tamu.app.model.response;

import java.io.Serializable;

public class VersionProject implements Serializable {

    private static final long serialVersionUID = 8384046327331854613L;

    private final String name;

    private final String scopeId;

    public VersionProject(String name, String scopeId) {
        super();
        this.name = name;
        this.scopeId = scopeId;
    }

    public String getName() {
        return name;
    }

    public String getScopeId() {
        return scopeId;
    }

}
