package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class InternalStatsTest {

    @Test
    public void testNewInternalStats() {
        InternalStats internalStats = new InternalStats(3);
        assertEquals(3, internalStats.getInternalCount());
    }

}
