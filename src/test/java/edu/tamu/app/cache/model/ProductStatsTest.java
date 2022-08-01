package edu.tamu.app.cache.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProductStatsTest {

    @Test
    public void testNewProductStats() {
        ProductStats productStats = new ProductStats("0001", "Sprint 1", 2, 3, 10, 3, 1);
        assertEquals(productStats.getId(), "0001");
        assertEquals(productStats.getName(), "Sprint 1");
        assertEquals(2, productStats.getRequestCount());
        assertEquals(3, productStats.getIssueCount());
        assertEquals(10, productStats.getFeatureCount());
        assertEquals(3, productStats.getDefectCount());
        assertEquals(1, productStats.getInternalCount());
        assertEquals(13, productStats.getBacklogItemCount());
    }

}
