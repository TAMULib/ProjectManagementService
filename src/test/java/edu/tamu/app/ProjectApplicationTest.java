package edu.tamu.app;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProjectApplicationTest {

    @Test
    public void testContextLoads() {
        assertTrue("Project application context failed to load!", true);
    }

    @Test
    public void testProjectApplicationConfigure() {
        ProjectApplication application = new ProjectApplication();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        application.configure(builder);
    }

}
