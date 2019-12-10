package edu.tamu.app.service.manager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
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

	private GitHubService gitHubService;

	private GitHub github;

	private List<RemoteProject> mockRemoteProjects;

	private List<Sprint> mockActiveSprints;

	@Before
	public void setup() throws Exception {
		ManagementService managementService = new RemoteProjectManager("GitHub", ServiceType.GITHUB,
				new HashMap<String, String>() {
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

		when(cardTypeRepo.findByMapping(any(String.class))).thenAnswer(new Answer<Optional<CardType>>() {
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

		when(cardTypeRepo.findByIdentifier(any(String.class)))
				.thenReturn(new CardType("Feature", new HashSet<String>(Arrays.asList(new String[] { "Story" }))));

		when(statusRepo.findByMapping(any(String.class))).thenAnswer(new Answer<Optional<Status>>() {
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

		when(estimateRepo.findByMapping(any(String.class))).thenAnswer(new Answer<Optional<Estimate>>() {
			@Override
			public Optional<Estimate> answer(InvocationOnMock invocation) {
				return Optional.empty();
			}
		});

		when(statusRepo.findByIdentifier(any(String.class)))
				.thenReturn(new Status("None", new HashSet<String>(Arrays.asList(new String[] { "None", "Future" }))));

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
		GHOrganization organization = mock(GHOrganization.class);

		Map<String, GHRepository> mockRepos = new HashMap<String, GHRepository>();

		List<GHLabel> mockLabels = new ArrayList<GHLabel>();
		
		List<GHProject> mockProjects = new ArrayList<GHProject>();

		@SuppressWarnings("unchecked")
		PagedIterable<GHLabel> labelsIterable = mock(PagedIterable.class);

		when(labelsIterable.asList()).thenReturn(mockLabels);
		
		@SuppressWarnings("unchecked")
		PagedIterable<GHProject> projectsIterable = mock(PagedIterable.class);

		when(projectsIterable.asList()).thenReturn(mockProjects);

		GHRepository mockRepo1 = mock(GHRepository.class);
		GHRepository mockRepo2 = mock(GHRepository.class);

		when(mockRepo1.listLabels()).thenReturn(labelsIterable);
		when(mockRepo2.listLabels()).thenReturn(labelsIterable);
		
		when(mockRepo1.listProjects()).thenReturn(projectsIterable);
		when(mockRepo2.listProjects()).thenReturn(projectsIterable);

		mockRepos.put("Test Repo 1", mockRepo1);
		mockRepos.put("Test Repo 2", mockRepo2);

		when(organization.getRepositories()).thenReturn(mockRepos);

		when(github.getOrganization(any(String.class))).thenReturn(organization);

		List<RemoteProject> remoteProjects = gitHubService.getRemoteProjects();
		assertEquals("Didn't get all the remote projects", 2, remoteProjects.size());
	}
}