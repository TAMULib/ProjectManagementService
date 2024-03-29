package edu.tamu.app.service.manager;

import static edu.tamu.app.service.manager.AbstractGitHubService.DEFECT_LABEL;
import static edu.tamu.app.service.manager.AbstractGitHubService.FEATURE_LABEL;
import static edu.tamu.app.service.manager.AbstractGitHubService.ISSUE_LABEL;
import static edu.tamu.app.service.manager.AbstractGitHubService.REQUEST_LABEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHProject.ProjectStateFilter;
import org.kohsuke.github.GHProjectCard;
import org.kohsuke.github.GHProjectColumn;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.RemoteProject;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.mapping.CardTypeMappingService;
import edu.tamu.app.mapping.EstimateMappingService;
import edu.tamu.app.mapping.StatusMappingService;
import edu.tamu.app.model.CardType;
import edu.tamu.app.model.Estimate;
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.CardTypeRepo;
import edu.tamu.app.model.repo.EstimateRepo;
import edu.tamu.app.model.repo.StatusRepo;
import edu.tamu.app.model.request.FeatureRequest;

@ExtendWith(MockitoExtension.class)
public class GitHubProjectServiceTest extends CacheMockTests {

    private static final String TEST_REPOSITORY1_NAME = "Test repository 1 name";
    private static final String TEST_REPOSITORY2_NAME = "Test repository 2 name";
    private static final String TEST_UNUSED_LABEL_NAME = "unused";
    private static final String TEST_FEATURE_REQUEST_TITLE = "Feature request";
    private static final String TEST_FEATURE_REQUEST_DESCRIPTION = "Description of feature request";
    private static final String TEST_USER1_NAME = "User 1 name";
    private static final String TEST_USER1_AVATAR_PATH = "https://avatars2.githubusercontent.com/u/1234567?v=4";
    private static final String TEST_USER2_AVATAR_PATH = "https://avatars2.githubusercontent.com/u/2222222?v=4";
    private static final String TEST_USER3_AVATAR_PATH = "https://avatars2.githubusercontent.com/u/3333333?v=4";
    private static final String TEST_USER1_AVATAR_NAME = "1234567";
    private static final String TEST_COLUMN1_NAME = "Test Column 1";
    private static final Long TEST_REPOSITORY1_ID = 1L;
    private static final Long TEST_REPOSITORY2_ID = 2L;
    private static final Long TEST_USER1_ID = 3L;
    private static final Long TEST_PRODUCT1_ID = 4L;
    private static final String TEST_PROJECT1_URL = "http://localhost/1";
    private static final String TEST_PROJECT1_TOKEN = "0123456789";

    @Mock private GHLabel testLabel1;
    @Mock private GHLabel testLabel2;
    @Mock private GHLabel testLabel3;
    @Mock private GHLabel testLabel4;
    @Mock private GHLabel testLabel5;
    @Mock private GHIssue testIssue1;
    @Mock private GHIssue testIssue2;
    @Mock private GHIssue testIssue3;
    @Mock private GHIssue testIssue4;
    @Mock private GHIssue testIssue5;
    @Mock private GHUser testUser1;
    @Mock private GHUser testUser2;
    @Mock private GHUser testUser3;
    @Mock private GHMilestone testMilestone;
    @Mock private GHProjectCard testCard1;
    @Mock private GHProjectCard testCard2;
    @Mock private GHProjectCard testCard3;
    @Mock private GHProjectCard testCard4;
    @Mock private GHProjectCard testCard5;
    @Mock private PagedIterable<GHProjectCard> cardIterable1;
    @Mock private PagedIterable<GHProjectCard> cardIterable2;
    @Mock private PagedIterable<GHProjectCard> cardIterable3;
    @Mock private GHProjectColumn testColumn1;
    @Mock private GHProjectColumn testColumn2;
    @Mock private GHProjectColumn testColumn3;
    @Mock private PagedIterable<GHProjectColumn> columnIterable;
    @Mock private GHProject testProject1;
    @Mock private GHProject testProject2;
    @Mock private GHProject testProject3;
    @Mock private PagedIterable<GHLabel> labelIterable;
    @Mock private GHIssueBuilder issueBuilder;
    @Mock private GHRepository testRepository1;
    @Mock private GHRepository testRepository2;
    @Mock private PagedIterable<GHProject> projectIterable;
    @Mock private GHOrganization testOrganization;
    @Mock private FeatureRequest testFeatureRequest;
    @Mock private RestTemplate restTemplate;
    @Mock private ResponseEntity<byte[]> response;
    @Mock private CardTypeRepo cardTypeRepo;
    @Mock private StatusRepo statusRepo;
    @Mock private EstimateRepo estimateRepo;
    @Mock private GitHubBuilder ghBuilder;
    @Mock private GitHub github;

