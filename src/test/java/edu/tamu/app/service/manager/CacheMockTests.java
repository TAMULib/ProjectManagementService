package edu.tamu.app.service.manager;

import java.io.IOException;
import java.util.List;

import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.model.Sprint;

public abstract class CacheMockTests {

    @Value("classpath:mock/cache/active-sprints.json")
    private Resource activeSprints;

    @Value("classpath:mock/cache/remote-projects.json")
    private Resource remoteProjects;

    @Spy
    private ObjectMapper objectMapper;

    // @formatter:off
    protected List<Sprint> getMockActiveSprints() throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(activeSprints.getFile(), new TypeReference<List<Sprint>>() {});
    }

    protected List<RemoteProject> getMockRemoteProjects() throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(remoteProjects.getFile(), new TypeReference<List<RemoteProject>>() {});
    }
    // @formatter:on

}