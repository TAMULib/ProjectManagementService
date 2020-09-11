package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProductApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ProductModelTest extends ModelTest {
    protected static final String TEST_URL_1 = "http://localhost/";
    protected static final String TEST_URL_2 = "http://127.0.0.1/";

    @Test
    public void testGetName() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        assertEquals("Product did not return the correct name!", TEST_PRODUCT_NAME, product.getName());
    }

    @Test
    public void testGetScopeId() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", "", null);
        assertEquals("Product did not return the correct scope id!", TEST_PROJECT_SCOPE1, product.getScopeId());
    }

    @Test
    public void testGetDevUri() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, TEST_URL_1, "", "", "", null);
        assertEquals("Product did not return the correct dev URL!", TEST_URL_1, product.getDevUrl());
    }

    @Test
    public void testGetPreUri() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", TEST_URL_1, "", "", null);
        assertEquals("Product did not return the correct pre URL!", TEST_URL_1, product.getPreUrl());
    }

    @Test
    public void testGetProductionUrl() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", TEST_URL_1, "", null);
        assertEquals("Product did not return the correct production URL!", TEST_URL_1, product.getProductionUrl());
    }

    @Test
    public void testGetWikiUrl() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", TEST_URL_1, null);
        assertEquals("Product did not return the correct wiki URL!", TEST_URL_1, product.getWikiUrl());
    }

    @Test
    public void testGetRemoteProducts() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        assertEquals("Product did not return the correct remote project info!", TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, product.getRemoteProjectInfo());
    }

    @Test
    public void testSetName() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        product.setName(TEST_PROJECT_SCOPE2);
        assertEquals("Product did not correctly update the scope id!", TEST_PROJECT_SCOPE2, product.getName());
    }

    @Test
    public void testSetDevUrl() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, TEST_URL_1, "", "", "", null);
        product.setDevUrl(TEST_URL_2);
        assertEquals("Product did not correctly update the dev URL!", TEST_URL_2, product.getDevUrl());
    }

    @Test
    public void testSetPreUrl() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", TEST_URL_1, "", "", null);
        product.setPreUrl(TEST_URL_2);
        assertEquals("Product did not correctly update the pre URL!", TEST_URL_2, product.getPreUrl());
    }

    @Test
    public void testSetProductionUrl() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", TEST_URL_1, "", null);
        product.setProductionUrl(TEST_URL_2);
        assertEquals("Product did not correctly update the production URL!", TEST_URL_2, product.getProductionUrl());
    }

    @Test
    public void testSetWikiUrl() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", TEST_URL_1, null);
        product.setWikiUrl(TEST_URL_2);
        assertEquals("Product did not correctly update the Wiki URL!", TEST_URL_2, product.getWikiUrl());
    }

    @Test
    public void testSetScopeId() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1, TEST_PROJECT_SCOPE1, "", "", "", "", null);
        product.setScopeId(TEST_PROJECT_SCOPE2);
        assertEquals("Product did not correctly update the scope id!", TEST_PROJECT_SCOPE2, product.getScopeId());
    }

    @Test
    public void testSetRemoteProducts() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        product.setRemoteProductInfo(TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST2);
        assertEquals("Product did not return the correct remote project info!", TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST2, product.getRemoteProjectInfo());
    }

    @Test
    public void testAddRemoteProduct() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        product.addRemoteProductInfo(TEST_REMOTE_PROJECT_INFO3);
        List<RemoteProjectInfo> remoteProjectInfo = product.getRemoteProjectInfo();
        assertEquals("Product did not correctly add the remote project!", true, remoteProjectInfo.contains(TEST_REMOTE_PROJECT_INFO3));
    }

    @Test
    public void testRemoveRemoteProduct() {
        Product product = new Product(TEST_PRODUCT_NAME, TEST_PRODUCT_REMOTE_PROJECT_INFO_LIST1);
        product.removeRemoteProduct(TEST_REMOTE_PROJECT_INFO1);
        List<RemoteProjectInfo> remoteProjectInfo = product.getRemoteProjectInfo();
        assertEquals("Product did not correctly add the remote project!", false, remoteProjectInfo.contains(TEST_REMOTE_PROJECT_INFO1));
    }

}
