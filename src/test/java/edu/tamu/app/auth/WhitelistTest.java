package edu.tamu.app.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.Whitelist;

@RunWith(SpringRunner.class)
public class WhitelistTest {
    
    private static final String[] testAuthorities = { "123456789", "987654321" };
    
    @Mock
    private HttpServletRequest request;
    
    @InjectMocks
    private Whitelist whitelist;
    
    @Test
    public void testIsAllowed() {
        setField(whitelist, "whitelist", testAuthorities);
        when(request.getRemoteAddr()).thenReturn("123456789");
        boolean result = whitelist.isAllowed(request);
        assertTrue("Result was not true", result);
    }
    
    @Test
    public void testIsAllowedWithNonwhitelistedRequest() {
        setField(whitelist, "whitelist", testAuthorities);
        when(request.getRemoteAddr()).thenReturn("111111111");
        boolean result = whitelist.isAllowed(request);
        assertFalse("Result was not false", result);
    }
}
