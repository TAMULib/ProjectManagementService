package edu.tamu.app.service.manager;

import static edu.tamu.app.service.manager.AbstractGitHubService.DEFECT_LABEL;
import static edu.tamu.app.service.manager.AbstractGitHubService.FEATURE_LABEL;
import static edu.tamu.app.service.manager.AbstractGitHubService.ISSUE_LABEL;
import static edu.tamu.app.service.manager.AbstractGitHubService.REQUEST_LABEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

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
import java.io.IOException;
import java.nio.file.Files;
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
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHProject.ProjectStateFilter;
import org.kohsuke.github.GHProjectCard;
import org.kohsuke.github.GHProjectColumn;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
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
    private static final String TEST_PROJECT1_NAME = "Test Project 1 Sprint Name";
    private static final String TEST_PROJECT2_NAME = "Test Project 2 Sprint Name";
    private static final String TEST_PROJECT3_NAME = "Test Project 3 Sprint Name";
    private static final Long TEST_REPOSITORY1_ID = 1L;
    private static final Long TEST_USER1_ID = 3L;

    private static final String TEST_PROJECT_URL1 = "http://localhost/1";

    private static final String TEST_PROJECT_TOKEN1 = "0123456789";

    private static final GHLabel TEST_LABEL1 = mock(GHLabel.class);
    private static final GHLabel TEST_LABEL2 = mock(GHLabel.class);
    private static final GHLabel TEST_LABEL3 = mock(GHLabel.class);
    private static final GHLabel TEST_LABEL4 = mock(GHLabel.class);
    private static final GHLabel TEST_LABEL5 = mock(GHLabel.class);

    private static final GHIssue TEST_ISSUE1 = mock(GHIssue.class, RETURNS_DEEP_STUBS);
    private static final GHIssue TEST_ISSUE2 = mock(GHIssue.class, RETURNS_DEEP_STUBS);
    private static final GHIssue TEST_ISSUE3 = mock(GHIssue.class, RETURNS_DEEP_STUBS);
    private static final GHIssue TEST_ISSUE4 = mock(GHIssue.class, RETURNS_DEEP_STUBS);
    private static final GHIssue TEST_ISSUE5 = mock(GHIssue.class, RETURNS_DEEP_STUBS);

    private static final GHUser TEST_USER1 = mock(GHUser.class);
    private static final GHUser TEST_USER2 = mock(GHUser.class);
    private static final GHUser TEST_USER3 = mock(GHUser.class);

    private static final GHProjectCard TEST_CARD1 = mock(GHProjectCard.class, RETURNS_DEEP_STUBS);
    private static final GHProjectCard TEST_CARD2 = mock(GHProjectCard.class, RETURNS_DEEP_STUBS);
    private static final GHProjectCard TEST_CARD3 = mock(GHProjectCard.class, RETURNS_DEEP_STUBS);
    private static final GHProjectCard TEST_CARD4 = mock(GHProjectCard.class, RETURNS_DEEP_STUBS);
    private static final GHProjectCard TEST_CARD5 = mock(GHProjectCard.class, RETURNS_DEEP_STUBS);

    private static final GHProjectColumn TEST_COLUMN1 = mock(GHProjectColumn.class, RETURNS_DEEP_STUBS);
    private static final GHProjectColumn TEST_COLUMN2 = mock(GHProjectColumn.class, RETURNS_DEEP_STUBS);
    private static final GHProjectColumn TEST_COLUMN3 = mock(GHProjectColumn.class, RETURNS_DEEP_STUBS);

    private static final GHProject TEST_PROJECT1 = mock(GHProject.class, RETURNS_DEEP_STUBS);
    private static final GHProject TEST_PROJECT2 = mock(GHProject.class, RETURNS_DEEP_STUBS);
    private static final GHProject TEST_PROJECT3 = mock(GHProject.class, RETURNS_DEEP_STUBS);

    private static final GHRepository TEST_REPOSITORY1 = mock(GHRepository.class, RETURNS_DEEP_STUBS);
    private static final GHRepository TEST_REPOSITORY2 = mock(GHRepository.class, RETURNS_DEEP_STUBS);

    private static final GHOrganization TEST_ORGANIZATION = mock(GHOrganization.class, RETURNS_DEEP_STUBS);

    private static final FeatureRequest TEST_FEATURE_REQUEST = mock(FeatureRequest.class);

    private static final RestTemplate restTemplate = mock(RestTemplate.class);

    private static final List<GHLabel> ALL_TEST_LABELS = new ArrayList<GHLabel>(
            Arrays.asList(new GHLabel[] { TEST_LABEL1, TEST_LABEL2, TEST_LABEL3, TEST_LABEL4, TEST_LABEL5 }));
    private static final List<GHLabel> TEST_CARD1_LABELS = new ArrayList<GHLabel>(
            Arrays.asList(new GHLabel[] { TEST_LABEL1, TEST_LABEL5 }));
    private static final List<GHLabel> TEST_CARD2_LABELS = new ArrayList<GHLabel>(
            Arrays.asList(new GHLabel[] { TEST_LABEL2, TEST_LABEL5 }));
    private static final List<GHLabel> TEST_CARD3_LABELS = new ArrayList<GHLabel>(
            Arrays.asList(new GHLabel[] { TEST_LABEL3, TEST_LABEL5 }));
    private static final List<GHLabel> TEST_CARD4_LABELS = new ArrayList<GHLabel>(
            Arrays.asList(new GHLabel[] { TEST_LABEL4 }));
    private static final List<GHLabel> TEST_CARD5_LABELS = new ArrayList<GHLabel>(
            Arrays.asList(new GHLabel[] { TEST_LABEL5 }));

    private static final List<GHUser> TEST_USERS1 = new ArrayList<GHUser>(Arrays.asList(new GHUser[] { TEST_USER1 }));
    private static final List<GHUser> TEST_USERS2 = new ArrayList<GHUser>(
            Arrays.asList(new GHUser[] { TEST_USER1, TEST_USER2 }));
    private static final List<GHUser> TEST_USERS3 = new ArrayList<GHUser>(Arrays.asList(new GHUser[] {}));
    private static final List<GHUser> TEST_USERS4 = new ArrayList<GHUser>(Arrays.asList(new GHUser[] { TEST_USER2 }));
    private static final List<GHUser> TEST_USERS5 = new ArrayList<GHUser>(
            Arrays.asList(new GHUser[] { TEST_USER3, TEST_USER1 }));

    private static final List<GHProjectCard> TEST_COLUMN1_CARDS = new ArrayList<GHProjectCard>(
            Arrays.asList(new GHProjectCard[] { TEST_CARD1, TEST_CARD2, TEST_CARD3 }));
    private static final List<GHProjectCard> TEST_COLUMN2_CARDS = new ArrayList<GHProjectCard>(
            Arrays.asList(new GHProjectCard[] { TEST_CARD3, TEST_CARD4 }));
    private static final List<GHProjectCard> TEST_COLUMN3_CARDS = new ArrayList<GHProjectCard>(
            Arrays.asList(new GHProjectCard[] { TEST_CARD5 }));

    private static final List<GHIssue> TEST_ISSUE_LIST = new ArrayList<GHIssue>(
            Arrays.asList((new GHIssue[] { TEST_ISSUE1, TEST_ISSUE2, TEST_ISSUE3, TEST_ISSUE4, TEST_ISSUE5 })));

    private static final List<GHProjectColumn> TEST_PROJECT_COLUMNS = new ArrayList<GHProjectColumn>(
            Arrays.asList(new GHProjectColumn[] { TEST_COLUMN1, TEST_COLUMN2, TEST_COLUMN3 }));

    private static final List<GHProject> TEST_PROJECTS = new ArrayList<GHProject>(
            Arrays.asList(new GHProject[] { TEST_PROJECT1, TEST_PROJECT2, TEST_PROJECT3 }));

    private static final Map<String, GHRepository> TEST_REPOSITORY_MAP = Stream.of(
            new Object[][] { { TEST_REPOSITORY1_NAME, TEST_REPOSITORY1 }, { TEST_REPOSITORY2_NAME, TEST_REPOSITORY2 } })
            .collect(Collectors.toMap(data -> (String) data[0], data -> (GHRepository) data[1]));

    @Value("classpath:images/no_avatar.png")
    private Resource mockImage;

    @Mock
    private CardTypeRepo cardTypeRepo;

    @Mock
    private StatusRepo statusRepo;

    @Mock
    private EstimateRepo estimateRepo;

    @Mock
    private GitHubBuilder ghBuilder;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private GitHubProjectService gitHubProjectService;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private CardTypeMappingService cardTypeMappingService;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private StatusMappingService statusMappingService;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private EstimateMappingService estimateMappingService;

    @Mock
    private GitHub github;

    @BeforeEach
    public void setUp() throws Exception {
        ManagementService managementService = new RemoteProjectManager("GitHub", ServiceType.GITHUB_PROJECT, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);

        when(TEST_ORGANIZATION.getRepositories()).thenReturn(TEST_REPOSITORY_MAP);
        when(TEST_ORGANIZATION.listProjects(any(ProjectStateFilter.class)).asList()).thenReturn(TEST_PROJECTS);

        when(TEST_REPOSITORY1.getId()).thenReturn(TEST_REPOSITORY1_ID);
        when(TEST_REPOSITORY1.createIssue(any(String.class)).body(any(String.class)).create()).thenReturn(TEST_ISSUE1);
        when(TEST_REPOSITORY1.listProjects(any(ProjectStateFilter.class)).asList()).thenReturn(TEST_PROJECTS);
        when(TEST_REPOSITORY1.listProjects().asList()).thenReturn(TEST_PROJECTS);
        when(TEST_REPOSITORY1.getIssues(any(GHIssueState.class))).thenReturn(TEST_ISSUE_LIST);
        when(TEST_REPOSITORY2.getIssues(any(GHIssueState.class))).thenReturn(TEST_ISSUE_LIST);
        when(TEST_REPOSITORY2.listProjects().asList()).thenReturn(TEST_PROJECTS);
        when(TEST_REPOSITORY1.listLabels().asList()).thenReturn(ALL_TEST_LABELS);
        when(TEST_REPOSITORY2.listLabels().asList()).thenReturn(ALL_TEST_LABELS);

        when(TEST_PROJECT1.listColumns().asList()).thenReturn(TEST_PROJECT_COLUMNS);
        when(TEST_PROJECT2.listColumns().asList()).thenReturn(TEST_PROJECT_COLUMNS);
        when(TEST_PROJECT3.listColumns().asList()).thenReturn(TEST_PROJECT_COLUMNS);

        when(TEST_PROJECT1.getName()).thenReturn(TEST_PROJECT1_NAME);
        when(TEST_PROJECT2.getName()).thenReturn(TEST_PROJECT2_NAME);
        when(TEST_PROJECT3.getName()).thenReturn(TEST_PROJECT3_NAME);

        when(TEST_COLUMN1.listCards().asList()).thenReturn(TEST_COLUMN1_CARDS);
        when(TEST_COLUMN2.listCards().asList()).thenReturn(TEST_COLUMN2_CARDS);
        when(TEST_COLUMN3.listCards().asList()).thenReturn(TEST_COLUMN3_CARDS);

        when(TEST_CARD1.getId()).thenReturn(1L);
        when(TEST_CARD2.getId()).thenReturn(2L);
        when(TEST_CARD3.getId()).thenReturn(3L);
        when(TEST_CARD4.getId()).thenReturn(4L);
        when(TEST_CARD5.getId()).thenReturn(5L);

        when(TEST_CARD1.getContent()).thenReturn(TEST_ISSUE1);
        when(TEST_CARD2.getContent()).thenReturn(TEST_ISSUE2);
        when(TEST_CARD3.getContent()).thenReturn(TEST_ISSUE3);
        when(TEST_CARD4.getContent()).thenReturn(TEST_ISSUE4);
        when(TEST_CARD5.getContent()).thenReturn(TEST_ISSUE5);
        when(TEST_CARD1.getColumn()).thenReturn(TEST_COLUMN1);

        when(TEST_ISSUE1.getLabels()).thenReturn(TEST_CARD1_LABELS);
        when(TEST_ISSUE2.getLabels()).thenReturn(TEST_CARD2_LABELS);
        when(TEST_ISSUE3.getLabels()).thenReturn(TEST_CARD3_LABELS);
        when(TEST_ISSUE4.getLabels()).thenReturn(TEST_CARD4_LABELS);
        when(TEST_ISSUE5.getLabels()).thenReturn(TEST_CARD5_LABELS);
        when(TEST_ISSUE1.getAssignees()).thenReturn(TEST_USERS1);

        when(TEST_COLUMN1.getName()).thenReturn(TEST_COLUMN1_NAME);

        when(TEST_CARD2.getContent().getLabels()).thenReturn(TEST_CARD2_LABELS);
        when(TEST_CARD3.getContent().getLabels()).thenReturn(TEST_CARD3_LABELS);
        when(TEST_CARD4.getContent().getLabels()).thenReturn(TEST_CARD4_LABELS);
        when(TEST_CARD5.getContent().getLabels()).thenReturn(TEST_CARD5_LABELS);
        when(TEST_CARD2.getContent().getAssignees()).thenReturn(TEST_USERS2);
        when(TEST_CARD3.getContent().getAssignees()).thenReturn(TEST_USERS3);
        when(TEST_CARD4.getContent().getAssignees()).thenReturn(TEST_USERS4);
        when(TEST_CARD5.getContent().getAssignees()).thenReturn(TEST_USERS5);

        when(TEST_USER1.getId()).thenReturn(TEST_USER1_ID);
        when(TEST_USER1.getName()).thenReturn(TEST_USER1_NAME);
        when(TEST_USER1.getAvatarUrl()).thenReturn(TEST_USER1_AVATAR_PATH);
        when(TEST_USER2.getAvatarUrl()).thenReturn(TEST_USER2_AVATAR_PATH);
        when(TEST_USER3.getAvatarUrl()).thenReturn(TEST_USER3_AVATAR_PATH);

        when(TEST_LABEL1.getName()).thenReturn(REQUEST_LABEL);
        when(TEST_LABEL2.getName()).thenReturn(ISSUE_LABEL);
        when(TEST_LABEL3.getName()).thenReturn(FEATURE_LABEL);
        when(TEST_LABEL4.getName()).thenReturn(DEFECT_LABEL);
        when(TEST_LABEL5.getName()).thenReturn(TEST_UNUSED_LABEL_NAME);

        when(TEST_FEATURE_REQUEST.getProductId()).thenReturn(TEST_REPOSITORY1_ID);
        when(TEST_FEATURE_REQUEST.getTitle()).thenReturn(TEST_FEATURE_REQUEST_TITLE);
        when(TEST_FEATURE_REQUEST.getDescription()).thenReturn(TEST_FEATURE_REQUEST_DESCRIPTION);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
            .thenAnswer(new Answer<ResponseEntity<byte[]>>() {
                @Override
                public ResponseEntity<byte[]> answer(InvocationOnMock invocation) throws IOException {
                    byte[] bytes = Files.readAllBytes(mockImage.getFile().toPath());
                    return new ResponseEntity<byte[]>(bytes, HttpStatus.OK);
                }
            });

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
    }

    @Test
    public void testGetRemoteProjects() throws Exception {
        when(github.getOrganization(any(String.class))).thenReturn(TEST_ORGANIZATION);

        List<RemoteProject> remoteProjects = gitHubProjectService.getRemoteProject();
        assertEquals(2, remoteProjects.size(), "Didn't get all the remote projects");
        assertEquals(1, remoteProjects.get(0).getRequestCount(), "Number of Requests was incorrect");
        assertEquals(2, remoteProjects.get(0).getIssueCount(), "Number of Issues was incorrect");
        assertEquals(1, remoteProjects.get(0).getFeatureCount(), "Number of Features was incorrect");
        assertEquals(1, remoteProjects.get(0).getDefectCount(), "Number of Defects was incorrect");
    }

    @Test
    public void testGetRemoteProjectByScopeId() throws Exception {
        when(github.getRepositoryById(any(String.class))).thenReturn(TEST_REPOSITORY1);

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
        when(github.getRepositoryById(any(String.class))).thenReturn(TEST_REPOSITORY1);

        List<Sprint> activeSprints = gitHubProjectService.getActiveSprintsByScopeId(String.valueOf(TEST_REPOSITORY1_ID));
        assertEquals(3, activeSprints.size(), "Didn't get all active sprints");
    }

    @Test
    public void testGetAdditionalActiveSprints() throws Exception {
        when(github.getOrganization(any(String.class))).thenReturn(TEST_ORGANIZATION);

        List<Sprint> additionalSprints = gitHubProjectService.getAdditionalActiveSprints();
        assertEquals(3, additionalSprints.size(), "Didn't get all additional sprints");
    }

    @Test
    public void testGetActiveSprintsByProjectIdType() throws Exception {
        when(github.getRepositoryById(any(String.class))).thenReturn(TEST_REPOSITORY1);

        List<Sprint> sprints = gitHubProjectService.getActiveSprintsByScopeId(String.valueOf(TEST_REPOSITORY1_ID));

        sprints.forEach(sprint -> {
            assertEquals(ServiceType.GITHUB_PROJECT.toString(), sprint.getType(), "Didn't get the correct Service Type for the Sprint");
        });
    }

    @Test
    public void testGetAdditionalActiveSprintType() throws Exception {
        when(github.getOrganization(any(String.class))).thenReturn(TEST_ORGANIZATION);

        List<Sprint> sprints = gitHubProjectService.getAdditionalActiveSprints();

        sprints.forEach(sprint -> {
            assertEquals(ServiceType.GITHUB_PROJECT.toString(), sprint.getType(), "Didn't get the correct Service Type for the Sprint");
        });
    }

    @Test
    public void testPush() throws Exception {
        when(github.getRepositoryById(any(String.class))).thenReturn(TEST_REPOSITORY1);

        String id = gitHubProjectService.push(TEST_FEATURE_REQUEST);
        assertNotNull(id);
    }

    @Test
    public void testGetMember() throws IOException {
        Member member = gitHubProjectService.getMember(TEST_USER1);
        assertEquals(String.valueOf(TEST_USER1_ID), member.getId(), "Member ID is incorrect");
        assertEquals(TEST_USER1_NAME, member.getName(), "Member Name is incorrect");
        assertEquals(TEST_USER1_AVATAR_NAME, member.getAvatar(), "Member Avatar URL is incorrect");
    }

    @Test
    public void testGetGitHubInstanceWithInvalidServiceEndpoint() throws IOException {
        when(ghBuilder.withEndpoint(any(String.class))).thenReturn(ghBuilder);
        when(ghBuilder.withOAuthToken(any(String.class))).thenReturn(ghBuilder);
        when(ghBuilder.build()).thenReturn(github);

        ManagementService invalidManagementService = new RemoteProjectManager("GitHub", ServiceType.GITHUB_PROJECT, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);

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

        ManagementService invalidManagementService = new RemoteProjectManager("GitHub", ServiceType.GITHUB_PROJECT, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);

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
        when(github.getOrganization(any(String.class))).thenReturn(TEST_ORGANIZATION);
        when(TEST_CARD1.getContent()).thenReturn(null);

        List<Sprint> sprints = gitHubProjectService.getAdditionalActiveSprints();
        assertEquals(5, sprints.get(0).getCards().size(), "Didn't get expected number of cards");
    }
}
