package edu.tamu.app.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import edu.tamu.app.ProjectApplication;
import edu.tamu.app.model.request.FeatureRequest;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class TemplateServiceTest {

    @Autowired
    private TemplateService templateService;

    @Test
    public void testCraftVersionOneXmlRequestBody() throws JsonParseException, JsonMappingException, IOException {
        String mockBody = StreamUtils.copyToString(new ClassPathResource("mock/request.xml").getInputStream(), Charset.defaultCharset());
        FeatureRequest request = new FeatureRequest("Test Request", "This is a test description!", 1L, "1000");
        String body = templateService.craftVersionOneXmlRequestBody(request);
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode value = (JsonNode) xmlMapper.readValue(body, ObjectNode.class);
        JsonNode mockValue = (JsonNode) xmlMapper.readValue(mockBody, ObjectNode.class);
        assertEquals("VersionOne XML request body was not as expected!", mockValue, value);
    }

}
