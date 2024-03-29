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
import edu.tamu.app.model.CardType;
import edu.tamu.app.model.repo.CardTypeRepo;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class CardTypeMappingServiceTest {

    @Autowired
    protected CardTypeRepo cardTypeRepo;

    @Autowired
    private CardTypeMappingService cardTypeMappingService;

    @BeforeEach
    public void setup() {
        cardTypeRepo.create(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story", "Feature" }))));
    }

    @Test
    public void testMap() {
        assertEquals("Feature", cardTypeMappingService.map("Story"));
    }

    @Test
    public void testHandleUnmapped() {
        assertEquals("Defect", cardTypeMappingService.handleUnmapped("Defect"), "Handled unmapped incorrectly!");
    }

    @AfterEach
    public void cleanup() {
        cardTypeRepo.deleteAll();
    }

}
