package edu.tamu.app.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import edu.tamu.app.ProductApplication;
import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.StatusRepo;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class StatusMappingServiceTest {

    @Autowired
    private StatusRepo statusRepo;

    @Autowired
    private StatusMappingService statusMappingService;

    @BeforeEach
    public void setup() {
        statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
    }

    @Test
    public void testMap() {
        assertEquals("None", statusMappingService.map("Future"));
    }

    @Test
    public void testHandleUnmapped() {
        assertEquals("In Progress", statusMappingService.handleUnmapped("In Progress"), "Handled unmapped incorrectly!");
    }

    @AfterEach
    public void cleanup() {
        statusRepo.deleteAll();
    }

}
