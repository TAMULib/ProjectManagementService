package edu.tamu.app.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.repo.CardTypeRepo;
import edu.tamu.app.model.repo.EstimateRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProductManagerRepo;
import edu.tamu.app.model.repo.StatusRepo;

public abstract class ModelTest {

    protected static final String TEST_PRODUCT_NAME = "Test Product Name";

    protected static final String TEST_ALTERNATE_PRODUCT_NAME = "Alternate Product Name";

    protected static final String TEST_REMOTE_PRODUCT_MANAGER1_NAME = "Test Remote Product Manager 1";
    protected static final String TEST_REMOTE_PRODUCT_MANAGER2_NAME = "Test Remote Product Manager 2";

    protected static final String TEST_ALTERNATE_REMOTE_PRODUCT_MANAGER_NAME = "Alternate Remote Product Manager";

    @Autowired
    protected StatusRepo statusRepo;

    @Autowired
    protected CardTypeRepo cardTypeRepo;

    @Autowired
    protected EstimateRepo estimateRepo;

    @Autowired
    protected ProductRepo productRepo;

    @Autowired
    protected RemoteProductManagerRepo remoteProductManagerRepo;

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
        productRepo.deleteAll();
        remoteProductManagerRepo.deleteAll();
    }

}
