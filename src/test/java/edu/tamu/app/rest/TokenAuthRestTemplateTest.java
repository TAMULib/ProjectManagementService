package edu.tamu.app.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TokenAuthRestTemplateTest {

    @Test
    public void testNewTokenAuthRestTemplate() {
        TokenAuthRestTemplate basicAuthRestTemplate = new TokenAuthRestTemplate("token");
        assertNotNull("Unable to create new basic auth rest template!", basicAuthRestTemplate);
        assertEquals("Token auth rest template did not have expected token!", "token", basicAuthRestTemplate.getToken());
    }

    @Test(expected = RuntimeException.class)
    public void testNewTokenAuthRestTemplateException() {
        new TokenAuthRestTemplate("");
    }

}
