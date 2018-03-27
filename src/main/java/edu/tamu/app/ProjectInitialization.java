package edu.tamu.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.ManagementSetting;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.VersionManagementSoftwareRepo;
import edu.tamu.app.service.registry.ManagementBeanRegistry;

@Component
@Profile("!test")
public class ProjectInitialization implements CommandLineRunner {

    @Autowired
    private ManagementBeanRegistry managementBeanRegistry;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private VersionManagementSoftwareRepo versionManagementSoftwareRepo;

    @Override
    public void run(String... args) throws Exception {

        // TODO: register beans for management services that are persisted

        // TODO: remove all the following

        List<ManagementSetting> settings = new ArrayList<ManagementSetting>() {
            private static final long serialVersionUID = 2020874481642498006L;
            {
                add(new ManagementSetting("url", "https://www15.v1host.com/TexasAMLibrary"));
                add(new ManagementSetting("username", ""));
                add(new ManagementSetting("password", ""));
            }
        };

        VersionManagementSoftware versionManagementSoftware = versionManagementSoftwareRepo.create(new VersionManagementSoftware("Version One", ServiceType.VERSION_ONE, settings));

        Project project = projectRepo.create(new Project("Cap", "7869", versionManagementSoftware));

        managementBeanRegistry.register(project, versionManagementSoftware);
    }
}
