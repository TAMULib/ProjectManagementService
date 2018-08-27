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
        if (cardTypeRepo.findByIdentifier("Feature") == null) {
            cardTypeRepo.create(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story" }))));
        }
    }

}
