package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.tamu.app.model.Status;
import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.StatusRepo;
import edu.tamu.weaver.response.ApiResponse;

@RunWith(SpringRunner.class)
public class StatusControllerTest {

    private Status noneStatus;

    private Status doneStatus;

    @Mock
    private StatusRepo statusRepo;

    @InjectMocks
    private StatusController statusController;

    @Before
    public void setup() throws JsonParseException, JsonMappingException, IOException {
        MockitoAnnotations.initMocks(this);
        noneStatus = new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" })));
        doneStatus = new Status("Done", new HashSet<String>(Arrays.asList(new String[] { "Done" })));

        when(statusRepo.findAll()).thenReturn(new ArrayList<Status>(Arrays.asList(new Status[] { noneStatus, doneStatus })));
        when(statusRepo.create(any(Status.class))).thenReturn(noneStatus);
        when(statusRepo.update(any(Status.class))).thenReturn(noneStatus);
        when(statusRepo.findOne(any(Long.class))).thenReturn(noneStatus);

        doNothing().when(statusRepo).delete(any(Status.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRead() {
        ApiResponse apiResponse = statusController.read();
        assertEquals("Request for statuses was unsuccessful", SUCCESS, apiResponse.getMeta().getStatus());
        assertEquals("Number of statuses was not correct", 2, ((ArrayList<User>) apiResponse.getPayload().get("ArrayList<Status>")).size());
    }

    @Test
    public void testReadById() {
        ApiResponse apiResponse = statusController.read(1L);
        assertEquals("Request for statuse was unsuccessful", SUCCESS, apiResponse.getMeta().getStatus());
        assertEquals("Statue read was incorrect", "None", ((Status) apiResponse.getPayload().get("Status")).getIdentifier());
    }

    @Test
    public void testCreate() {
        ApiResponse apiResponse = statusController.create(noneStatus);
        assertEquals("Status was not successfully updated", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testUpdate() {
        ApiResponse apiResponse = statusController.update(noneStatus);
        assertEquals("Status was not successfully updated", SUCCESS, apiResponse.getMeta().getStatus());
    }

    @Test
    public void testDelete() {
        ApiResponse apiResponse = statusController.delete(doneStatus);
        assertEquals("Status was not successfully deleted", SUCCESS, apiResponse.getMeta().getStatus());
    }

}
