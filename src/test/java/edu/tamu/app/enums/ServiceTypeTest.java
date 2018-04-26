package edu.tamu.app.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.enums.ServiceType.Setting;

@RunWith(SpringRunner.class)
public class ServiceTypeTest {
    
    private static final String TEST_GLOSS = "Test Gloss";
    private static final String TEST_KEY = "Test Gloss";
    private static final boolean TEST_VISIBLE = true;
    
    private static ServiceType type = ServiceType.VERSION_ONE;
    
    private static Setting setting = new Setting(TEST_KEY, TEST_GLOSS, TEST_VISIBLE);

    @Test
    public void testSetAndGetGloss() {
        assertEquals("Gloss did not start out as 'Version One'", "Version One", type.getGloss());
        type.setGloss(TEST_GLOSS);
        assertEquals("Gloss value is not what was expected", TEST_GLOSS, type.getGloss());
    }
    
    @Test
    public void testSettingsValues() {
        assertEquals("Correct Gloss value was not returned", TEST_GLOSS, setting.getGloss());
        assertEquals("Correct Gloss value was not returned", TEST_KEY, setting.getKey());
        assertEquals("Correct Gloss value was not returned", TEST_VISIBLE, setting.isVisible());
    }
}
