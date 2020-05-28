package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class InternalStatsTest {

    @Test
    public void testNewInternalStats() {
        InternalStats internalStats = new InternalStats(1, 3);
        assertEquals(2, internalStats.getAssignedCount());
        assertEquals(1, internalStats.getUnassignedCount());
        assertEquals(3, internalStats.getTotalCount());
    }

}
