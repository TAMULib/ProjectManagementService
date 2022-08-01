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
import edu.tamu.app.model.CardType;
import edu.tamu.app.model.repo.CardTypeRepo;

@ExtendWith(SpringExtension.class)
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
        assertEquals(cardTypeMappingService.map("Story"), "Feature");
    }

    @Test
    public void testHandleUnmapped() {
        assertEquals(cardTypeMappingService.handleUnmapped("Defect"), "Handled unmapped incorrectly!", "Defect");
    }

    @AfterEach
    public void cleanup() {
        cardTypeRepo.deleteAll();
    }

}
