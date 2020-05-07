package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    protected static final String TEST_PRODUCT_SCOPE1 = "0010";
    protected static final String TEST_PRODUCT_SCOPE2 = "0011";
    protected static final String TEST_PRODUCT_SCOPE3 = "0020";

    protected static final RemoteProductManager TEST_REMOTE_PRODUCT_MANAGER1 = new RemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER1_NAME, ServiceType.VERSION_ONE, getMockSettings());
    protected static final RemoteProductManager TEST_REMOTE_PRODUCT_MANAGER2 = new RemoteProductManager(TEST_REMOTE_PRODUCT_MANAGER2_NAME, ServiceType.GITHUB, getMockSettings());

    protected static final RemoteProductInfo TEST_REMOTE_PRODUCT_INFO1 = new RemoteProductInfo(TEST_PRODUCT_SCOPE1, TEST_REMOTE_PRODUCT_MANAGER1);
    protected static final RemoteProductInfo TEST_REMOTE_PRODUCT_INFO2 = new RemoteProductInfo(TEST_PRODUCT_SCOPE2, TEST_REMOTE_PRODUCT_MANAGER1);
    protected static final RemoteProductInfo TEST_REMOTE_PRODUCT_INFO3 = new RemoteProductInfo(TEST_PRODUCT_SCOPE3, TEST_REMOTE_PRODUCT_MANAGER2);

    protected static final List<RemoteProductInfo> TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST1 = new ArrayList<RemoteProductInfo>(Arrays.asList(TEST_REMOTE_PRODUCT_INFO1, TEST_REMOTE_PRODUCT_INFO2));
    protected static final List<RemoteProductInfo> TEST_PRODUCT_REMOTE_PRODUCT_INFO_LIST2 = new ArrayList<RemoteProductInfo>(Arrays.asList(TEST_REMOTE_PRODUCT_INFO3));

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

    protected static Map<String, String> getMockSettings() {
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
