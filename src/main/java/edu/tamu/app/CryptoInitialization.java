package edu.tamu.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.converter.CryptoConverter;

@Component
public class CryptoInitialization implements CommandLineRunner {

    @Value("${app.security.secret}")
    private String secret;

    @Override
    public void run(String... args) throws Exception {
        CryptoConverter.setKey(secret);
    }

}
