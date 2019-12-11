package edu.tamu.app.service.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import static edu.tamu.app.service.manager.GitHubService.FEATURE_LABEL;
import static edu.tamu.app.service.manager.GitHubService.DEFECT_LABEL;
import static edu.tamu.app.service.manager.GitHubService.ISSUE_LABEL;
import static edu.tamu.app.service.manager.GitHubService.REQUEST_LABEL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.kohsuke.github.PagedIterable;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit4.SpringRunner;

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

@RunWith(SpringRunner.class)
public class GitHubServiceTest extends CacheMockTests {
	private static final String TEST_REPOSITORY1_NAME = "Test repository 1 name";
	private static final String TEST_REPOSITORY2_NAME = "Test repository 2 name";
	private static final String TEST_UNUSED_LABEL_NAME = "unused";
    private static final Long TEST_REPOSITORY1_ID = 1L;
    private static final Long TEST_REPOSITORY2_ID = 2L;
	
	private static final GHLabel TEST_LABEL1 = mock(GHLabel.class);
	private static final GHLabel TEST_LABEL2 = mock(GHLabel.class);
	private static final GHLabel TEST_LABEL3 = mock(GHLabel.class);
	private static final GHLabel TEST_LABEL4 = mock(GHLabel.class);
	private static final GHLabel TEST_LABEL5 = mock(GHLabel.class);

	private static final GHProjectCard TEST_CARD1 = mock(GHProjectCard.class, RETURNS_DEEP_STUBS.get());
	private static final GHProjectCard TEST_CARD2 = mock(GHProjectCard.class, RETURNS_DEEP_STUBS.get());
	private static final GHProjectCard TEST_CARD3 = mock(GHProjectCard.class, RETURNS_DEEP_STUBS.get());
	private static final GHProjectCard TEST_CARD4 = mock(GHProjectCard.class, RETURNS_DEEP_STUBS.get());
	private static final GHProjectCard TEST_CARD5 = mock(GHProjectCard.class, RETURNS_DEEP_STUBS.get());

	private static final GHProjectColumn TEST_COLUMN1 = mock(GHProjectColumn.class, RETURNS_DEEP_STUBS.get());
	private static final GHProjectColumn TEST_COLUMN2 = mock(GHProjectColumn.class, RETURNS_DEEP_STUBS.get());
	private static final GHProjectColumn TEST_COLUMN3 = mock(GHProjectColumn.class, RETURNS_DEEP_STUBS.get());

	private static final GHProject TEST_PROJECT1 = mock(GHProject.class, RETURNS_DEEP_STUBS.get());
	private static final GHProject TEST_PROJECT2 = mock(GHProject.class, RETURNS_DEEP_STUBS.get());
	private static final GHProject TEST_PROJECT3 = mock(GHProject.class, RETURNS_DEEP_STUBS.get());

	private static final GHRepository TEST_REPOSITORY1 = mock(GHRepository.class, RETURNS_DEEP_STUBS.get());
	private static final GHRepository TEST_REPOSITORY2 = mock(GHRepository.class, RETURNS_DEEP_STUBS.get());

	private static final GHOrganization TEST_ORGANIZATION = mock(GHOrganization.class);

