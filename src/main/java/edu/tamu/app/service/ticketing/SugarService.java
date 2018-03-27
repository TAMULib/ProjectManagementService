package edu.tamu.app.service.ticketing;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.weaver.email.service.EmailSender;

@Service
public class SugarService implements TicketManagementSoftwareBean {

    @Autowired
    private EmailSender emailService;

    // NOTE: using reporting email as it is the same
    @Value("${app.reporting.address}")
    private String reportingAddress;

    @Override
    public String submit(TicketRequest request) {
        String results = "Unable to submit ticket to sugar at this time!";
        try {
            emailService.sendEmail(reportingAddress, request.getService() + " Issue: " + request.getTitle(), request.getDescription());
            results = "Successfully submitted issue " + request.getService() + "!";
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return results;
    }

}
