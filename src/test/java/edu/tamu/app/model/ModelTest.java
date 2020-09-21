package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.repo.CardTypeRepo;
import edu.tamu.app.model.repo.EstimateRepo;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.repo.StatusRepo;

public abstract class ModelTest {

    protected static final String TEST_PRODUCT_NAME = "Test Product Name";

    protected static final String TEST_ALTERNATE_PRODUCT_NAME = "Alternate Product Name";

    protected static final String TEST_REMOTE_PROJECT_MANAGER1_NAME = "Test Remote Project Manager 1";
    protected static final String TEST_REMOTE_PROJECT_MANAGER2_NAME = "Test Remote Project Manager 2";

    protected static final String TEST_ALTERNATE_REMOTE_PROJECT_MANAGER_NAME = "Alternate Remote Project Manager";

    protected static final String TEST_PROJECT_SCOPE1 = "0010";
    protected static final String TEST_PROJECT_SCOPE2 = "0011";
    protected static final String TEST_PROJECT_SCOPE3 = "0020";

    protected static final String TEST_PROJECT_URL1 = "http://localhost/1";
    protected static final String TEST_PROJECT_URL2 = "http://localhost/2";

    protected static final String TEST_PROJECT_TOKEN1 = "0123456789";
    protected static final String TEST_PROJECT_TOKEN2 = "9876543210";

    protected static final String TEST_INTERNAL_REQUEST_TITLE1 = "Test Internal Request Title 1";
    protected static final String TEST_INTERNAL_REQUEST_TITLE2 = "Test Internal Request Title 2";

    protected static final String TEST_INTERNAL_REQUEST_DESCRIPTION1 = "Test Internal Request Description 1";
    protected static final String TEST_INTERNAL_REQUEST_DESCRIPTION2 = "Test Internal Request Description 2";

    protected static final String TEST_OTHER_URL_1 = "Test Other URL 1";
    protected static final String TEST_OTHER_URL_2 = "Test Other URL 2";

    protected static final Date TEST_INTERNAL_REQUEST_CREATED_ON1 = new Date();
    protected static final Date TEST_INTERNAL_REQUEST_CREATED_ON2 = new Date(System.currentTimeMillis() + 100);

    protected static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER1 = new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER1_NAME, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);
    protected static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER2 = new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER2_NAME, ServiceType.GITHUB_PROJECT, TEST_PROJECT_URL2, TEST_PROJECT_TOKEN2);

    protected static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
    protected static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO2 = new RemoteProjectInfo(TEST_PROJECT_SCOPE2, TEST_REMOTE_PROJECT_MANAGER1);
    protected static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO3 = new RemoteProjectInfo(TEST_PROJECT_SCOPE3, TEST_REMOTE_PROJECT_MANAGER2);

    protected static final List<RemoteProjectInfo> TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1 = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1, TEST_REMOTE_PROJECT_INFO2));
    protected static final List<RemoteProjectInfo> TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST2 = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO3));

    protected static final List<String> TEST_OTHER_URLS = new ArrayList<String>(Arrays.asList(TEST_OTHER_URL_1, TEST_OTHER_URL_2));

    protected static final Product TEST_PRODUCT = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", "", TEST_OTHER_URLS);

    protected static final InternalRequest TEST_INTERNAL_REQUEST1 = new InternalRequest(TEST_INTERNAL_REQUEST_TITLE1, TEST_INTERNAL_REQUEST_DESCRIPTION1, TEST_PRODUCT, TEST_INTERNAL_REQUEST_CREATED_ON1);
    protected static final InternalRequest TEST_INTERNAL_REQUEST2 = new InternalRequest(TEST_INTERNAL_REQUEST_TITLE2, TEST_INTERNAL_REQUEST_DESCRIPTION2, null, TEST_INTERNAL_REQUEST_CREATED_ON2);

    @Autowired
    protected StatusRepo statusRepo;

    @Autowired
    protected CardTypeRepo cardTypeRepo;

    @Autowired
    protected EstimateRepo estimateRepo;

    @Autowired
    protected ProductRepo productRepo;

    @Autowired
    protected RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Autowired
    protected InternalRequestRepo internalRequestRepo;

    @After
    public void cleanup() {
        statusRepo.deleteAll();
        cardTypeRepo.deleteAll();
        estimateRepo.deleteAll();
        internalRequestRepo.deleteAll();
        productRepo.deleteAll();
        remoteProjectManagerRepo.deleteAll();
    }

}
