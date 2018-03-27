package edu.tamu.app;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.Project;
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

        // TODO: register beans for management services that are persisted

        // TODO: remove all the following

        Map<String, String> settings = new HashMap<String, String>();
        
        settings.put("url", "https://www15.v1host.com/TexasAMLibrary");
        settings.put("username", "");
        settings.put("password", "");

        VersionManagementSoftware versionManagementSoftware = new VersionManagementSoftware("Version One", ServiceType.VERSION_ONE, settings);

        Project project = projectRepo.create(new Project("Cap", "7869", versionManagementSoftware));

        managementBeanRegistry.register(project, versionManagementSoftware);
    }
}
