package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.model.ServiceType.Setting;

@RunWith(SpringRunner.class)
public class ServiceTypeTest {

    private static final String TEST_TYPE = "text";
    private static final String TEST_GLOSS = "Gloss";
    private static final String TEST_KEY = "key";
    private static final boolean TEST_VISIBLE = true;

    private static ServiceType type = ServiceType.VERSION_ONE;

    private static Setting setting = new Setting(TEST_TYPE, TEST_KEY, TEST_GLOSS, TEST_VISIBLE);

    @Test
    public void testGetGloss() {
        assertEquals("Gloss did not start out as 'Version One'", "Version One", type.getGloss());
    }

    @Test
    public void testSetGloss() {
        type.setGloss(TEST_GLOSS);
        assertEquals("Gloss value is not what was expected", TEST_GLOSS, type.getGloss());
    }

    @Test
    public void testSettingsValues() {
        assertEquals("Settings did not have the correct type!", TEST_TYPE, setting.getType());
        assertEquals("Settings did not have the correct gloss!", TEST_GLOSS, setting.getGloss());
        assertEquals("Settings did not have the correct key!", TEST_KEY, setting.getKey());
        assertEquals("Settings did not have the correct visible flag!", TEST_VISIBLE, setting.isVisible());
    }

}
