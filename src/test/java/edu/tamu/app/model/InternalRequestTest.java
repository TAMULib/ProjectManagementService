package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.versionone.apiclient.exceptions.APIException;
import com.versionone.apiclient.exceptions.ConnectionException;
import com.versionone.apiclient.exceptions.OidException;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.model.repo.ProductRepo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class InternalRequestTest extends ModelTest {

    @Autowired
    private ProductRepo productRepo;

    @Before
    public void setup() throws ConnectionException, APIException, OidException, IOException {
        productRepo.create(TEST_PRODUCT);
    }

    @Test
    public void testCreate() {
        InternalRequest internalRequest1 = internalRequestRepo.create(TEST_INTERNAL_REQUEST1);
        assertEquals("Internal request had incorrect title!", TEST_INTERNAL_REQUEST_TITLE1, internalRequest1.getTitle());
    }

    @Test
    public void testRead() {
        internalRequestRepo.create(TEST_INTERNAL_REQUEST1);
        assertEquals("Could not read all internal requests!", 1, internalRequestRepo.findAll().size());
    }

    @Test
    public void testUpdate() {
        InternalRequest internalRequest1 = internalRequestRepo.create(TEST_INTERNAL_REQUEST1);
        internalRequest1.setTitle(TEST_INTERNAL_REQUEST_TITLE2);
        internalRequest1 = internalRequestRepo.update(internalRequest1);
        assertEquals("Internal request did not update title!", TEST_INTERNAL_REQUEST_TITLE2, internalRequest1.getTitle());
    }

    @Test
    public void testDelete() {
        InternalRequest internalRequest1 = internalRequestRepo.create(TEST_INTERNAL_REQUEST1);
        internalRequestRepo.delete(internalRequest1);
        assertEquals("Internal request was not deleted!", 0, internalRequestRepo.count());
    }

    @After
    public void cleanup() {
    }

}
