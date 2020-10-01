package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.service.manager.GitHubProjectService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductTest extends AbstractModelTest {

    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER1 = new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME1, ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);
    private static final RemoteProjectManager TEST_REMOTE_PROJECT_MANAGER2 = new RemoteProjectManager(TEST_REMOTE_PROJECT_MANAGER_NAME2, ServiceType.GITHUB_PROJECT, TEST_PROJECT_URL2, TEST_PROJECT_TOKEN2);

    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO1 = new RemoteProjectInfo(TEST_PROJECT_SCOPE1, TEST_REMOTE_PROJECT_MANAGER1);
    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO2 = new RemoteProjectInfo(TEST_PROJECT_SCOPE2, TEST_REMOTE_PROJECT_MANAGER1);
    private static final RemoteProjectInfo TEST_REMOTE_PROJECT_INFO3 = new RemoteProjectInfo(TEST_PROJECT_SCOPE3, TEST_REMOTE_PROJECT_MANAGER2);

    private static final List<RemoteProjectInfo> TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1 = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO1, TEST_REMOTE_PROJECT_INFO2));
    private static final List<RemoteProjectInfo> TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST2 = new ArrayList<RemoteProjectInfo>(Arrays.asList(TEST_REMOTE_PROJECT_INFO3));

    private static final List<String> TEST_OTHER_URLS1 = new ArrayList<String>(Arrays.asList(TEST_OTHER_URL1, TEST_OTHER_URL2));

    private static final String TEST_URL_1 = "http://localhost/";
    private static final String TEST_URL_2 = "http://127.0.0.1/";

    @MockBean
    private SugarService sugarService;

    @MockBean
    private GitHubProjectService gitHubService;

    @MockBean
    private VersionOneService versionOneService;

    @MockBean
    private ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService;

    @MockBean
    private ProductsStatsScheduledCacheService productsStatsScheduledCacheService;

    @MockBean
    private RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService;

    @MockBean
    private GitHubBuilder ghBuilder;

    @MockBean
    private GitHub github;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockSugarService(sugarService);
        mockGitHubService(gitHubService, ghBuilder);
        mockVersionOneService(versionOneService);
        mockActiveSprintsScheduledCacheService(activeSprintsScheduledCacheService);
        mockProductsStatsScheduledCacheService(productsStatsScheduledCacheService);
        mockRemoteProjectsScheduledCacheService(remoteProjectsScheduledCacheService);
    }

    @Test
    public void testGetName() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        assertEquals("Product did not return the correct name!", TEST_PRODUCT_NAME1, product.getName());
    }

    @Test
    public void testGetScopeId() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", "", null);
        assertEquals("Product did not return the correct scope id!", TEST_PROJECT_SCOPE1, product.getScopeId());
    }

    @Test
    public void testGetDevUri() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, TEST_URL_1, "", "", "", null);
        assertEquals("Product did not return the correct dev URL!", TEST_URL_1, product.getDevUrl());
    }

    @Test
    public void testGetPreUri() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", TEST_URL_1, "", "", null);
        assertEquals("Product did not return the correct pre URL!", TEST_URL_1, product.getPreUrl());
    }

    @Test
    public void testGetProductionUrl() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", TEST_URL_1, "", null);
        assertEquals("Product did not return the correct production URL!", TEST_URL_1, product.getProductionUrl());
    }

    @Test
    public void testGetWikiUrl() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", TEST_URL_1, null);
        assertEquals("Product did not return the correct wiki URL!", TEST_URL_1, product.getWikiUrl());
    }

    @Test
    public void testGetRemoteProducts() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        assertEquals("Product did not return the correct remote project info!", TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, product.getRemoteProjectInfo());
    }

    @Test
    public void testSetName() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        product.setName(TEST_PROJECT_SCOPE2);
        assertEquals("Product did not correctly update the scope id!", TEST_PROJECT_SCOPE2, product.getName());
    }

    @Test
    public void testSetDevUrl() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, TEST_URL_1, "", "", "", null);
        product.setDevUrl(TEST_URL_2);
        assertEquals("Product did not correctly update the dev URL!", TEST_URL_2, product.getDevUrl());
    }

    @Test
    public void testSetPreUrl() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", TEST_URL_1, "", "", null);
        product.setPreUrl(TEST_URL_2);
        assertEquals("Product did not correctly update the pre URL!", TEST_URL_2, product.getPreUrl());
    }

    @Test
    public void testSetProductionUrl() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", TEST_URL_1, "", null);
        product.setProductionUrl(TEST_URL_2);
        assertEquals("Product did not correctly update the production URL!", TEST_URL_2, product.getProductionUrl());
    }

    @Test
    public void testSetWikiUrl() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", TEST_URL_1, null);
        product.setWikiUrl(TEST_URL_2);
        assertEquals("Product did not correctly update the Wiki URL!", TEST_URL_2, product.getWikiUrl());
    }

    @Test
    public void testSetScopeId() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", "", null);
        product.setScopeId(TEST_PROJECT_SCOPE2);
        assertEquals("Product did not correctly update the scope id!", TEST_PROJECT_SCOPE2, product.getScopeId());
    }

    @Test
    public void testSetRemoteProducts() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        product.setRemoteProductInfo(TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST2);
        assertEquals("Product did not return the correct remote project info!", TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST2, product.getRemoteProjectInfo());
    }

    @Test
    public void testAddRemoteProduct() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        product.addRemoteProductInfo(TEST_REMOTE_PROJECT_INFO3);
        List<RemoteProjectInfo> remoteProjectInfo = product.getRemoteProjectInfo();
        assertEquals("Product did not correctly add the remote project!", true, remoteProjectInfo.contains(TEST_REMOTE_PROJECT_INFO3));
    }

    @Test
    public void testRemoveRemoteProduct() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        product.removeRemoteProduct(TEST_REMOTE_PROJECT_INFO1);
        List<RemoteProjectInfo> remoteProjectInfo = product.getRemoteProjectInfo();
        assertEquals("Product did not correctly add the remote project!", false, remoteProjectInfo.contains(TEST_REMOTE_PROJECT_INFO1));
    }

    @Test
    public void testSetOtherUrls() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        product.setOtherUrls(TEST_OTHER_URLS1);
        assertEquals("Product did not correctly set the other URLs", 2, product.getOtherUrls().size());
    }

    @Test
    public void testAddOtherUrl() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", "", new ArrayList<String>(Arrays.asList(TEST_OTHER_URL1)));
        product.addOtherUrl(TEST_OTHER_URL2);
        assertEquals("Product did not correctly add the second URL", 2, product.getOtherUrls().size());
    }

    @Test
    public void testRemoveOtherUrl() {
        Product product = new Product(TEST_PRODUCT_NAME1, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", "", TEST_OTHER_URLS1);
        product.removeOtherUrl(TEST_OTHER_URL1);
        assertEquals("Product did not remove other URL", 1, product.getOtherUrls().size());
        assertEquals("Product did not remove correct other URL", TEST_OTHER_URL2, product.getOtherUrls().get(0));
    }

}
