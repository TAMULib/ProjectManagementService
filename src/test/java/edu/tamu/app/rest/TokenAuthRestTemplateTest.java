package edu.tamu.app.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TokenAuthRestTemplateTest {

    @Test
    public void testNewTokenAuthRestTemplate() {
        TokenAuthRestTemplate basicAuthRestTemplate = new TokenAuthRestTemplate("token");
        assertNotNull(basicAuthRestTemplate, "Unable to create new basic auth rest template!");
        assertEquals("token", basicAuthRestTemplate.getToken(), "Token auth rest template did not have expected token!");
    }

    @Test
    public void testNewTokenAuthRestTemplateException() {
        assertThrows(RuntimeException.class, () -> {
            new TokenAuthRestTemplate("");
        });
    }

}
