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
public class CardTypeTest extends ModelTest {

    @Test
    public void testCreate() {
        CardType cardType = cardTypeRepo.create(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story", "Feature" }))));
        assertNotNull("Unable to create card type!", cardType);
        assertEquals("Card type repo had incorrect number of card types!", 1, cardTypeRepo.count());
        assertEquals("Card type had incorrect identifier!", "Feature", cardType.getIdentifier());
        assertEquals("Card type had incorrect number of mappings!", 2, cardType.getMapping().size());
    }

    @Test
    public void testRead() {
        cardTypeRepo.create(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
        assertNotNull("Unable to find card type by identifier!", cardTypeRepo.findByIdentifier("Feature"));
        assertTrue("Unable to find card type by mapping!", cardTypeRepo.findByMapping("None").isPresent());
        assertTrue("Unable to find card type by mapping!", cardTypeRepo.findByMapping("Future").isPresent());
    }

    @Test
    public void testUpdate() {
        CardType cardType = cardTypeRepo.create(new CardType("Story", new HashSet<String>(Arrays.asList(new String[] { "Story" }))));
        cardType.setIdentifier("Feature");
        cardType.setMapping(new HashSet<String>(Arrays.asList(new String[] { "Feature", "Story", "Task" })));
        cardType = cardTypeRepo.update(cardType);
        assertEquals("Card type had incorrect identifier!", "Feature", cardType.getIdentifier());
        assertEquals("Card type had incorrect number of mappings!", 3, cardType.getMapping().size());
    }

    @Test
    public void testDelete() {
        CardType cardType = cardTypeRepo.create(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story", "Feature" }))));
        cardTypeRepo.delete(cardType);
        assertNull("Unable to delete card type!", cardTypeRepo.findByIdentifier("Feature"));
        assertEquals("Card type repo had incorrect number of card types!", 0, cardTypeRepo.count());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicate() {
        cardTypeRepo.create(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story", "Feature" }))));
        cardTypeRepo.create(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story" }))));
    }

}
