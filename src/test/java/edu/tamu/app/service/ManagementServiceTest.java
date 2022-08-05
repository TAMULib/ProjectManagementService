package edu.tamu.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.ServiceType;

@ExtendWith(SpringExtension.class)
public class ManagementServiceTest {

    private static final String TEST_NAME = "Test Name";

    private static ServiceType TEST_TYPE = ServiceType.VERSION_ONE;

    private static final String TEST_URL1 = "http://localhost/1";

    private static final String TEST_TOKEN1 = "0123456789";

    private ManagementService managementService;

    @BeforeEach
    public void setup() {
        managementService = mock(ManagementService.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testSetName() {
        assertNull(managementService.getName(), "Name was already set");
        managementService.setName(TEST_NAME);
        assertEquals(TEST_NAME, managementService.getName(), "Name was not set correctly");
    }

    @Test
    public void testSetType() {
        assertNull(managementService.getType(), "Type was already set");
        managementService.setType(TEST_TYPE);
        assertEquals(TEST_TYPE, managementService.getType(), "Type was not set correctly");
    }

    @Test
    public void testSetUrl() {
        assertNull(managementService.getUrl(), "URL was already set");
        managementService.setUrl(TEST_URL1);
        assertEquals(TEST_URL1, managementService.getUrl(), "URL was not set correctly");
    }

    @Test
    public void testSetToken() {
        assertNull(managementService.getToken(), "Token was already set");
        managementService.setToken(TEST_TOKEN1);
        assertEquals(TEST_TOKEN1, managementService.getToken(), "Token was not set correctly");
    }

}
