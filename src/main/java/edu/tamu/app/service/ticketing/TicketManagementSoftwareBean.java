package edu.tamu.app.service.ticketing;

import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.registry.ManagementBean;

public interface TicketManagementSoftwareBean extends ManagementBean {

    public String submit(TicketRequest request);

}
