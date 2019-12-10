package edu.tamu.app.mock.github.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.mock.github.loader.MockGitHubLoader;

@Profile("test")
@RestController
@RequestMapping("/mock/github")
public class MockGitHubController {

    @Autowired
    private MockGitHubLoader mockGitHubLoader;

    @RequestMapping(value = "/orgs/{name}", headers = "Accept: application/vnd.github.v3+json", produces = "application/json; charset=utf-8")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody String getOrganization(@PathVariable String name) throws IOException {
        System.out.println("\n\n\nEntered controller\n\n\n");
        return mockGitHubLoader.getOrganization();
    }

}