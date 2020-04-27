package edu.tamu.app.cache.model;

public class RemoteProduct extends ProductStats {

    private static final long serialVersionUID = 8384046327331854613L;

    public RemoteProduct() {
        super();
    }

    public RemoteProduct(String id, String name, int requestCount, int issueCount, int featureCount, int defectCount) {
        super(id, name, requestCount, issueCount, featureCount, defectCount);
    }

}
