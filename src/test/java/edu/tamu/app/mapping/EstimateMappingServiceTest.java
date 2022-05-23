package edu.tamu.app.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.model.Estimate;
import edu.tamu.app.model.repo.EstimateRepo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class EstimateMappingServiceTest {

    @Autowired
    protected EstimateRepo estimateRepo;

    @Autowired
    private EstimateMappingService estimateMappingService;

    @BeforeEach
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
        assertEquals(null, estimateMappingService.handleUnmapped("Unknown"), "Handled unmapped incorrectly!");
    }

    @AfterEach
    public void cleanup() {
        estimateRepo.deleteAll();
    }

}