    @Mock(answer = Answers.CALLS_REAL_METHODS) private GitHubProjectService gitHubProjectService;
    @Mock(answer = Answers.CALLS_REAL_METHODS) private CardTypeMappingService cardTypeMappingService;
    @Mock(answer = Answers.CALLS_REAL_METHODS) private StatusMappingService statusMappingService;
    @Mock(answer = Answers.CALLS_REAL_METHODS) private EstimateMappingService estimateMappingService;

    @BeforeEach
    public void setup() throws Exception {
        ManagementService managementService = new RemoteProjectManager("GitHub", ServiceType.GITHUB_MILESTONE, TEST_PROJECT1_URL, TEST_PROJECT1_TOKEN);

        Map<String, GHRepository> testRepositoryMap = Stream.of(
            new Object[][] { { TEST_REPOSITORY1_NAME, testRepository1 }, { TEST_REPOSITORY2_NAME, testRepository2 } })
            .collect(Collectors.toMap(data -> (String) data[0], data -> (GHRepository) data[1]));

        List<GHLabel> allTestLabels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { testLabel1, testLabel2, testLabel3, testLabel4, testLabel5 }));
        List<GHLabel> testCard1Labels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { testLabel1, testLabel5 }));
        List<GHLabel> testCard2Labels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { testLabel2, testLabel5 }));
        List<GHLabel> testCard3Labels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { testLabel3, testLabel5 }));
        List<GHLabel> testCard4Labels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { testLabel4 }));
        List<GHLabel> testCard5Labels = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { testLabel5 }));

        List<GHUser> testUsers1 = new ArrayList<GHUser>(Arrays.asList(new GHUser[] { testUser1 }));
        List<GHUser> testUsers2 = new ArrayList<GHUser>(Arrays.asList(new GHUser[] { testUser1, testUser2 }));
        List<GHUser> testUsers3 = new ArrayList<GHUser>(Arrays.asList(new GHUser[] {}));
        List<GHUser> testUsers4 = new ArrayList<GHUser>(Arrays.asList(new GHUser[] { testUser2 }));
        List<GHUser> testUsers5 = new ArrayList<GHUser>(Arrays.asList(new GHUser[] { testUser3, testUser1 }));

        List<GHProjectCard> testColumn1Cards = new ArrayList<GHProjectCard>(Arrays.asList(new GHProjectCard[] { testCard1, testCard2, testCard3 }));
        List<GHProjectCard> testColumn2Cards = new ArrayList<GHProjectCard>(Arrays.asList(new GHProjectCard[] { testCard3, testCard4 }));
        List<GHProjectCard> testColumn3Cards = new ArrayList<GHProjectCard>(Arrays.asList(new GHProjectCard[] { testCard5 }));

        List<GHIssue> testIssueList = new ArrayList<GHIssue>(Arrays.asList((new GHIssue[] { testIssue1, testIssue2, testIssue3, testIssue4, testIssue5 })));
        List<GHProjectColumn> testProjectColumns = new ArrayList<GHProjectColumn>(Arrays.asList(new GHProjectColumn[] { testColumn1, testColumn2, testColumn3 }));
        List<GHProject> testProjects = new ArrayList<GHProject>(Arrays.asList(new GHProject[] { testProject1, testProject2, testProject3 }));

        setField(cardTypeMappingService, "serviceMappingRepo", cardTypeRepo);
        setField(statusMappingService, "serviceMappingRepo", statusRepo);
        setField(estimateMappingService, "serviceMappingRepo", estimateRepo);

        setField(gitHubProjectService, "ghBuilder", ghBuilder);
        setField(gitHubProjectService, "managementService", managementService);
        setField(gitHubProjectService, "cardTypeMappingService", cardTypeMappingService);
        setField(gitHubProjectService, "statusMappingService", statusMappingService);
        setField(gitHubProjectService, "estimateMappingService", estimateMappingService);
        setField(gitHubProjectService, "github", github);
        setField(gitHubProjectService, "members", new HashMap<String, Member>());
        setField(gitHubProjectService, "restTemplate", restTemplate);

        lenient().when(testOrganization.getRepositories()).thenReturn(testRepositoryMap);

        lenient().when(testOrganization.listProjects(any(ProjectStateFilter.class))).thenReturn(projectIterable);

        lenient().when(testRepository1.getId()).thenReturn(TEST_REPOSITORY1_ID);
        lenient().when(testRepository1.getName()).thenReturn(TEST_REPOSITORY1_NAME);
        lenient().when(testRepository1.listProjects(any(ProjectStateFilter.class))).thenReturn(projectIterable);
        lenient().when(testRepository1.listProjects()).thenReturn(projectIterable);
        lenient().when(testRepository1.listLabels()).thenReturn(labelIterable);
        lenient().when(testRepository1.getIssues(any(GHIssueState.class))).thenReturn(testIssueList);

        lenient().when(testRepository1.createIssue(any(String.class))).thenReturn(issueBuilder);
        lenient().when(issueBuilder.body(any(String.class))).thenReturn(issueBuilder);
        lenient().when(issueBuilder.create()).thenReturn(testIssue1);

        lenient().when(testRepository2.getId()).thenReturn(TEST_REPOSITORY2_ID);
        lenient().when(testRepository2.getName()).thenReturn(TEST_REPOSITORY2_NAME);
        lenient().when(testRepository2.getIssues(any(GHIssueState.class))).thenReturn(testIssueList);
        lenient().when(testRepository2.listProjects()).thenReturn(projectIterable);
        lenient().when(testRepository2.listLabels()).thenReturn(labelIterable);

        lenient().when(testProject1.listColumns()).thenReturn(columnIterable);
        lenient().when(testProject2.listColumns()).thenReturn(columnIterable);
        lenient().when(testProject3.listColumns()).thenReturn(columnIterable);

        lenient().when(testProject1.getName()).thenReturn(AbstractGitHubService.SPRINT);
        lenient().when(testProject2.getName()).thenReturn(AbstractGitHubService.SPRINT);
        lenient().when(testProject3.getName()).thenReturn(AbstractGitHubService.SPRINT);

        lenient().when(testColumn1.listCards()).thenReturn(cardIterable1);
        lenient().when(testColumn2.listCards()).thenReturn(cardIterable2);
        lenient().when(testColumn3.listCards()).thenReturn(cardIterable3);

        lenient().when(testCard1.getId()).thenReturn(1L);
        lenient().when(testCard2.getId()).thenReturn(2L);
        lenient().when(testCard3.getId()).thenReturn(3L);
        lenient().when(testCard4.getId()).thenReturn(4L);
        lenient().when(testCard5.getId()).thenReturn(5L);

        lenient().when(testCard1.getContent()).thenReturn(testIssue1);
        lenient().when(testCard2.getContent()).thenReturn(testIssue2);
        lenient().when(testCard3.getContent()).thenReturn(testIssue3);
        lenient().when(testCard4.getContent()).thenReturn(testIssue4);
        lenient().when(testCard5.getContent()).thenReturn(testIssue5);

        lenient().when(testCard1.getColumn()).thenReturn(testColumn1);
        lenient().when(testCard2.getColumn()).thenReturn(testColumn1);
        lenient().when(testCard3.getColumn()).thenReturn(testColumn1);
        lenient().when(testCard4.getColumn()).thenReturn(testColumn1);
        lenient().when(testCard5.getColumn()).thenReturn(testColumn1);

        lenient().when(testIssue1.getLabels()).thenReturn(testCard1Labels);
        lenient().when(testIssue2.getLabels()).thenReturn(testCard2Labels);
        lenient().when(testIssue3.getLabels()).thenReturn(testCard3Labels);
        lenient().when(testIssue4.getLabels()).thenReturn(testCard4Labels);
        lenient().when(testIssue5.getLabels()).thenReturn(testCard5Labels);
        lenient().when(testIssue1.getAssignees()).thenReturn(testUsers1);

        lenient().when(testColumn1.getName()).thenReturn(TEST_COLUMN1_NAME);

        lenient().when(testCard2.getContent().getLabels()).thenReturn(testCard2Labels);
        lenient().when(testCard3.getContent().getLabels()).thenReturn(testCard3Labels);
        lenient().when(testCard4.getContent().getLabels()).thenReturn(testCard4Labels);
        lenient().when(testCard5.getContent().getLabels()).thenReturn(testCard5Labels);
        lenient().when(testCard2.getContent().getAssignees()).thenReturn(testUsers2);
        lenient().when(testCard3.getContent().getAssignees()).thenReturn(testUsers3);
        lenient().when(testCard4.getContent().getAssignees()).thenReturn(testUsers4);
        lenient().when(testCard5.getContent().getAssignees()).thenReturn(testUsers5);

        lenient().when(testUser1.getId()).thenReturn(TEST_USER1_ID);
        lenient().when(testUser1.getName()).thenReturn(TEST_USER1_NAME);
        lenient().when(testUser1.getAvatarUrl()).thenReturn(TEST_USER1_AVATAR_PATH);
        lenient().when(testUser2.getAvatarUrl()).thenReturn(TEST_USER2_AVATAR_PATH);
        lenient().when(testUser3.getAvatarUrl()).thenReturn(TEST_USER3_AVATAR_PATH);

        lenient().when(testLabel1.getName()).thenReturn(REQUEST_LABEL);
        lenient().when(testLabel2.getName()).thenReturn(ISSUE_LABEL);
        lenient().when(testLabel3.getName()).thenReturn(FEATURE_LABEL);
        lenient().when(testLabel4.getName()).thenReturn(DEFECT_LABEL);
        lenient().when(testLabel5.getName()).thenReturn(TEST_UNUSED_LABEL_NAME);

        lenient().when(testFeatureRequest.getProductId()).thenReturn(TEST_PRODUCT1_ID);
        lenient().when(testFeatureRequest.getTitle()).thenReturn(TEST_FEATURE_REQUEST_TITLE);
        lenient().when(testFeatureRequest.getDescription()).thenReturn(TEST_FEATURE_REQUEST_DESCRIPTION);

        lenient().when(labelIterable.toList()).thenReturn(allTestLabels);
        lenient().when(projectIterable.toList()).thenReturn(testProjects);
        lenient().when(columnIterable.toList()).thenReturn(testProjectColumns);
        lenient().when(cardIterable1.toList()).thenReturn(testColumn1Cards);
        lenient().when(cardIterable2.toList()).thenReturn(testColumn2Cards);
        lenient().when(cardIterable3.toList()).thenReturn(testColumn3Cards);

        lenient().when(restTemplate.exchange(
            any(String.class),
            any(HttpMethod.class),
            Mockito.<HttpEntity<String>>any(),
            Mockito.<Class<byte[]>>any()))
                .thenReturn(response);

        lenient().when(response.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);

        lenient().when(cardTypeRepo.findByMapping(any(String.class))).thenAnswer(new Answer<Optional<CardType>>() {
            @Override
            public Optional<CardType> answer(InvocationOnMock invocation) {
                String identifier = (String) invocation.getArguments()[0];
                Optional<CardType> cardType = Optional.empty();
                switch (identifier) {
                case "Story":
                    cardType = Optional
                            .of(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story" }))));
                    break;
                case "Defect":
                    cardType = Optional
                            .of(new CardType("Defect", new HashSet<String>(Arrays.asList(new String[] { "Defect" }))));
                    break;
                }
                return cardType;
            }
        });

        lenient().when(statusRepo.findByMapping(any(String.class))).thenAnswer(new Answer<Optional<Status>>() {
            @Override
            public Optional<Status> answer(InvocationOnMock invocation) {
                String identifier = (String) invocation.getArguments()[0];
                Optional<Status> status = Optional.empty();
                switch (identifier) {
                case "None":
                case "Feature":
                    status = Optional.of(
                            new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
                    break;
                case "In Progress":
                    status = Optional.of(new Status("In Progress",
                            new HashSet<String>(Arrays.asList(new String[] { "In Progress" }))));
                    break;
                case "Done":
                    status = Optional
                            .of(new Status("Done", new HashSet<String>(Arrays.asList(new String[] { "Done" }))));
                    break;
                case "Accepted":
                    status = Optional.of(
                            new Status("Accepted", new HashSet<String>(Arrays.asList(new String[] { "Accepted" }))));
                    break;
                }
                return status;
            }
        });

        lenient().when(estimateRepo.findByMapping(any(String.class))).thenAnswer(new Answer<Optional<Estimate>>() {
            @Override
            public Optional<Estimate> answer(InvocationOnMock invocation) {
                return Optional.empty();
            }
        });

        lenient().when(statusRepo.findByIdentifier(any(String.class)))
            .thenReturn(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
    }

    @Test
    public void testGetRemoteProjects() throws Exception {
        when(github.getOrganization(any(String.class))).thenReturn(testOrganization);

        List<RemoteProject> remoteProjects = gitHubProjectService.getRemoteProject();
        assertEquals(2, remoteProjects.size(), "Didn't get all the remote projects");
        assertEquals(1, remoteProjects.get(0).getRequestCount(), "Number of Requests was incorrect");
        assertEquals(2, remoteProjects.get(0).getIssueCount(), "Number of Issues was incorrect");
        assertEquals(1, remoteProjects.get(0).getFeatureCount(), "Number of Features was incorrect");
        assertEquals(1, remoteProjects.get(0).getDefectCount(), "Number of Defects was incorrect");
    }

    @Test
    public void testGetRemoteProjectByScopeId() throws Exception {
        when(github.getRepositoryById(any(String.class))).thenReturn(testRepository1);

        RemoteProject project = gitHubProjectService.getRemoteProjectByScopeId(String.valueOf(TEST_REPOSITORY1_ID));
        assertNotNull(project, "Didn't get the remote project");
        assertEquals(String.valueOf(TEST_REPOSITORY1_ID), project.getId(), "Did not get the expected project");
        assertEquals(1, project.getRequestCount(), "Number of Requests was incorrect");
        assertEquals(2, project.getIssueCount(), "Number of Issues was incorrect");
        assertEquals(1, project.getFeatureCount(), "Number of Features was incorrect");
        assertEquals(1, project.getDefectCount(), "Number of Defects was incorrect");
    }

    @Test
    public void testGetActiveSprintsByProjectId() throws Exception {
        when(github.getRepositoryById(any(String.class))).thenReturn(testRepository1);

        List<Sprint> activeSprints = gitHubProjectService.getActiveSprintsByScopeId(String.valueOf(TEST_REPOSITORY1_ID));
        assertEquals(3, activeSprints.size(), "Didn't get all active sprints");
    }

    @Test
    public void testGetAdditionalActiveSprints() throws Exception {
        when(github.getOrganization(any(String.class))).thenReturn(testOrganization);

        List<Sprint> additionalSprints = gitHubProjectService.getAdditionalActiveSprints();
        assertEquals(3, additionalSprints.size(), "Didn't get all additional sprints");
    }

    @Test
    public void testGetActiveSprintsByProjectIdType() throws Exception {
        when(github.getRepositoryById(any(String.class))).thenReturn(testRepository1);

        List<Sprint> sprints = gitHubProjectService.getActiveSprintsByScopeId(String.valueOf(TEST_REPOSITORY1_ID));

        sprints.forEach(sprint -> {
            assertEquals(ServiceType.GITHUB_PROJECT.toString(), sprint.getType(), "Didn't get the correct Service Type for the Sprint");
        });
    }

    @Test
    public void testGetAdditionalActiveSprintsType() throws Exception {
        when(github.getOrganization(any(String.class))).thenReturn(testOrganization);

        List<Sprint> sprints = gitHubProjectService.getAdditionalActiveSprints();

        sprints.forEach(sprint -> {
            assertEquals(ServiceType.GITHUB_PROJECT.toString(), sprint.getType(), "Didn't get the correct Service Type for the Sprint");
        });
    }

    @Test
    public void testPush() throws Exception {
        when(github.getRepositoryById(any(String.class))).thenReturn(testRepository1);

        String id = gitHubProjectService.push(testFeatureRequest);
        assertNotNull(id);
    }

    @Test
    public void testGetMember() throws IOException {
        Member member = gitHubProjectService.getMember(testUser1);
        assertEquals(String.valueOf(TEST_USER1_ID), member.getId(), "Member ID is incorrect");
        assertEquals(TEST_USER1_NAME, member.getName(), "Member Name is incorrect");
        assertEquals(TEST_USER1_AVATAR_NAME, member.getAvatar(), "Member Avatar URL is incorrect");
    }

    @Test
    public void testGetGitHubInstanceWithInvalidServiceEndpoint() throws IOException {
        when(ghBuilder.withEndpoint(any(String.class))).thenReturn(ghBuilder);
        when(ghBuilder.withOAuthToken(any(String.class))).thenReturn(ghBuilder);
        when(ghBuilder.build()).thenReturn(github);

        ManagementService invalidManagementService = new RemoteProjectManager("GitHub", ServiceType.GITHUB_PROJECT, TEST_PROJECT1_URL, TEST_PROJECT1_TOKEN);

        setField(gitHubProjectService, "managementService", invalidManagementService);

        try {
            gitHubProjectService.getGitHubInstance();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "GitHub service endpoint was not defined");
        }
    }

    @Test
    public void testGetGitHubInstanceWithInvalidToken() throws IOException {
        when(ghBuilder.withEndpoint(any(String.class))).thenReturn(ghBuilder);
        when(ghBuilder.withOAuthToken(any(String.class))).thenReturn(ghBuilder);
        when(ghBuilder.build()).thenReturn(github);

        ManagementService invalidManagementService = new RemoteProjectManager("GitHub", ServiceType.GITHUB_PROJECT, TEST_PROJECT1_URL, TEST_PROJECT1_TOKEN);

        setField(gitHubProjectService, "managementService", invalidManagementService);

        try {
            gitHubProjectService.getGitHubInstance();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "GitHub token was not defined");
        }
    }

    @Test
    public void testGetGitHubInstanceByToken() throws IOException {
        when(ghBuilder.withEndpoint(any(String.class))).thenReturn(ghBuilder);
        when(ghBuilder.withOAuthToken(any(String.class))).thenReturn(ghBuilder);
        when(ghBuilder.build()).thenReturn(github);

        GitHub gitHubInstance = gitHubProjectService.getGitHubInstance();
        assertNotNull(gitHubInstance, "GitHub object was not created");
    }

    @Test
    public void testGetCardsWithNote() throws Exception {
        when(github.getOrganization(any(String.class))).thenReturn(testOrganization);
        when(testCard1.getContent()).thenReturn(null);

        List<Sprint> sprints = gitHubProjectService.getAdditionalActiveSprints();
        assertEquals(5, sprints.get(0).getCards().size(), "Didn't get expected number of cards");
    }
}
