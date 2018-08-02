package edu.tamu.app.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.Member;
import edu.tamu.app.model.Card;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.Sprint;
import edu.tamu.app.model.VersionManagementSoftware;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.registry.ManagementBeanRegistry;
import edu.tamu.app.service.versioning.VersionManagementSoftwareBean;

@RunWith(SpringRunner.class)
public class SprintsCacheServiceTest {
    private static final String TEST_PROJECT1_NAME = "Test Project 1 Name";
    private static final String TEST_PROJECT1_SCOPE = "1000";
    private static final String TEST_PROJECT2_NAME = "Test Project 2 Name";

    private static final String TEST_SPRINT1_IDENTIFIER = "Test Sprint 1 Identifier";
    private static final String TEST_SPRINT1_NAME = "Test Sprint 1 Name";
    private static final String TEST_SPRINT2_IDENTIFIER = "Test Sprint 2 Identifier";
    private static final String TEST_SPRINT2_NAME = "Test Sprint 2 Name";

    private static final String TEST_MEMBER1_NAME = "Test Assignee 1 Name";
    private static final String TEST_MEMBER1_AVATAR_URL = "/TexasAMLibrary/rest-1.v1/Data/Image/1706";
    private static final String TEST_MEMBER2_NAME = "Test Assignee 2 Name";
    private static final String TEST_MEMBER2_AVATAR_URL = "/TexasAMLibrary/rest-1.v1/Data/Image/5801";

    private static final Member TEST_MEMBER1 = new Member(TEST_MEMBER1_NAME, TEST_MEMBER1_AVATAR_URL);
    private static final Member TEST_MEMBER2 = new Member(TEST_MEMBER2_NAME, TEST_MEMBER2_AVATAR_URL);

    private static final String TEST_CARD1_NAME = "Test Card 1 Name";
    private static final String TEST_CARD1_TITLE= "Test Card 1 Title";
    private static final String TEST_CARD1_BODY = "Test Card 1 Body";
    private static final String TEST_CARD1_ESTIMATE = "2";
    private static final String TEST_CARD1_STATUS = "Test Card 1 Status";
    private static final String TEST_CARD1_CARDTYPE = "Test Card 1 CardType";

    private static final String TEST_CARD2_NAME = "Test Card 2 Name";
    private static final String TEST_CARD2_TITLE= "Test Card 2 Title";
    private static final String TEST_CARD2_BODY = "Test Card 2 Body";
    private static final String TEST_CARD2_ESTIMATE = "5";
    private static final String TEST_CARD2_STATUS = "Test Card 2 Status";
    private static final String TEST_CARD2_CARDTYPE = "Test Card 2 CardType";

    private static final List<Member> TEST_CARD1_MEMBERS = new ArrayList<Member>(Arrays.asList(new Member[] { TEST_MEMBER1, TEST_MEMBER2 }));
    private static final List<Member> TEST_CARD2_MEMBERS = new ArrayList<Member>();

    private static final Card TEST_CARD1= new Card(TEST_CARD1_NAME, TEST_CARD1_TITLE, TEST_CARD1_BODY, TEST_CARD1_ESTIMATE, TEST_CARD1_MEMBERS, TEST_CARD1_STATUS, TEST_CARD1_CARDTYPE);
    private static final Card TEST_CARD2= new Card(TEST_CARD2_NAME, TEST_CARD2_TITLE, TEST_CARD2_BODY, TEST_CARD2_ESTIMATE, TEST_CARD2_MEMBERS, TEST_CARD2_STATUS, TEST_CARD2_CARDTYPE);

    private static final List<Card> TEST_SPRINT1_CARDS = new ArrayList<Card>(Arrays.asList(new Card[] { TEST_CARD1, TEST_CARD2 }));
    private static final List<Card> TEST_SPRINT2_CARDS = new ArrayList<Card>(Arrays.asList(new Card[] { TEST_CARD2, TEST_CARD1 }));

    private static final VersionManagementSoftware TEST_PROJECT1_VERSION_MANAGERMENT_SOFTWARE = new VersionManagementSoftware("Test Version Management Software", ServiceType.VERSION_ONE, new HashMap<String, String>());

    private static Project TEST_PROJECT1 = new Project(TEST_PROJECT1_NAME, TEST_PROJECT1_SCOPE, TEST_PROJECT1_VERSION_MANAGERMENT_SOFTWARE);
    private static Project TEST_PROJECT2 = new Project(TEST_PROJECT2_NAME);

    private static Sprint TEST_SPRINT1 = new Sprint(TEST_SPRINT1_IDENTIFIER, TEST_SPRINT1_NAME, TEST_PROJECT1_NAME, TEST_SPRINT1_CARDS);
    private static Sprint TEST_SPRINT2 = new Sprint(TEST_SPRINT2_IDENTIFIER, TEST_SPRINT2_NAME, TEST_PROJECT2_NAME, TEST_SPRINT2_CARDS);

    private static List<Project> mockProjectList = new ArrayList<Project>(Arrays.asList(new Project[] { TEST_PROJECT1, TEST_PROJECT2 }));
    private static List<Sprint> mockSprintList = new ArrayList<Sprint>(Arrays.asList(new Sprint[] { TEST_SPRINT1, TEST_SPRINT2 }));

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private ManagementBeanRegistry managementBeanRegistry;

    @Mock
    private VersionManagementSoftwareBean vmsBean;

    @Spy
    @InjectMocks
    private SprintsCacheService sprintsCache;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(projectRepo.findAll()).thenReturn(mockProjectList);
        when(vmsBean.getActiveSprintsByProject(any(Project.class))).thenReturn(mockSprintList);
    }

    @Test
    public void cacheActiveSprints() {
        sprintsCache.cacheActiveSprints();
    }
}