	private static final List<GHLabel> ALL_TEST_LABELS = new ArrayList<GHLabel>(
		Arrays.asList(new GHLabel[] { TEST_LABEL1, TEST_LABEL2, TEST_LABEL3, TEST_LABEL4, TEST_LABEL5 }));
	private static final List<GHLabel> TEST_CARD1_LABELS = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { TEST_LABEL1, TEST_LABEL5 }));
	private static final List<GHLabel> TEST_CARD2_LABELS = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { TEST_LABEL2, TEST_LABEL5 }));
	private static final List<GHLabel> TEST_CARD3_LABELS = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { TEST_LABEL3, TEST_LABEL5 }));
	private static final List<GHLabel> TEST_CARD4_LABELS = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { TEST_LABEL4 }));
	private static final List<GHLabel> TEST_CARD5_LABELS = new ArrayList<GHLabel>(Arrays.asList(new GHLabel[] { TEST_LABEL5 }));

	private static final List<GHProjectCard> TEST_COLUMN1_CARDS = new ArrayList<GHProjectCard>(
		Arrays.asList(new GHProjectCard[] { TEST_CARD1, TEST_CARD2, TEST_CARD3 }));
	private static final List<GHProjectCard> TEST_COLUMN2_CARDS = new ArrayList<GHProjectCard>(
		Arrays.asList(new GHProjectCard[] { TEST_CARD3, TEST_CARD4 }));
	private static final List<GHProjectCard> TEST_COLUMN3_CARDS = new ArrayList<GHProjectCard>(
		Arrays.asList(new GHProjectCard[] { TEST_CARD5 }));

	private static final List<GHProjectColumn> TEST_PROJECT_COLUMNS = new ArrayList<GHProjectColumn>(
		Arrays.asList(new GHProjectColumn[] { TEST_COLUMN1, TEST_COLUMN2, TEST_COLUMN3 }));

	private static final List<GHProject> TEST_PROJECTS = new ArrayList<GHProject>(Arrays.asList(new GHProject[] { TEST_PROJECT1, TEST_PROJECT2, TEST_PROJECT3 }));

	private static final List<GHRepository> TEST_REPOSITORIES = new ArrayList<GHRepository>(
		Arrays.asList(new GHRepository[] { TEST_REPOSITORY1, TEST_REPOSITORY2 } ));
	

	Map<String, GHRepository> TEST_REPOSITORY_MAP = Stream.of(new Object[][] {
		{ TEST_REPOSITORY1_NAME, TEST_REPOSITORY1 },
		{ TEST_REPOSITORY2_NAME, TEST_REPOSITORY2 }
	}).collect(Collectors.toMap(data -> (String) data[0], data -> (GHRepository) data[1]));

	private GitHubService gitHubService;

	private GitHub github;

	private List<RemoteProject> mockRemoteProjects;

	private List<Sprint> mockActiveSprints;

	@Before
	public void setup() throws Exception {
		ManagementService managementService = new RemoteProjectManager("GitHub", ServiceType.GITHUB, new HashMap<String, String>() {
			private static final long serialVersionUID = 2020874481642498006L;
			{
				put("url", "https://localhost:9101/TexasAMLibrary");
				put("username", "username");
				put("password", "password");
			}
		});

		CardTypeRepo cardTypeRepo = mock(CardTypeRepo.class);
		StatusRepo statusRepo = mock(StatusRepo.class);
		EstimateRepo estimateRepo = mock(EstimateRepo.class);

		CardTypeMappingService cardTypeMappingService = mock(CardTypeMappingService.class, Mockito.CALLS_REAL_METHODS);

		StatusMappingService statusMappingService = mock(StatusMappingService.class, Mockito.CALLS_REAL_METHODS);

		EstimateMappingService estimateMappingService = mock(EstimateMappingService.class, Mockito.CALLS_REAL_METHODS);

		gitHubService = mock(GitHubService.class, Mockito.CALLS_REAL_METHODS);

		github = mock(GitHub.class);

		when(github.getOrganization(any(String.class))).thenReturn(TEST_ORGANIZATION);
		when(github.getRepositoryById(any(String.class))).thenReturn(TEST_REPOSITORY1);

		when(TEST_ORGANIZATION.getRepositories()).thenReturn(TEST_REPOSITORY_MAP);

		when(TEST_REPOSITORY1.getId()).thenReturn(TEST_REPOSITORY1_ID);
		when(TEST_REPOSITORY1.listProjects().asList()).thenReturn(TEST_PROJECTS);
		when(TEST_REPOSITORY2.listProjects().asList()).thenReturn(TEST_PROJECTS);
		when(TEST_REPOSITORY1.listLabels().asList()).thenReturn(ALL_TEST_LABELS);
		when(TEST_REPOSITORY2.listLabels().asList()).thenReturn(ALL_TEST_LABELS);

		when(TEST_PROJECT1.listColumns().asList()).thenReturn(TEST_PROJECT_COLUMNS);
		when(TEST_PROJECT2.listColumns().asList()).thenReturn(TEST_PROJECT_COLUMNS);
		when(TEST_PROJECT3.listColumns().asList()).thenReturn(TEST_PROJECT_COLUMNS);

		when(TEST_COLUMN1.listCards().asList()).thenReturn(TEST_COLUMN1_CARDS);
		when(TEST_COLUMN2.listCards().asList()).thenReturn(TEST_COLUMN2_CARDS);
		when(TEST_COLUMN3.listCards().asList()).thenReturn(TEST_COLUMN3_CARDS);

		when(TEST_CARD1.getContent().getLabels()).thenReturn(TEST_CARD1_LABELS);
		when(TEST_CARD2.getContent().getLabels()).thenReturn(TEST_CARD2_LABELS);
		when(TEST_CARD3.getContent().getLabels()).thenReturn(TEST_CARD3_LABELS);
		when(TEST_CARD4.getContent().getLabels()).thenReturn(TEST_CARD4_LABELS);
		when(TEST_CARD5.getContent().getLabels()).thenReturn(TEST_CARD5_LABELS);

		when(TEST_LABEL1.getName()).thenReturn(REQUEST_LABEL);
		when(TEST_LABEL2.getName()).thenReturn(ISSUE_LABEL);
		when(TEST_LABEL3.getName()).thenReturn(FEATURE_LABEL);
		when(TEST_LABEL4.getName()).thenReturn(DEFECT_LABEL);
		when(TEST_LABEL5.getName()).thenReturn(TEST_UNUSED_LABEL_NAME);


		when(cardTypeRepo.findByMapping(any(String.class))).thenAnswer(new Answer<Optional<CardType>>() {
			@Override
			public Optional<CardType> answer(InvocationOnMock invocation) {
				String identifier = (String) invocation.getArguments()[0];
				Optional<CardType> cardType = Optional.empty();
				switch (identifier) {
				case "Story":
					cardType = Optional.of(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story" }))));
					break;
				case "Defect":
					cardType = Optional.of(new CardType("Defect", new HashSet<String>(Arrays.asList(new String[] { "Defect" }))));
					break;
				}
				return cardType;
			}
		});

		when(cardTypeRepo.findByIdentifier(any(String.class))).thenReturn(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story" }))));

		when(statusRepo.findByMapping(any(String.class))).thenAnswer(new Answer<Optional<Status>>() {
			@Override
			public Optional<Status> answer(InvocationOnMock invocation) {
				String identifier = (String) invocation.getArguments()[0];
				Optional<Status> status = Optional.empty();
				switch (identifier) {
				case "None":
				case "Feature":
					status = Optional.of(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
					break;
				case "In Progress":
					status = Optional.of(new Status("In Progress", new HashSet<String>(Arrays.asList(new String[] { "In Progress" }))));
					break;
				case "Done":
					status = Optional.of(new Status("Done", new HashSet<String>(Arrays.asList(new String[] { "Done" }))));
					break;
				case "Accepted":
					status = Optional.of( new Status("Accepted", new HashSet<String>(Arrays.asList(new String[] { "Accepted" }))));
					break;
				}
				return status;
			}
		});

		when(estimateRepo.findByMapping(any(String.class))).thenAnswer(new Answer<Optional<Estimate>>() {
			@Override
			public Optional<Estimate> answer(InvocationOnMock invocation) {
				return Optional.empty();
			}
		});

		when(statusRepo.findByIdentifier(any(String.class))).thenReturn(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));
		

		setField(cardTypeMappingService, "serviceMappingRepo", cardTypeRepo);
		setField(statusMappingService, "serviceMappingRepo", statusRepo);
		setField(estimateMappingService, "serviceMappingRepo", estimateRepo);

		setField(gitHubService, "managementService", managementService);
		setField(gitHubService, "cardTypeMappingService", cardTypeMappingService);
		setField(gitHubService, "statusMappingService", statusMappingService);
		setField(gitHubService, "estimateMappingService", estimateMappingService);
		setField(gitHubService, "github", github);
		setField(gitHubService, "members", new HashMap<String, Member>());

		mockRemoteProjects = getMockRemoteProjects();

		mockActiveSprints = getMockActiveSprints();
	}

	@Test
	public void testGetRemoteProjects() throws Exception {
		List<RemoteProject> remoteProjects = gitHubService.getRemoteProjects();
		assertEquals("Didn't get all the remote projects", 2, remoteProjects.size());
		assertEquals("Number of Requests was incorrect", 3, remoteProjects.get(0).getRequestCount());
		assertEquals("Number of Issues was incorrect", 3, remoteProjects.get(0).getIssueCount());
		assertEquals("Number of Features was incorrect", 6, remoteProjects.get(0).getFeatureCount());
		assertEquals("Number of Defects was incorrect", 3, remoteProjects.get(0).getDefectCount());
	}

	@Test
	public void testGetRemoteProjectByScopeId() throws Exception {
		RemoteProject project = gitHubService.getRemoteProjectByScopeId(String.valueOf(TEST_REPOSITORY1_ID));
		assertNotNull("Didn't get the remote project", project);
		assertEquals("Did not get the expected project", String.valueOf(TEST_REPOSITORY1_ID), project.getId());
		assertEquals("Number of Requests was incorrect", 3, project.getRequestCount());
		assertEquals("Number of Issues was incorrect", 3, project.getIssueCount());
		assertEquals("Number of Features was incorrect", 6, project.getFeatureCount());
		assertEquals("Number of Defects was incorrect", 3, project.getDefectCount());
	}
}