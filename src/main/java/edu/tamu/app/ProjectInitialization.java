package edu.tamu.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@Component
public class ProjectInitialization implements CommandLineRunner {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Override
    public void run(String... args) throws Exception {
        remoteProjectManagerRepo.findAll().forEach(versionManagementSoftware -> {
            managementBeanRegistry.register(versionManagementSoftware);
        });
    }

}
