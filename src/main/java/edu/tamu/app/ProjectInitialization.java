package edu.tamu.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.repo.VersionManagementSoftwareRepo;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@Component
public class ProjectInitialization implements CommandLineRunner {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private VersionManagementSoftwareRepo versionManagementSoftwareRepo;

    @Override
    public void run(String... args) throws Exception {
        versionManagementSoftwareRepo.findAll().forEach(versionManagementSoftware -> {
            managementBeanRegistry.register(versionManagementSoftware);
        });
    }
}
