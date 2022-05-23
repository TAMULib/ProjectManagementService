package edu.tamu.app.model.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FeatureRequetTest {

    @Test
    public void testNewFeatureRequest() {
        FeatureRequest request = newFeatureRequest();
        assertNotNull(request, "Could not instantiate feature request!");
        assertEquals("New Feature", request.getTitle(), "Feature request had incorrect title!");
        assertEquals("I would like to turn off service through API.", request.getDescription(), "Feature request had incorrect description!");
        assertEquals(1L, request.getProductId(), 0, "Feature request had incorrect product id!");
        assertEquals("0001", request.getScopeId(), "Feature request had incorrect scope id!");
    }

    @Test
    public void testSetTitle() {
        FeatureRequest request = newFeatureRequest();
        request.setTitle("Fix It");
        assertEquals("Fix It", request.getTitle(), "Feature request did not set title!");
    }

    @Test
    public void testSetDescription() {
        FeatureRequest request = newFeatureRequest();
        request.setDescription("It just doesn't work!");
        assertEquals("It just doesn't work!", request.getDescription(), "Feature request did not set description!");
    }

    @Test
    public void testSetProductId() {
        FeatureRequest request = newFeatureRequest();
        request.setProductId(2L);
        assertEquals(2L, request.getProductId(), 0, "Feature request did not set product id!");
    }

    @Test
    public void testSetScopeId() {
        FeatureRequest request = newFeatureRequest();
        request.setScopeId("0002");
        assertEquals("0002", request.getScopeId(), "Feature request did not set scope id!");
    }

    private FeatureRequest newFeatureRequest() {
        return new FeatureRequest("New Feature", "I would like to turn off service through API.", 1L, "0001");
    }

}
