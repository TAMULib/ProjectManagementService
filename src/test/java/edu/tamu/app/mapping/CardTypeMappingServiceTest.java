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
import edu.tamu.app.model.CardType;
import edu.tamu.app.model.repo.CardTypeRepo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class CardTypeMappingServiceTest {

    @Autowired
    protected CardTypeRepo cardTypeRepo;

    @Autowired
    private CardTypeMappingService cardTypeMappingService;

    @Before
    public void setup() {
        cardTypeRepo.create(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story", "Feature" }))));
    }

    @Test
    public void testMap() {
        assertEquals("Feature", cardTypeMappingService.map("Story"));
    }

    @Test
    public void testHandleUnmapped() {
        assertEquals("Handled unmapped incorrectly!", "Defect", cardTypeMappingService.handleUnmapped("Defect"));
    }

    @After
    public void cleanup() {
        cardTypeRepo.deleteAll();
    }

}
