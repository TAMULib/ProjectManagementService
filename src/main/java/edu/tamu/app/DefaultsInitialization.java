package edu.tamu.app;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.CardType;
import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.CardTypeRepo;
import edu.tamu.app.model.repo.StatusRepo;

@Component
@Profile("!test")
public class DefaultsInitialization implements CommandLineRunner {

    @Autowired
    private StatusRepo statusRepo;

    @Autowired
    private CardTypeRepo cardTypeRepo;

    @Override
    public void run(String... args) throws Exception {
        if (statusRepo.findByIdentifier("None") == null) {
            statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        }

        if (cardTypeRepo.findByIdentifier("Feature") == null) {
            cardTypeRepo.create(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story" }))));
        }
    }

}
