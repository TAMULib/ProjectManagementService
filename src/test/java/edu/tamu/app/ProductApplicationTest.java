package edu.tamu.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(classes = { ProductApplication.class }, webEnvironment=WebEnvironment.RANDOM_PORT)
public class ProductApplicationTest {

    @Test
    public void testContextLoads() {
        assertTrue(true, "Product application context failed to load!");
    }

    @Test
    public void testProductApplicationConfigure() {
        ProductApplication application = new ProductApplication();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        application.configure(builder);
    }

}
