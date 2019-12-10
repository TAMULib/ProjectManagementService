package edu.tamu.app.service.manager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHProjectCard;
import org.kohsuke.github.GHProjectColumn;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.util.ReflectionUtils;

import edu.tamu.app.ProjectApplication;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;

@SpringBootTest(classes = { ProjectApplication.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class GitHubServiceTest extends GitHubMockData {

    private static List<GHProject> testProjects;

    @Mock
    private GitHub github;

    @Mock
    private GitHubBuilder ghBuilder;

    @Mock
    private GHOrganization organization;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHRepository repository1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHRepository repository2;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHProject project1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHProject project2;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHProjectColumn column1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHProjectColumn column2;

    // Request
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHProjectCard card1;

    // Issue
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHProjectCard card2;

    // Feature
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHProjectCard card3;

    // Bug
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHProjectCard card4;

    // Unclassified
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GHProjectCard card5;

    // Request label
    @Mock
    private GHLabel label1;

    // Issue label
    @Mock
    private GHLabel label2;

    // Feature label
    @Mock
    private GHLabel label3;

    // Bug label
    @Mock
    private GHLabel label4;

    // Unused label
    @Mock
    private GHLabel label5;

    private GitHubService gitHubService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        ManagementService managementService = new RemoteProjectManager("GitHub", ServiceType.GITHUB, new HashMap<String, String>() {
            private static final long serialVersionUID = 2020874481642498006L;
            {
                put("url", "http://localhost:9101/mock/github");
                put("username", "username");
                put("password", "password");
            }
        });
        gitHubService = new GitHubService(managementService);

        Map<String, GHRepository> testRepositoryMap = Stream.of(new Object[][] {
            { TEST_REPOSITORY1_NAME, repository1 },
            { TEST_REPOSITORY2_NAME, repository2 }
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (GHRepository) data[1]));

        testProjects = new ArrayList<GHProject>(Arrays.asList(new GHProject[] { project1, project2 }));
        List<GHProjectColumn> testColumns = new ArrayList<GHProjectColumn>(Arrays.asList(new GHProjectColumn[] { column1, column2 }));
        List<GHProjectCard> testCards = new ArrayList<GHProjectCard>(Arrays.asList(new GHProjectCard[] { card1, card2, card3 }));
        List<GHLabel> testLabels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { label1, label2, label3, label4, label5 }));
        List<GHLabel> testCard1Labels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { label1, label5 }));
        List<GHLabel> testCard2Labels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { label2, label5 }));
        List<GHLabel> testCard3Labels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { label3, label5 }));
        List<GHLabel> testCard4Labels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { label4 }));
        List<GHLabel> testCard5Labels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { label5 }));

        

        doReturn(organization).when(github).getOrganization(GitHubService.ORGANIZATION);
        // when(github.getOrganization(any(String.class))).thenReturn(organization);

        when(organization.getRepositories()).thenReturn(testRepositoryMap);

        when(repository1.listProjects().asList()).thenReturn(testProjects);
        when(repository2.listProjects().asList()).thenReturn(testProjects);
        when(repository1.listLabels().asList()).thenReturn(testLabels);
        when(repository2.listLabels().asList()).thenReturn(testLabels);
        when(repository1.getId()).thenReturn(TEST_REPOSITORY1_ID);
        when(repository2.getId()).thenReturn(TEST_REPOSITORY2_ID);
        when(repository1.getName()).thenReturn(TEST_REPOSITORY1_NAME);
        when(repository2.getName()).thenReturn(TEST_REPOSITORY2_NAME);

        when(project1.listColumns().asList()).thenReturn(testColumns);
        when(project2.listColumns().asList()).thenReturn(testColumns);

        when(column1.listCards().asList()).thenReturn(testCards);
        when(column2.listCards().asList()).thenReturn(testCards);

        when(card1.getContent().getLabels()).thenReturn(testCard1Labels);
        when(card2.getContent().getLabels()).thenReturn(testCard2Labels);
        when(card3.getContent().getLabels()).thenReturn(testCard3Labels);
        when(card4.getContent().getLabels()).thenReturn(testCard4Labels);
        when(card5.getContent().getLabels()).thenReturn(testCard5Labels);
    }

    @Test
    public void testGetRemoteProjects() throws Exception {
        List<RemoteProject> remoteProjects = gitHubService.getRemoteProjects();
        assertEquals("Didn't get all the remote projects", 2, remoteProjects.size());
    }
}