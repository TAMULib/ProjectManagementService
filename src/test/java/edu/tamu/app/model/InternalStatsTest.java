package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class InternalStatsTest {

    @Test
    public void testNewInternalStats() {
        InternalStats internalStats = new InternalStats(1, 3);
        assertEquals(2, internalStats.getAssignedCount());
        assertEquals(1, internalStats.getUnassignedCount());
        assertEquals(3, internalStats.getTotalCount());
    }

}
