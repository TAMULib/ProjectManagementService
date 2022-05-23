package edu.tamu.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Web server initialization.
 */
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = { "edu.tamu.*" })
public class ProductApplication extends SpringBootServletInitializer {

    /**
     * Entry point to the application from within servlet.
     *
     * @param args
     *            String[]
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

    /**
     * Entry point to the application if run using spring-boot:run.
     *
     * @param application
     *            SpringApplicationBuilder
     * @return SpringApplicationBuilder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ProductApplication.class);
    }

}