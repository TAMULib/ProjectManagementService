package edu.tamu.app.mock.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@Profile("test")
@RequestMapping("/TexasAMLibrary")
public class MockVersionOne {

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/rest-1.v1/Data/Request", headers = "Accept=application/xml", method = RequestMethod.POST, produces = "application/xml")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody JsonNode pushRequest() throws JsonProcessingException, IOException {
        return objectMapper.readTree(new ClassPathResource("mock/response.json").getInputStream());
    }

}
