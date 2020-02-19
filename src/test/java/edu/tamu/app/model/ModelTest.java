package edu.tamu.app.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.repo.CardTypeRepo;
import edu.tamu.app.model.repo.EstimateRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.repo.StatusRepo;

public abstract class ModelTest {

    protected static final String TEST_PROJECT_NAME = "Test Project Name";

    protected static final String TEST_ALTERNATE_PROJECT_NAME = "Alternate Project Name";

    protected static final String TEST_REMOTE_PROJECT_MANAGER1_NAME = "Test Remote Project Manager 1";
    protected static final String TEST_REMOTE_PROJECT_MANAGER2_NAME = "Test Remote Project Manager 2";

    protected static final String TEST_ALTERNATE_REMOTE_PROJECT_MANAGER_NAME = "Alternate Remote Project Manager";

    @Autowired
    protected StatusRepo statusRepo;

    @Autowired
    protected CardTypeRepo cardTypeRepo;

    @Autowired
    protected EstimateRepo estimateRepo;

    @Autowired
    protected ProjectRepo projectRepo;

    @Autowired
    protected RemoteProjectManagerRepo remoteProjectManagerRepo;

    protected Map<String, String> getMockSettings() {
        return new HashMap<String, String>() {
            private static final long serialVersionUID = 2020874481642498006L;
            {
                put("url", "https://localhost:9101/TexasAMLibrary");
                put("username", "username");
                put("password", "password");
            }
        };
    }

    @After
    public void cleanup() {
        statusRepo.deleteAll();
        cardTypeRepo.deleteAll();
        estimateRepo.deleteAll();
        projectRepo.deleteAll();
        remoteProjectManagerRepo.deleteAll();
    }

}
