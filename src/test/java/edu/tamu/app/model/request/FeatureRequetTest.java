package edu.tamu.app.model.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class FeatureRequetTest {

    @Test
    public void testNewFeatureRequest() {
        FeatureRequest request = newFeatureRequest();
        assertNotNull("Could not instantiate feature request!", request);
        assertEquals("Feature request had incorrect title!", "New Feature", request.getTitle());
        assertEquals("Feature request had incorrect description!", "I would like to turn off service through API.", request.getDescription());
        assertEquals("Feature request had incorrect product id!", 1L, request.getProductId(), 0);
        assertEquals("Feature request had incorrect scope id!", "0001", request.getScopeId());
    }

    @Test
    public void testSetTitle() {
        FeatureRequest request = newFeatureRequest();
        request.setTitle("Fix It");
        assertEquals("Feature request did not set title!", "Fix It", request.getTitle());
    }

    @Test
    public void testSetDescription() {
        FeatureRequest request = newFeatureRequest();
        request.setDescription("It just doesn't work!");
        assertEquals("Feature request did not set description!", "It just doesn't work!", request.getDescription());
    }

    @Test
    public void testSetProductId() {
        FeatureRequest request = newFeatureRequest();
        request.setProductId(2L);
        assertEquals("Feature request did not set product id!", 2L, request.getProductId(), 0);
    }

    @Test
    public void testSetScopeId() {
        FeatureRequest request = newFeatureRequest();
        request.setScopeId("0002");
        assertEquals("Feature request did not set scope id!", "0002", request.getScopeId());
    }

    private FeatureRequest newFeatureRequest() {
        return new FeatureRequest("New Feature", "I would like to turn off service through API.", 1L, "0001");
    }

}
