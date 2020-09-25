package edu.tamu.app.controller.integration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.Product;
import edu.tamu.app.model.repo.CardTypeRepo;
import edu.tamu.app.model.repo.EstimateRepo;
import edu.tamu.app.model.repo.InternalRequestRepo;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProjectManagerRepo;
import edu.tamu.app.model.repo.StatusRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.manager.GitHubService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

public abstract class IntegrationTest {

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

    protected void mockSugarService(SugarService sugarService) {
        when(sugarService.submit(any(TicketRequest.class))).thenReturn("Successfully submitted issue for test service!");
    }

    protected void mockGitHubService(GitHubService gitHubService, GitHubBuilder gitHubBuilder) {
        GitHub gitHub = mock(GitHub.class);

        try {
            when(gitHubBuilder.withEndpoint(any(String.class))).thenReturn(gitHubBuilder);
            when(gitHubBuilder.withOAuthToken(any(String.class))).thenReturn(gitHubBuilder);
            when(gitHubBuilder.build()).thenReturn(gitHub);
            when(gitHubService.push(any(FeatureRequest.class))).thenReturn("1L");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void mockVersionOneService(VersionOneService versionOneService) {
        try {
            when(versionOneService.push(any(FeatureRequest.class))).thenReturn("token");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void mockActiveSprintsScheduledCacheService(ActiveSprintsScheduledCacheService activeSprintsScheduledCacheService) {
        doNothing().when(activeSprintsScheduledCacheService).addProduct(any(Product.class));
        doNothing().when(activeSprintsScheduledCacheService).updateProduct(any(Product.class));
        doNothing().when(activeSprintsScheduledCacheService).removeProduct(any(Product.class));
        doNothing().when(activeSprintsScheduledCacheService).update();
        doNothing().when(activeSprintsScheduledCacheService).broadcast();
    }

    protected void mockProductsStatsScheduledCacheService(ProductsStatsScheduledCacheService productsStatsScheduledCacheService) {
        doNothing().when(productsStatsScheduledCacheService).addProduct(any(Product.class));
        doNothing().when(productsStatsScheduledCacheService).updateProduct(any(Product.class));
        doNothing().when(productsStatsScheduledCacheService).removeProduct(any(Product.class));
        doNothing().when(productsStatsScheduledCacheService).update();
        doNothing().when(productsStatsScheduledCacheService).broadcast();
    }

    protected void mockRemoteProjectsScheduledCacheService(RemoteProjectsScheduledCacheService remoteProjectsScheduledCacheService) {
        doNothing().when(remoteProjectsScheduledCacheService).update();
        doNothing().when(remoteProjectsScheduledCacheService).broadcast();
    }

    protected void cleanupRepos() {
        cardTypeRepo.deleteAll();
        estimateRepo.deleteAll();
        internalRequestRepo.deleteAll();
        productRepo.deleteAll();
        remoteProjectManagerRepo.deleteAll();
        statusRepo.deleteAll();
    }

}
