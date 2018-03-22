package edu.tamu.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.ManagementSetting;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@Component
public class ProjectInitialization implements CommandLineRunner {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private ProjectRepo projectRepo;

    @Override
    public void run(String... args) throws Exception {

        // TODO: get the management software entity from the projects or database

        // List<ManagementSetting> settings = new ArrayList<ManagementSetting>();

        // settings.add(new ManagementSetting("url", "https://www15.v1host.com/TexasAMLibrary"));


        // VersionManagementSoftware versionManagementSoftware = new VersionManagementSoftware("Version One", ServiceType.VERSION_ONE, settings);

        // TODO: loop over persisted projects and register their management software entity beans

        // Project project = projectRepo.create(new Project("Initial Sample Project", versionManagementSoftware));

        // managementBeanRegistry.register(project, versionManagementSoftware);

    }
}
