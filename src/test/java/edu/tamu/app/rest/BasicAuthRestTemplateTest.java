package edu.tamu.app.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class BasicAuthRestTemplateTest {

    @Test
    public void testNewBasicAuthRestTemplate() {
        BasicAuthRestTemplate basicAuthRestTemplate = new BasicAuthRestTemplate("username", "password");
        assertNotNull("Unable to create new basic auth rest template!", basicAuthRestTemplate);
        assertEquals("Basic auth rest template did not have expected username!", "username", basicAuthRestTemplate.getUsername());
        assertEquals("Basic auth rest template did not have expected password!", "password", basicAuthRestTemplate.getPassword());
    }

    @Test(expected = RuntimeException.class)
    public void testNewBasicAuthRestTemplateException() {
        new BasicAuthRestTemplate("", "");
    }

}
