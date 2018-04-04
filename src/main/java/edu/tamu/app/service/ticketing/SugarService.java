package edu.tamu.app.service.ticketing;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.weaver.auth.model.Credentials;
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
            emailService.sendEmail(reportingAddress, getSubject(request), getBody(request));
            results = "Successfully submitted issue for " + request.getService() + "!";
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return results;
    }

    private String getSubject(TicketRequest request) {
        return request.getService() + " Issue: " + request.getTitle();
    }

    private String getBody(TicketRequest request) {
        Credentials submitter = request.getCredentials();
        return request.getDescription() + "<br/><br/>Submitted by: " + submitter.getLastName() + ", " + submitter.getFirstName() + " (" + submitter.getEmail() + ")";

    }

}
