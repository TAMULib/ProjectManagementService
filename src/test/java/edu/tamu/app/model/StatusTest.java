package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.ProjectApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class StatusTest extends ModelTest {

    @Test
    public void testCreate() {
        Status status = statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        assertNotNull("Unable to create status!", status);
        assertEquals("Status repo had incorrect number of statuses!", 1, statusRepo.count());
        assertEquals("Status had incorrect identifier!", "None", status.getIdentifier());
        assertEquals("Status had incorrect number of mappings!", 2, status.getMapping().size());
    }

    @Test
    public void testRead() {
        statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        assertNotNull("Unable to find status by identifier!", statusRepo.findByIdentifier("None"));
        assertTrue("Unable to find status by mapping!", statusRepo.findByMapping("None").isPresent());
        assertTrue("Unable to find status by mapping!", statusRepo.findByMapping("Future").isPresent());
    }

    @Test
    public void testUpdate() {
        Status status = statusRepo.create(new Status("Unaivable", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        status.setIdentifier("None");
        status.setMapping(new HashSet<String>(Arrays.asList(new String[] { "None", "Future", "NA" })));
        status = statusRepo.update(status);
        assertEquals("Status had incorrect identifier!", "None", status.getIdentifier());
        assertEquals("Status had incorrect number of mappings!", 3, status.getMapping().size());
    }

    @Test
    public void testDelete() {
        Status status = statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        statusRepo.delete(status);
        assertNull("Unable to delete status!", statusRepo.findByIdentifier("None"));
        assertEquals("Status repo had incorrect number of statuses!", 0, statusRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicate() {
        statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        statusRepo.create(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
    }

}
