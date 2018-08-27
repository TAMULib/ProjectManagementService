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
import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.StatusRepo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class StatusMappingServiceTest {

    @Autowired
    private StatusRepo statusRepo;

    @Autowired
    private StatusMappingService statusMappingService;

    @Before
    public void setup() {
        statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
    }

    @Test
    public void testMap() {
        assertEquals("None", statusMappingService.map("Future"));
    }

    @Test
    public void testHandleUnmapped() {
        assertEquals("Handled unmapped incorrectly!", "In Progress", statusMappingService.handleUnmapped("In Progress"));
    }

    @After
    public void cleanup() {
        statusRepo.deleteAll();
    }

}
