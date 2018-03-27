package edu.tamu.app.model.request;

import edu.tamu.weaver.auth.model.Credentials;

public class TicketRequest extends AbstractRequest {

    private static final long serialVersionUID = -7150986466522854974L;

    private String service;

    private Credentials credentials;

    public TicketRequest() {
        super();
    }

    public TicketRequest(String title, String description, String service, Credentials credentials) {
        super(title, description);
        this.service = service;

        this.credentials = credentials;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

}
