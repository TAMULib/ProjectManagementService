package edu.tamu.app.model.request;

public class TicketRequest extends AbstractRequest {

    private static final long serialVersionUID = -7150986466522854974L;

    private String service;

    public TicketRequest() {
        super();
    }

    public TicketRequest(String title, String description, String service) {
        super(title, description);
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

}
