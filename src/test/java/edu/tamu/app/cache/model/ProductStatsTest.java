package edu.tamu.app.cache.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ProductStatsTest {

    @Test
    public void testNewProductStats() {
        ProductStats productStats = new ProductStats("0001", "Sprint 1", 2, 3, 10, 3);
        assertEquals("0001", productStats.getId());
        assertEquals("Sprint 1", productStats.getName());
        assertEquals(2, productStats.getRequestCount());
        assertEquals(3, productStats.getIssueCount());
        assertEquals(10, productStats.getFeatureCount());
        assertEquals(3, productStats.getDefectCount());
        assertEquals(13, productStats.getBacklogItemCount());
    }

}
