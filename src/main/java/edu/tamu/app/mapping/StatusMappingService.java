package edu.tamu.app.mapping;

import org.springframework.stereotype.Service;

import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.StatusRepo;

@Service
public class StatusMappingService extends AbstractMappingService<String, Status, StatusRepo> {

    @Override
    public String handleUnmapped(String rawData) {
        return rawData;
    }

}
