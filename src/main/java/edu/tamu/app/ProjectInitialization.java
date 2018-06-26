//package edu.tamu.app;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import edu.tamu.app.enums.ServiceType;
//import edu.tamu.app.model.Project;
//import edu.tamu.app.model.RemoteProjectManager;
//import edu.tamu.app.model.repo.ProjectRepo;
//import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
//import edu.tamu.app.service.registry.ManagementBeanRegistry;
//
//@Component
//public class ProjectInitialization implements CommandLineRunner {
//
//    @Autowired
//    private ProjectRepo projectRepo;
//
//    @Autowired
//    private ManagementBeanRegistry managementBeanRegistry;
//
//    @Autowired
//    private RemoteProjectManagerRepo remoteProjectManagerRepo;
//
//    @Override
//    public void run(String... args) throws Exception {
//        remoteProjectManagerRepo.findAll().forEach(versionManagementSoftware -> {
//            managementBeanRegistry.register(versionManagementSoftware);
//        });
//
//        Map<String, String> settings = new HashMap<String, String>() {
//            private static final long serialVersionUID = -8356476994617724353L;
//            {
//                put("url", "https://www15.v1host.com/TexasAMLibrary/");
//                put("username", "wwelling");
//                put("password", "DlbLsaDtattvi1981~");
//            }
//        };
//
//        RemoteProjectManager versionOne = remoteProjectManagerRepo.create(new RemoteProjectManager("Version One", ServiceType.VERSION_ONE, settings));
//
//        projectRepo.create(new Project("Cap", "7869", versionOne));
//        projectRepo.create(new Project("Directory", "5270", versionOne));
//        projectRepo.create(new Project("MyLibrary", "4871", versionOne));
//        projectRepo.create(new Project("MAGPIE", "5070", versionOne));
//        projectRepo.create(new Project("OAKTrust", "4889", versionOne));
//        projectRepo.create(new Project("Subject Librarian", "5525", versionOne));
//
//    }
//}
