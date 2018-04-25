package edu.tamu.app;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProjectInitialization.class)
public class ProjectManagementServiceTest {

    // Testing to make sure the AppContext initializes
    @Test
    public void testContext() {
        assertTrue(true);
    }

}
