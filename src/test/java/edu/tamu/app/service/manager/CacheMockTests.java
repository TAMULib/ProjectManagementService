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

import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.InternalRequest;

public abstract class CacheMockTests {

    @Value("classpath:mock/cache/active-sprints.json")
    private Resource activeSprints;

    @Value("classpath:mock/cache/remote-products.json")
    private Resource remoteProducts;

    @Value("classpath:mock/cache/internal-requests.json")
    private Resource internalRequests;

    @Spy
    private ObjectMapper objectMapper;

    // @formatter:off
    protected List<Sprint> getMockActiveSprints() throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(activeSprints.getFile(), new TypeReference<List<Sprint>>() {});
    }

    protected List<RemoteProduct> getMockRemoteProducts() throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(remoteProducts.getFile(), new TypeReference<List<RemoteProduct>>() {});
    }

    protected List<InternalRequest> getMockInternalRequests() throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(internalRequests.getFile(), new TypeReference<List<InternalRequest>>() {});
    }
    // @formatter:on

}
