package edu.tamu.app.model.repo;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.ModelTest;

public abstract class RepoTest extends ModelTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected CardTypeRepo cardTypeRepo;

    @Autowired
    protected EstimateRepo estimateRepo;

    @Autowired
    protected InternalRequestRepo internalRequestRepo;

    @Autowired
    protected ProductRepo productRepo;

    @Autowired
    protected RemoteProjectManagerRepo remoteProjectManagerRepo;

    @Autowired
    protected StatusRepo statusRepo;

    protected void cleanupRepos() {
        cardTypeRepo.deleteAll();
        estimateRepo.deleteAll();
        internalRequestRepo.deleteAll();
        productRepo.deleteAll();
        remoteProjectManagerRepo.deleteAll();
        statusRepo.deleteAll();
    }

}
