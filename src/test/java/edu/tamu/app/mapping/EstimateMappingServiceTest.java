package edu.tamu.app.mapping;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProjectApplication;
import edu.tamu.app.model.Estimate;
import edu.tamu.app.model.repo.EstimateRepo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class EstimateMappingServiceTest {

    @Autowired
    protected EstimateRepo estimateRepo;

    @Autowired
    private EstimateMappingService estimateMappingService;

    @Before
    public void setup() {
        estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
    }

    @Test
    public void testMap() {
        assertEquals(1.0f, estimateMappingService.map("Small"), 0);
        assertEquals(1.0f, estimateMappingService.map("1.0"), 0);
    }

    @Test
    public void testHandleUnmapped() {
        assertEquals("Handled unmapped incorrectly!", null, estimateMappingService.handleUnmapped("Unknown"));
    }

    @After
    public void cleanup() {
        estimateRepo.deleteAll();
    }

}
