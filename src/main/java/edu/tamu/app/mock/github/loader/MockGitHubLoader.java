  
package edu.tamu.app.mock.github.loader;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
@Profile("test")
public class MockGitHubLoader {

    @Value("classpath:mock/github/organization.json")
    private Resource organization;

    public String getOrganization() throws IOException {
        System.out.println("\n\n\n" + organization.toString() + "\n\n\n");
        return StreamUtils.copyToString(organization.getInputStream(), Charset.defaultCharset());
    }

}