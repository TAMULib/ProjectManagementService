package edu.tamu.app;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@Component
@Profile("!test")
public class ProjectInitialization implements CommandLineRunner {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private ProjectRepo projectRepo;

    @Override
    public void run(String... args) throws Exception {
        projectRepo.findAll().forEach(project -> {
            Optional<VersionManagementSoftware> versionManagementSoftware = Optional.ofNullable(project.getVersionManagementSoftware());
            if (versionManagementSoftware.isPresent()) {
                managementBeanRegistry.register(project, versionManagementSoftware.get());
            }
        });
    }
}
