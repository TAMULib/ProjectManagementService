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
        assertEquals(request.getTitle(), "Feature request had incorrect title!", "New Feature");
        assertEquals(request.getDescription(), "Feature request had incorrect description!", "I would like to turn off service through API.");
        assertEquals(1L, request.getProductId(), 0, "Feature request had incorrect product id!");
        assertEquals(request.getScopeId(), "Feature request had incorrect scope id!", "0001");
    }

    @Test
    public void testSetTitle() {
        FeatureRequest request = newFeatureRequest();
        request.setTitle("Fix It");
        assertEquals(request.getTitle(), "Feature request did not set title!", "Fix It");
    }

    @Test
    public void testSetDescription() {
        FeatureRequest request = newFeatureRequest();
        request.setDescription("It just doesn't work!");
        assertEquals(request.getDescription(), "Feature request did not set description!", "It just doesn't work!");
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
        assertEquals(request.getScopeId(), "Feature request did not set scope id!", "0002");
    }

    private FeatureRequest newFeatureRequest() {
        return new FeatureRequest("New Feature", "I would like to turn off service through API.", 1L, "0001");
    }

}
