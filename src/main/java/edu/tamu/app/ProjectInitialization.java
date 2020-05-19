package edu.tamu.app;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.CardType;
import edu.tamu.app.model.repo.CardTypeRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@Component
@Profile("!test")
public class ProjectInitialization implements CommandLineRunner {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Autowired
    private CardTypeRepo cardTypeRepo;

    @Override
    public void run(String... args) throws Exception {
        remoteProjectManagerRepo.findAll().forEach(versionManagementSoftware -> {
            managementBeanRegistry.register(versionManagementSoftware);
        });
        CardType type = cardTypeRepo.findByIdentifier("Feature");
        HashSet<String> featureTypes = new HashSet<String>(Arrays.asList(new String[] { "Story", "feature" }));
        if (type == null) {
            cardTypeRepo.create(new CardType("Feature", featureTypes));
        } else if (!type.getMapping().equals(featureTypes)) {
            type.setMapping(featureTypes);
            cardTypeRepo.update(type);
        }
        if (cardTypeRepo.findByIdentifier("Request") == null) {
            cardTypeRepo.create(new CardType("Request", new HashSet<String>(Arrays.asList(new String[] { "request" }))));
        }
        if (cardTypeRepo.findByIdentifier("Issue") == null) {
            cardTypeRepo.create(new CardType("Issue", new HashSet<String>(Arrays.asList(new String[] { "issue" }))));
        }
        if (cardTypeRepo.findByIdentifier("Defect") == null) {
            cardTypeRepo.create(new CardType("Defect", new HashSet<String>(Arrays.asList(new String[] { "bug" }))));
        }
    }

}
