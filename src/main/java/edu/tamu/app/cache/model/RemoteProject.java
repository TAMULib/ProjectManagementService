package edu.tamu.app.cache.model;

public class RemoteProject extends ProductStats {

    private static final long serialVersionUID = 8384046327331854613L;

    public RemoteProject() {
        super();
    }

    public RemoteProject(String id, String name, long requestCount, long issueCount, long featureCount, long defectCount, long internalCount) {
        super(id, name, requestCount, issueCount, featureCount, defectCount, internalCount);
    }

}
