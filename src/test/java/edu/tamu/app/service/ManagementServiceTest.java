package edu.tamu.app.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import java.util.HashMap;

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

    private static final HashMap<String, String> TEST_SETTINGS;
    static {
        TEST_SETTINGS = new HashMap<String, String>();
        TEST_SETTINGS.put("Test 1", "Test 1");
        TEST_SETTINGS.put("Test 2", "Test 2");
        TEST_SETTINGS.put("Test 3", "Test 3");
    }

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
    public void testSetSettings() {
        assertNull("Settings were not empty", managementService.getSettings());
        managementService.setSettings(TEST_SETTINGS);
        assertEquals("Settings where not set correctly", TEST_SETTINGS, managementService.getSettings());
    }

}
