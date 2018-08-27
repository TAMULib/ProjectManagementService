package edu.tamu.app.cache.model;

public class RemoteProject extends ProjectStats {

    private static final long serialVersionUID = 8384046327331854613L;

    public RemoteProject() {
        super();
    }

    public RemoteProject(String id, String name, int requestCount, int issueCount, int featureCount, int defectCount) {
        super(id, name, requestCount, issueCount, featureCount, defectCount);
    }

}
