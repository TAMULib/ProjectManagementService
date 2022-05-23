package edu.tamu.app.model;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import edu.tamu.app.cache.service.ActiveSprintsScheduledCacheService;
import edu.tamu.app.cache.service.ProductsStatsScheduledCacheService;
import edu.tamu.app.cache.service.RemoteProjectsScheduledCacheService;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.request.TicketRequest;
import edu.tamu.app.service.manager.GitHubProjectService;
import edu.tamu.app.service.manager.VersionOneService;
import edu.tamu.app.service.ticketing.SugarService;

public abstract class AbstractModelTest {

    protected static final float TEST_ESTIMATE_IDENTIFIER1 = 1.0f;
    protected static final float TEST_ESTIMATE_IDENTIFIER2 = 2.2f;

    protected static final String TEST_CARD_TYPE_IDENTIFIER1 = "Test Card Type Identifier 1";
    protected static final String TEST_CARD_TYPE_IDENTIFIER2 = "Test Card Type Identifier 2";

    protected static final String TEST_CARD_TYPE_MATCH1 = "Test Card Type Match 1";
    protected static final String TEST_CARD_TYPE_MATCH2 = "Test Card Type Match 2";

    protected static final String TEST_ESTIMATE_MATCH1 = "Test Estimate Match 1";
    protected static final String TEST_ESTIMATE_MATCH2 = "Test Estimate Match 2";

    protected static final String TEST_PRODUCT_NAME1 = "Test Product Name";

    protected static final String TEST_PRODUCT_NAME_ALTERNATE1 = "Alternate Product Name";

    protected static final String TEST_REMOTE_PROJECT_MANAGER_NAME1 = "Test Remote Project Manager 1";
    protected static final String TEST_REMOTE_PROJECT_MANAGER_NAME2 = "Test Remote Project Manager 2";

    protected static final String TEST_REMOTE_PROJECT_MANAGER_NAME_ALTERNATE1 = "Alternate Remote Project Manager";

    protected static final String TEST_PROJECT_SCOPE1 = "0010";
    protected static final String TEST_PROJECT_SCOPE2 = "0011";
    protected static final String TEST_PROJECT_SCOPE3 = "0020";

    protected static final String TEST_PROJECT_URL1 = "http://localhost/1";
    protected static final String TEST_PROJECT_URL2 = "http://localhost/2";

    protected static final String TEST_PROJECT_TOKEN1 = "0123456789";
    protected static final String TEST_PROJECT_TOKEN2 = "9876543210";

    protected static final String TEST_INTERNAL_REQUEST_TITLE1 = "Test Internal Request Title 1";
    protected static final String TEST_INTERNAL_REQUEST_TITLE2 = "Test Internal Request Title 2";

    protected static final String TEST_INTERNAL_REQUEST_DESCRIPTION1 = "Test Internal Request Description 1";
    protected static final String TEST_INTERNAL_REQUEST_DESCRIPTION2 = "Test Internal Request Description 2";

    protected static final String TEST_DEV_URL1 = "http://localhost/dev1/";
    protected static final String TEST_DEV_URL2 = "http://localhost/dev2/";

    protected static final String TEST_PRE_URL1 = "http://localhost/pre1/";
    protected static final String TEST_PRE_URL2 = "http://localhost/pre2/";

    protected static final String TEST_PROD_URL1 = "http://localhost/production1/";
    protected static final String TEST_PROD_URL2 = "http://localhost/production2/";

    protected static final String TEST_WIKI_URL1 = "http://localhost/wiki1/";
    protected static final String TEST_WIKI_URL2 = "http://localhost/wiki2/";

    protected static final String TEST_OTHER_URL1 = "http://localhost/other1/";
    protected static final String TEST_OTHER_URL2 = "http://localhost/other2/";

    protected static final Date TEST_INTERNAL_REQUEST_CREATED_ON1 = new Date();
    protected static final Date TEST_INTERNAL_REQUEST_CREATED_ON2 = new Date(System.currentTimeMillis() + 100);

    protected static final GHRepository TEST_GITHUB_REPOSITORY1 = mock(GHRepository.class, RETURNS_DEEP_STUBS);
    protected static final GHRepository TEST_GITHUB_REPOSITORY2 = mock(GHRepository.class, RETURNS_DEEP_STUBS);

    protected static final GHOrganization TEST_GITHUB_ORGANIZATION1 = mock(GHOrganization.class, RETURNS_DEEP_STUBS);

    protected void mockSugarService(SugarService sugarService) {
        when(sugarService.submit(any(TicketRequest.class))).thenReturn("Successfully submitted issue for test service!");
    }

    protected void mockGitHubService(GitHubProjectService gitHubService, GitHubBuilder gitHubBuilder) {
        GitHub gitHub = mock(GitHub.class);

        try {
            when(gitHubBuilder.withEndpoint(any(String.class))).thenReturn(gitHubBuilder);
            when(gitHubBuilder.withOAuthToken(any(String.class))).thenReturn(gitHubBuilder);
            when(gitHubBuilder.build()).thenReturn(gitHub);
            when(gitHubService.push(any(FeatureRequest.class))).thenReturn("1L");

            when(gitHub.getOrganization(any(String.class))).thenReturn(TEST_GITHUB_ORGANIZATION1);
            when(gitHub.getRepositoryById(any(String.class))).thenReturn(TEST_GITHUB_REPOSITORY1);
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

}
