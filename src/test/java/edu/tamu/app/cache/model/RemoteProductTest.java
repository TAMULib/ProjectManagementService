package edu.tamu.app.cache.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RemoteProductTest {

    @Test
    public void testNewRemoteProduct() {
        RemoteProduct remoteProduct = new RemoteProduct("0001", "Sprint 1", 2, 3, 10, 3);
        assertEquals("0001", remoteProduct.getId());
        assertEquals("Sprint 1", remoteProduct.getName());
        assertEquals(2, remoteProduct.getRequestCount());
        assertEquals(3, remoteProduct.getIssueCount());
        assertEquals(10, remoteProduct.getFeatureCount());
        assertEquals(3, remoteProduct.getDefectCount());
        assertEquals(13, remoteProduct.getBacklogItemCount());
    }

}
