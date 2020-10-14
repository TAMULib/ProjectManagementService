package edu.tamu.app;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment=WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ProductApplicationTest {

    @Test
    public void testContextLoads() {
        assertTrue("Product application context failed to load!", true);
    }

    @Test
    public void testProductApplicationConfigure() {
        ProductApplication application = new ProductApplication();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        application.configure(builder);
    }

}
