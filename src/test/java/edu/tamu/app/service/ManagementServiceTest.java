package edu.tamu.app.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.ServiceType;

@RunWith(SpringRunner.class)
public class ManagementServiceTest {

    private static final String TEST_NAME = "Test Name";

    private static ServiceType TEST_TYPE = ServiceType.VERSION_ONE;

    private static final String TEST_URL1 = "http://localhost/1";

    private static final String TEST_TOKEN1 = "0123456789";

    private ManagementService managementService;

    @Before
    public void setup() {
        managementService = mock(ManagementService.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testSetName() {
        assertNull("Name was already set", managementService.getName());
        managementService.setName(TEST_NAME);
        assertEquals("Name was not set correctly", TEST_NAME, managementService.getName());
    }

    @Test
    public void testSetType() {
        assertNull("Type was already set", managementService.getType());
        managementService.setType(TEST_TYPE);
        assertEquals("Type was not set correctly", TEST_TYPE, managementService.getType());
    }

    @Test
    public void testSetUrl() {
        assertNull("URL was already set", managementService.getUrl());
        managementService.setUrl(TEST_URL1);
        assertEquals("URL was not set correctly", TEST_URL1, managementService.getUrl());
    }

    @Test
    public void testSetToken() {
        assertNull("Token was already set", managementService.getToken());
        managementService.setToken(TEST_TOKEN1);
        assertEquals("Token was not set correctly", TEST_TOKEN1, managementService.getToken());
    }

}
