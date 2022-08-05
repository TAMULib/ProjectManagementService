package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.model.ServiceType.Setting;

@ExtendWith(SpringExtension.class)
public class ServiceTypeTest {

    private static final String TEST_TYPE = "text";
    private static final String TEST_GLOSS = "Gloss";
    private static final String TEST_KEY = "key";
    private static final boolean TEST_VISIBLE = true;

    private static ServiceType type = ServiceType.VERSION_ONE;

    private static Setting setting = new Setting(TEST_TYPE, TEST_KEY, TEST_GLOSS, TEST_VISIBLE);

    @Test
    public void testGetGloss() {
        assertEquals("Version One", type.getGloss(), "Gloss did not start out as 'Version One'");
    }

    @Test
    public void testSetGloss() {
        type.setGloss(TEST_GLOSS);
        assertEquals(TEST_GLOSS, type.getGloss(), "Gloss value is not what was expected");
    }

    @Test
    public void testSettingsValues() {
        assertEquals(TEST_TYPE, setting.getType(), "Settings did not have the correct type!");
        assertEquals(TEST_GLOSS, setting.getGloss(), "Settings did not have the correct gloss!");
        assertEquals(TEST_KEY, setting.getKey(), "Settings did not have the correct key!");
        assertEquals(TEST_VISIBLE, setting.isVisible(), "Settings did not have the correct visible flag!");
    }

}
