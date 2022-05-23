package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.StatusRepo;
import edu.tamu.weaver.response.ApiResponse;

@ExtendWith(SpringExtension.class)
public class StatusControllerTest {

    private Status noneStatus;

    private Status doneStatus;

    @Mock
    private StatusRepo statusRepo;

    @InjectMocks
    private StatusController statusController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        noneStatus = new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" })));
        doneStatus = new Status("Done", new HashSet<String>(Arrays.asList(new String[] { "Done" })));

        when(statusRepo.findAll()).thenReturn(new ArrayList<Status>(Arrays.asList(new Status[] { noneStatus, doneStatus })));
        when(statusRepo.create(any(Status.class))).thenReturn(noneStatus);
        when(statusRepo.update(any(Status.class))).thenReturn(noneStatus);
        when(statusRepo.findById(any(Long.class))).thenReturn(Optional.of(noneStatus));

        doNothing().when(statusRepo).delete(any(Status.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRead() {
        ApiResponse apiResponse = statusController.read();
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Request for statuses was unsuccessful");
        assertEquals(2, ((ArrayList<Status>) apiResponse.getPayload().get("ArrayList<Status>")).size(), "Number of statuses was not correct");
    }

    @Test
    public void testReadById() {
        ApiResponse apiResponse = statusController.read(noneStatus.getId());
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Request for status was unsuccessful");
        assertEquals("None", ((Status) apiResponse.getPayload().get("Status")).getIdentifier(), "Status read was incorrect");
    }

    @Test
    public void testCreate() {
        ApiResponse apiResponse = statusController.create(noneStatus);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Status was not successfully created");
    }

    @Test
    public void testUpdate() {
        ApiResponse apiResponse = statusController.update(noneStatus);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Status was not successfully updated");
    }

    @Test
    public void testDelete() {
        ApiResponse apiResponse = statusController.delete(doneStatus);
        assertEquals(SUCCESS, apiResponse.getMeta().getStatus(), "Status was not successfully deleted");
    }

}