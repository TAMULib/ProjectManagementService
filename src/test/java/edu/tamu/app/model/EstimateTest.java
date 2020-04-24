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

import edu.tamu.app.ProductApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class EstimateTest extends ModelTest {

    @Test
    public void testCreate() {
        Estimate estimate = estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        assertNotNull("Unable to create estimate!", estimate);
        assertEquals("Estimate repo had incorrect number of estimates!", 1, estimateRepo.count());
        assertEquals("Estimate had incorrect identifier!", 1.0f, estimate.getIdentifier(), 0);
        assertEquals("Estimate had incorrect number of mappings!", 2, estimate.getMapping().size());
    }

    @Test
    public void testRead() {
        estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        assertNotNull("Unable to find estimate by identifier!", estimateRepo.findByIdentifier(1.0f));
        assertTrue("Unable to find estimate by mapping!", estimateRepo.findByMapping("Small").isPresent());
        assertTrue("Unable to find estimate by mapping!", estimateRepo.findByMapping("small").isPresent());
    }

    @Test
    public void testUpdate() {
        Estimate estimate = estimateRepo.create(new Estimate(2.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        estimate.setIdentifier(5.0f);
        estimate.setMapping(new HashSet<String>(Arrays.asList(new String[] { "Large", "large", "lg" })));
        estimate = estimateRepo.update(estimate);
        assertEquals("Estimate had incorrect identifier!", 5.0f, estimate.getIdentifier(), 0);
        assertEquals("Estimate had incorrect number of mappings!", 3, estimate.getMapping().size());
    }

    @Test
    public void testDelete() {
        Estimate estimate = estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        estimateRepo.delete(estimate);
        assertNull("Unable to delete estimate!", estimateRepo.findByIdentifier(1.0f));
        assertEquals("Estimate repo had incorrect number of estimates!", 0, estimateRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicate() {
        estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small", "small" }))));
        estimateRepo.create(new Estimate(1.0f, new HashSet<String>(Arrays.asList(new String[] { "Small" }))));
    }

}
