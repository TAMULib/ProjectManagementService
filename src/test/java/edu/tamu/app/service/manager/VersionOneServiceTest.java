package edu.tamu.app.service.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Attribute;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.exceptions.APIException;
import com.versionone.apiclient.exceptions.ConnectionException;
import com.versionone.apiclient.exceptions.OidException;
import com.versionone.apiclient.exceptions.V1Exception;
import com.versionone.apiclient.interfaces.IAssetType;
import com.versionone.apiclient.interfaces.IAttributeDefinition;
import com.versionone.apiclient.interfaces.IMetaModel;
import com.versionone.apiclient.interfaces.IServices;
import com.versionone.apiclient.services.QueryResult;

import edu.tamu.app.cache.model.Card;
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
import edu.tamu.app.rest.TokenAuthRestTemplate;

@RunWith(SpringRunner.class)
public class VersionOneServiceTest extends CacheMockTests {

    private static final String TEST_PROJECT_URL1 = "http://localhost/1";

    private static final String TEST_PROJECT_TOKEN1 = "0123456789";

    @Value("classpath:images/no_avatar.png")
    private Resource mockImage;

    private VersionOneService versionOneService;

    private IServices services;

    private TokenAuthRestTemplate restTemplate;

    private List<RemoteProject> mockRemoteProjects;

    private List<Sprint> mockActiveSprints;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws JsonParseException, JsonMappingException, IOException, V1Exception {
        ManagementService managementService = new RemoteProjectManager("Version One", ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);

        CardTypeRepo cardTypeRepo = mock(CardTypeRepo.class);
        StatusRepo statusRepo = mock(StatusRepo.class);
        EstimateRepo estimateRepo = mock(EstimateRepo.class);

        CardTypeMappingService cardTypeMappingService = mock(CardTypeMappingService.class, Mockito.CALLS_REAL_METHODS);

        StatusMappingService statusMappingService = mock(StatusMappingService.class, Mockito.CALLS_REAL_METHODS);

        EstimateMappingService estimateMappingService = mock(EstimateMappingService.class, Mockito.CALLS_REAL_METHODS);

        versionOneService = mock(VersionOneService.class, Mockito.CALLS_REAL_METHODS);

        services = mock(IServices.class);

        restTemplate = mock(TokenAuthRestTemplate.class);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Object[].class))).thenAnswer(new Answer<ResponseEntity<byte[]>>() {
            @Override
            public ResponseEntity<byte[]> answer(InvocationOnMock invocation) throws IOException {
                byte[] bytes = Files.readAllBytes(mockImage.getFile().toPath());
                return new ResponseEntity<byte[]>(bytes, HttpStatus.OK);
            }
        });

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
                    status = Optional.of(new Status("Accepted", new HashSet<String>(Arrays.asList(new String[] { "Accepted" }))));
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

        setField(versionOneService, "managementService", managementService);
        setField(versionOneService, "cardTypeMappingService", cardTypeMappingService);
        setField(versionOneService, "statusMappingService", statusMappingService);
        setField(versionOneService, "estimateMappingService", estimateMappingService);
        setField(versionOneService, "services", services);
        setField(versionOneService, "restTemplate", restTemplate);
        setField(versionOneService, "members", new HashMap<String, Member>());

        mockRemoteProjects = getMockRemoteProjects();

        mockActiveSprints = getMockActiveSprints();
    }

    @Test
    public void testVersionOneTokenAuthConnector() {
        ManagementService managementService = new RemoteProjectManager("Version One", ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);
        setField(versionOneService, "managementService", managementService);
    }

    @Test
    public void testGetRemoteProducts() throws ConnectionException, APIException, OidException, JsonParseException, JsonMappingException, IOException {
        QueryResult result = mock(QueryResult.class);
        IMetaModel metaModel = mock(IMetaModel.class);
        IAssetType scopeType = mock(IAssetType.class);
        IAttributeDefinition nameAttributeDefinition = mock(IAttributeDefinition.class);

        Asset[] assets = getMockRemoteProductAssets();

        when(scopeType.getDisplayName()).thenReturn("AssetType'Scope");
        when(scopeType.getToken()).thenReturn("Scope");

        when(nameAttributeDefinition.getName()).thenReturn("Name");
        when(nameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Name'Scope");
        when(nameAttributeDefinition.getToken()).thenReturn("Scope.Name");
        when(nameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(nameAttributeDefinition.getAssetType()).thenReturn(scopeType);

        when(result.getAssets()).thenReturn(assets);

        when(scopeType.getAttributeDefinition("Name")).thenReturn(nameAttributeDefinition);

        when(metaModel.getAssetType("Scope")).thenReturn(scopeType);

        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        doReturn(2).when(versionOneService).getPrimaryWorkItemCount(matches("Request"), any(String.class));
        doReturn(3).when(versionOneService).getPrimaryWorkItemCount(matches("Issue"), any(String.class));
        doReturn(4).when(versionOneService).getPrimaryWorkItemCount(matches("Story"), any(String.class));
        doReturn(1).when(versionOneService).getPrimaryWorkItemCount(matches("Defect"), any(String.class));

        assertRemoteProducts(versionOneService.getRemoteProject());
    }

    @Test
    public void testGetRemoteProductByScopeId() throws ConnectionException, APIException, OidException, JsonParseException, JsonMappingException, IOException {
        Oid oid = mock(Oid.class);

        QueryResult result = mock(QueryResult.class);
        IMetaModel metaModel = mock(IMetaModel.class);
        IAssetType scopeType = mock(IAssetType.class);
        IAttributeDefinition nameAttributeDefinition = mock(IAttributeDefinition.class);

        Asset[] assets = getMockRemoteProductAssetByScopeId("1934");

        when(scopeType.getDisplayName()).thenReturn("AssetType'Scope");
        when(scopeType.getToken()).thenReturn("Scope");

        when(nameAttributeDefinition.getName()).thenReturn("Name");
        when(nameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Name'Scope");
        when(nameAttributeDefinition.getToken()).thenReturn("Scope.Name");
        when(nameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(nameAttributeDefinition.getAssetType()).thenReturn(scopeType);

        when(result.getAssets()).thenReturn(assets);

        when(scopeType.getAttributeDefinition("Name")).thenReturn(nameAttributeDefinition);

        when(metaModel.getAssetType("Scope")).thenReturn(scopeType);

        when(services.getOid(any(String.class))).thenReturn(oid);
        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        doReturn(2).when(versionOneService).getPrimaryWorkItemCount("Request", "1934");
        doReturn(3).when(versionOneService).getPrimaryWorkItemCount("Issue", "1934");
        doReturn(4).when(versionOneService).getPrimaryWorkItemCount("Story", "1934");
        doReturn(1).when(versionOneService).getPrimaryWorkItemCount("Defect", "1934");

        RemoteProject remoteProject = versionOneService.getRemoteProjectByScopeId("1934");

        assertEquals("Remote project has incorrect scope id!", mockRemoteProjects.get(0).getId(), remoteProject.getId());
        assertEquals("Remote project had incorrect name!", mockRemoteProjects.get(0).getName(), remoteProject.getName());
    }

    @Test
    public void testGetPrimaryWorkItemCount() throws ConnectionException, APIException, OidException, JsonParseException, JsonMappingException, IOException {
        testGetPrimaryWorkItemCount("Request");
        testGetPrimaryWorkItemCount("Story");
        testGetPrimaryWorkItemCount("Defect");
    }

    private void testGetPrimaryWorkItemCount(String type) throws ConnectionException, APIException, OidException, JsonParseException, JsonMappingException, IOException {
        QueryResult result = mock(QueryResult.class);
        IMetaModel metaModel = mock(IMetaModel.class);
        IAssetType assetType = mock(IAssetType.class);
        IAttributeDefinition scopeAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition assetStateAttributeDefinition = mock(IAttributeDefinition.class);

        Asset[] assets = new Asset[0];

        when(assetType.getDisplayName()).thenReturn("AssetType'" + type);
        when(assetType.getToken()).thenReturn(type);

        when(scopeAttributeDefinition.getName()).thenReturn("Scope");
        when(scopeAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Scope'" + type);
        when(scopeAttributeDefinition.getToken()).thenReturn(type + "Scope");
        when(scopeAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Relation);
        when(scopeAttributeDefinition.getAssetType()).thenReturn(assetType);

        when(assetStateAttributeDefinition.getName()).thenReturn("AssetState");
        when(assetStateAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'AssetState'" + type);
        when(assetStateAttributeDefinition.getToken()).thenReturn(type + "AssetState");
        when(assetStateAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Relation);
        when(assetStateAttributeDefinition.getAssetType()).thenReturn(assetType);

        when(assetType.getAttributeDefinition("Scope")).thenReturn(scopeAttributeDefinition);
        when(assetType.getAttributeDefinition("AssetState")).thenReturn(assetStateAttributeDefinition);

        when(metaModel.getAssetType(type)).thenReturn(assetType);

        when(result.getAssets()).thenReturn(assets);

        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        assertEquals("Incorrect number of " + type, 0, versionOneService.getPrimaryWorkItemCount(type, "1934"));
    }

    @Test
    public void testGetActiveSprintsByProductId() throws ConnectionException, APIException, OidException, IOException {
        QueryResult result = mock(QueryResult.class);

        IMetaModel metaModel = mock(IMetaModel.class);

        IAssetType timeboxType = mock(IAssetType.class);

        IAttributeDefinition nameAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition stateCodeAttributeeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition scheduleScheduledScopesAttributeeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition scheduleScheduledScopesNameAttributeeDefinition = mock(IAttributeDefinition.class);

        Asset[] assets = getMockActiveSprintAssets("0001");

        when(timeboxType.getDisplayName()).thenReturn("AssetType'Timebox");
        when(timeboxType.getToken()).thenReturn("Timebox");

        when(nameAttributeDefinition.getName()).thenReturn("Name");
        when(nameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Name'Timebox");
        when(nameAttributeDefinition.getToken()).thenReturn("Timebox.Name");
        when(nameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(nameAttributeDefinition.getAssetType()).thenReturn(timeboxType);

        when(stateCodeAttributeeDefinition.getName()).thenReturn("State.Code");
        when(stateCodeAttributeeDefinition.getDisplayName()).thenReturn("AttributeDefinition'State.Code'Timebox");
        when(stateCodeAttributeeDefinition.getToken()).thenReturn("Timebox.State.Code");
        when(stateCodeAttributeeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(stateCodeAttributeeDefinition.getAssetType()).thenReturn(timeboxType);

        when(scheduleScheduledScopesAttributeeDefinition.getName()).thenReturn("Schedule.ScheduledScopes");
        when(scheduleScheduledScopesAttributeeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Schedule.ScheduledScopes'Timebox");
        when(scheduleScheduledScopesAttributeeDefinition.getToken()).thenReturn("Timebox.Schedule.ScheduledScopes");
        when(scheduleScheduledScopesAttributeeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Relation);
        when(scheduleScheduledScopesAttributeeDefinition.getAssetType()).thenReturn(timeboxType);

        when(scheduleScheduledScopesNameAttributeeDefinition.getName()).thenReturn("Schedule.ScheduledScopes.Name");
        when(scheduleScheduledScopesNameAttributeeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Schedule.ScheduledScopes.Name'Timebox");
        when(scheduleScheduledScopesNameAttributeeDefinition.getToken()).thenReturn("Timebox.Schedule.ScheduledScopes.Name");
        when(scheduleScheduledScopesNameAttributeeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(scheduleScheduledScopesNameAttributeeDefinition.getAssetType()).thenReturn(timeboxType);

        when(timeboxType.getAttributeDefinition("Name")).thenReturn(nameAttributeDefinition);
        when(timeboxType.getAttributeDefinition("State.Code")).thenReturn(stateCodeAttributeeDefinition);
        when(timeboxType.getAttributeDefinition("Schedule.ScheduledScopes")).thenReturn(scheduleScheduledScopesAttributeeDefinition);
        when(timeboxType.getAttributeDefinition("Schedule.ScheduledScopes.Name")).thenReturn(scheduleScheduledScopesNameAttributeeDefinition);

        when(metaModel.getAssetType("Timebox")).thenReturn(timeboxType);

        when(result.getAssets()).thenReturn(assets);

        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        List<Card> mocKSprint1Cards = mockActiveSprints.get(0).getCards();
        doReturn(mocKSprint1Cards).when(versionOneService).getActiveSprintsCards("0001");
        List<Card> mocKSprint2Cards = mockActiveSprints.get(1).getCards();
        doReturn(mocKSprint2Cards).when(versionOneService).getActiveSprintsCards("0002");

        List<Sprint> sprints = versionOneService.getActiveSprintsByScopeId("0001");

        assertActiveSprints(sprints);
    }

    @Test
    public void testGetActiveSprintsCards() throws JsonParseException, JsonMappingException, APIException, IOException, ConnectionException, OidException {
        QueryResult result = mock(QueryResult.class);

        IMetaModel metaModel = mock(IMetaModel.class);

        IAssetType primaryWorkitemType = mock(IAssetType.class);

        IAttributeDefinition nameAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition numberAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition assetTypeAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition descriptionAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition statusNameAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition estimateAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition ownersAttributeDefinition = mock(IAttributeDefinition.class);

        IAttributeDefinition timboxAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition assetStateAttributeDefinition = mock(IAttributeDefinition.class);

        List<Card> mocKSprint1Cards = mockActiveSprints.get(0).getCards();
        Asset[] assets = getMockActiveSprintCardAssets(mocKSprint1Cards);

        when(primaryWorkitemType.getDisplayName()).thenReturn("AssetType'PrimaryWorkitem");
        when(primaryWorkitemType.getToken()).thenReturn("PrimaryWorkitem");

        when(nameAttributeDefinition.getName()).thenReturn("Name");
        when(nameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Name'PrimaryWorkitem");
        when(nameAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Name");
        when(nameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(nameAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

        when(numberAttributeDefinition.getName()).thenReturn("Number");
        when(numberAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Number'PrimaryWorkitem");
        when(numberAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Number");
        when(numberAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(numberAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

        when(assetTypeAttributeDefinition.getName()).thenReturn("AssetType");
        when(assetTypeAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'AssetType'PrimaryWorkitem");
        when(assetTypeAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.AssetType");
        when(assetTypeAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.AssetType);
        when(assetTypeAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

        when(descriptionAttributeDefinition.getName()).thenReturn("Description");
        when(descriptionAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Description'PrimaryWorkitem");
        when(descriptionAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Description");
        when(descriptionAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(descriptionAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

        when(statusNameAttributeDefinition.getName()).thenReturn("Status.Name");
        when(statusNameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Status.Name'PrimaryWorkitem");
        when(statusNameAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Status.Name");
        when(statusNameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(statusNameAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

        when(estimateAttributeDefinition.getName()).thenReturn("Estimate");
        when(estimateAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Estimate'PrimaryWorkitem");
        when(estimateAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Estimate");
        when(estimateAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Numeric);
        when(estimateAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

        when(ownersAttributeDefinition.getName()).thenReturn("Owners");
        when(ownersAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Owners'PrimaryWorkitem");
        when(ownersAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Owners");
        when(ownersAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Relation);
        when(ownersAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

        when(timboxAttributeDefinition.getName()).thenReturn("Timebox");
        when(timboxAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Timebox'PrimaryWorkitem");
        when(timboxAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Timebox");
        when(timboxAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Relation);
        when(timboxAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

        when(assetStateAttributeDefinition.getName()).thenReturn("AssetState");
        when(assetStateAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'AssetState'PrimaryWorkitem");
        when(assetStateAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.AssetState");
        when(assetStateAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.State);
        when(assetStateAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

        when(primaryWorkitemType.getAttributeDefinition("Name")).thenReturn(nameAttributeDefinition);
        when(primaryWorkitemType.getAttributeDefinition("Number")).thenReturn(numberAttributeDefinition);
        when(primaryWorkitemType.getAttributeDefinition("AssetType")).thenReturn(assetTypeAttributeDefinition);
        when(primaryWorkitemType.getAttributeDefinition("Description")).thenReturn(descriptionAttributeDefinition);
        when(primaryWorkitemType.getAttributeDefinition("Status.Name")).thenReturn(statusNameAttributeDefinition);
        when(primaryWorkitemType.getAttributeDefinition("Estimate")).thenReturn(estimateAttributeDefinition);
        when(primaryWorkitemType.getAttributeDefinition("Owners")).thenReturn(ownersAttributeDefinition);

        when(primaryWorkitemType.getAttributeDefinition("Timebox")).thenReturn(timboxAttributeDefinition);
        when(primaryWorkitemType.getAttributeDefinition("AssetState")).thenReturn(assetStateAttributeDefinition);

        when(metaModel.getAssetType("PrimaryWorkitem")).thenReturn(primaryWorkitemType);

        when(result.getAssets()).thenReturn(assets);

        when(services.getMeta()).thenReturn(metaModel);

        when(services.retrieve(any(Query.class))).thenReturn(result);

        doAnswer(new Answer<Member>() {
            @Override
            public Member answer(InvocationOnMock invocation) {
                String memberId = (String) invocation.getArguments()[0];
                return getMockMemberById(memberId);
            }
        }).when(versionOneService).getMember(any(String.class));

        List<Card> cards = versionOneService.getActiveSprintsCards("0001");

        assertActiveSprintCards(mocKSprint1Cards, cards);
    }

    @Test
    public void testGetMember() throws JsonParseException, JsonMappingException, APIException, IOException, OidException, ConnectionException {
        Oid oid = mock(Oid.class);

        QueryResult result = mock(QueryResult.class);
        IMetaModel metaModel = mock(IMetaModel.class);
        IAssetType memberType = mock(IAssetType.class);
        IAttributeDefinition nameAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition avatarAttributeDefinition = mock(IAttributeDefinition.class);

        Member mockMember = getMockMemberById("0001");
        Asset[] assets = getMockMemberAsset(mockMember, false);

        when(result.getAssets()).thenReturn(assets);

        when(memberType.getDisplayName()).thenReturn("AssetType'Member");
        when(memberType.getToken()).thenReturn("Member");

        when(nameAttributeDefinition.getName()).thenReturn("Name");
        when(nameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Name'Member");
        when(nameAttributeDefinition.getToken()).thenReturn("Member.Name");
        when(nameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(nameAttributeDefinition.getAssetType()).thenReturn(memberType);

        when(avatarAttributeDefinition.getName()).thenReturn("Avatar");
        when(avatarAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Avatar'Member");
        when(avatarAttributeDefinition.getToken()).thenReturn("Member.Avatar");
        when(avatarAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Relation);
        when(avatarAttributeDefinition.getAssetType()).thenReturn(memberType);

        when(memberType.getAttributeDefinition("Name")).thenReturn(nameAttributeDefinition);
        when(memberType.getAttributeDefinition("Avatar")).thenReturn(avatarAttributeDefinition);

        when(metaModel.getAssetType("Member")).thenReturn(memberType);

        when(services.getOid("Member:0001")).thenReturn(oid);

        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        Member member = versionOneService.getMember("0001");

        assertEquals("Member had incorrect id!", mockMember.getId(), member.getId());
        assertEquals("Member had incorrect name!", mockMember.getName(), member.getName());
        assertEquals("Member had incorrect avatar!", mockMember.getAvatar(), member.getAvatar());
    }

    @Test
    public void testGetMemberWithAvatarImage() throws JsonParseException, JsonMappingException, APIException, IOException, OidException, ConnectionException {
        Oid oid = mock(Oid.class);

        QueryResult result = mock(QueryResult.class);
        IMetaModel metaModel = mock(IMetaModel.class);
        IAssetType memberType = mock(IAssetType.class);
        IAttributeDefinition nameAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition avatarAttributeDefinition = mock(IAttributeDefinition.class);

        Member mockMember = getMockMemberById("0003");
        Asset[] assets = getMockMemberAsset(mockMember, true);

        when(result.getAssets()).thenReturn(assets);

        when(memberType.getDisplayName()).thenReturn("AssetType'Member");
        when(memberType.getToken()).thenReturn("Member");

        when(nameAttributeDefinition.getName()).thenReturn("Name");
        when(nameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Name'Member");
        when(nameAttributeDefinition.getToken()).thenReturn("Member.Name");
        when(nameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(nameAttributeDefinition.getAssetType()).thenReturn(memberType);

        when(avatarAttributeDefinition.getName()).thenReturn("Avatar");
        when(avatarAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Avatar'Member");
        when(avatarAttributeDefinition.getToken()).thenReturn("Member.Avatar");
        when(avatarAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Relation);
        when(avatarAttributeDefinition.getAssetType()).thenReturn(memberType);

        when(memberType.getAttributeDefinition("Name")).thenReturn(nameAttributeDefinition);
        when(memberType.getAttributeDefinition("Avatar")).thenReturn(avatarAttributeDefinition);

        when(metaModel.getAssetType("Member")).thenReturn(memberType);

        when(services.getOid("Member:0003")).thenReturn(oid);

        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        Member member = versionOneService.getMember("0003");

        assertEquals("Member had incorrect id!", mockMember.getId(), member.getId());
        assertEquals("Member had incorrect name!", mockMember.getName(), member.getName());
        assertEquals("Member had incorrect avatar!", mockMember.getAvatar(), member.getAvatar());
    }

    @Test
    public void testPush() throws V1Exception {
        Oid oid = mock(Oid.class);
        Oid assetOid = mock(Oid.class);

        IMetaModel metaModel = mock(IMetaModel.class);
        IAssetType assetType = mock(IAssetType.class);
        IAttributeDefinition attributeDefinition = mock(IAttributeDefinition.class);

        Asset mockAsset = mock(Asset.class);

        when(assetType.getAttributeDefinition(any(String.class))).thenReturn(attributeDefinition);
        when(metaModel.getAssetType(any(String.class))).thenReturn(assetType);

        when(services.getOid(any(String.class))).thenReturn(oid);
        when(services.getMeta()).thenReturn(metaModel);
        when(services.createNew(any(IAssetType.class), any(Oid.class))).thenReturn(mockAsset);

        when(mockAsset.getOid()).thenReturn(assetOid);
        when(assetOid.getToken()).thenReturn("token:assetOid");

        doNothing().when(mockAsset).setAttributeValue(any(IAttributeDefinition.class), any(Object.class));

        Object request = versionOneService.push(new FeatureRequest("New Feature", "I would like to turn off service through API.", 1L, "0001"));

        assertNotNull(request);
    }

    private Asset[] getMockActiveSprintAssets(String scopeId) throws JsonParseException, JsonMappingException, IOException, APIException {
        List<Asset> mockAssets = new ArrayList<Asset>();
        for (Sprint activeSprint : mockActiveSprints) {

            Asset mockAsset = mock(Asset.class);

            Oid mockOid = mock(Oid.class);

            IAssetType timeboxType = mock(IAssetType.class);

            IAttributeDefinition nameAttributeDefinition = mock(IAttributeDefinition.class);
            IAttributeDefinition scheduleScheduledScopesAttributeDefinition = mock(IAttributeDefinition.class);
            IAttributeDefinition scheduledScopesNameAttributeDefinition = mock(IAttributeDefinition.class);

            Attribute mockNameAttribute = mock(Attribute.class);
            Attribute mockScheduledScopesAttribute = mock(Attribute.class);
            Attribute mockScheduledScopesNameAttribute = mock(Attribute.class);

            when(mockOid.toString()).thenReturn("Timebox:" + activeSprint.getId());

            when(timeboxType.getDisplayName()).thenReturn("AssetType'Timebox");
            when(timeboxType.getToken()).thenReturn("Timebox");

            when(nameAttributeDefinition.getName()).thenReturn("Name");
            when(nameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Name'Timebox");
            when(nameAttributeDefinition.getToken()).thenReturn("Timebox.Name");
            when(nameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
            when(nameAttributeDefinition.getAssetType()).thenReturn(timeboxType);

            when(mockNameAttribute.getDefinition()).thenReturn(nameAttributeDefinition);
            when(mockNameAttribute.getValue()).thenReturn(activeSprint.getName());

            when(scheduleScheduledScopesAttributeDefinition.getName()).thenReturn("Schedule.ScheduledScopes.Name");
            when(scheduleScheduledScopesAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Schedule.ScheduledScopes.Name'Timebox");
            when(scheduleScheduledScopesAttributeDefinition.getToken()).thenReturn("Timebox.Schedule.ScheduledScopes.Name");
            when(scheduleScheduledScopesAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
            when(scheduleScheduledScopesAttributeDefinition.getAssetType()).thenReturn(timeboxType);

            when(mockScheduledScopesAttribute.getDefinition()).thenReturn(scheduleScheduledScopesAttributeDefinition);
            when(mockScheduledScopesAttribute.getValues()).thenReturn(new String[] { "Scope:" + scopeId });

            when(scheduledScopesNameAttributeDefinition.getName()).thenReturn("Schedule.ScheduledScopes.Name");
            when(scheduledScopesNameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Schedule.ScheduledScopes.Name'Timebox");
            when(scheduledScopesNameAttributeDefinition.getToken()).thenReturn("Timebox.Schedule.ScheduledScopes.Name");
            when(scheduledScopesNameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
            when(scheduledScopesNameAttributeDefinition.getAssetType()).thenReturn(timeboxType);

            when(mockScheduledScopesNameAttribute.getDefinition()).thenReturn(scheduledScopesNameAttributeDefinition);
            when(mockScheduledScopesNameAttribute.getValues()).thenReturn(new String[] { activeSprint.getProduct() });

            when(mockAsset.getOid()).thenReturn(mockOid);

            when(mockAsset.getAttribute(any(IAttributeDefinition.class))).thenAnswer(new Answer<Attribute>() {
                @Override
                public Attribute answer(InvocationOnMock invocation) {
                    IAttributeDefinition attributeDefinition = (IAttributeDefinition) invocation.getArguments()[0];
                    Attribute attibute;
                    switch (attributeDefinition.getName()) {
                    case "Name":
                        attibute = mockNameAttribute;
                        break;
                    case "Schedule.ScheduledScopes":
                        attibute = mockScheduledScopesAttribute;
                        break;
                    case "Schedule.ScheduledScopes.Name":
                        attibute = mockScheduledScopesNameAttribute;
                        break;
                    default:
                        attibute = null;
                    }
                    return attibute;
                }
            });

            mockAssets.add(mockAsset);
        }
        return mockAssets.toArray(new Asset[mockAssets.size()]);
    }

    private Asset[] getMockActiveSprintCardAssets(List<Card> mockSprintCards) throws JsonParseException, JsonMappingException, IOException, APIException {
        List<Asset> mockAssets = new ArrayList<Asset>();

        for (Card card : mockSprintCards) {

            Asset mockAsset = mock(Asset.class);

            Oid mockOid = mock(Oid.class);

            IAssetType primaryWorkitemType = mock(IAssetType.class);

            IAttributeDefinition nameAttributeDefinition = mock(IAttributeDefinition.class);
            IAttributeDefinition numberAttributeDefinition = mock(IAttributeDefinition.class);
            IAttributeDefinition assetTypeAttributeDefinition = mock(IAttributeDefinition.class);
            IAttributeDefinition descriptionAttributeDefinition = mock(IAttributeDefinition.class);
            IAttributeDefinition statusNameAttributeDefinition = mock(IAttributeDefinition.class);
            IAttributeDefinition estimateAttributeDefinition = mock(IAttributeDefinition.class);
            IAttributeDefinition ownersAttributeDefinition = mock(IAttributeDefinition.class);

            Attribute mockNameAttribute = mock(Attribute.class);
            Attribute mockNumberAttribute = mock(Attribute.class);
            Attribute mockAssetTypeAttribute = mock(Attribute.class);
            Attribute mockDescriptionAttribute = mock(Attribute.class);
            Attribute mockStatusNameAttribute = mock(Attribute.class);
            Attribute mockEstimateAttribute = mock(Attribute.class);
            Attribute mockOwnersAttribute = mock(Attribute.class);

            when(mockOid.toString()).thenReturn("PrimaryWorkitem:" + card.getId());

            when(primaryWorkitemType.getDisplayName()).thenReturn("AssetType'PrimaryWorkitem");
            when(primaryWorkitemType.getToken()).thenReturn("PrimaryWorkitem");

            when(nameAttributeDefinition.getName()).thenReturn("Name");
            when(nameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Name'PrimaryWorkitem");
            when(nameAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Name");
            when(nameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
            when(nameAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

            when(mockNameAttribute.getDefinition()).thenReturn(nameAttributeDefinition);
            when(mockNameAttribute.getValue()).thenReturn(card.getName());

            when(numberAttributeDefinition.getName()).thenReturn("Number");
            when(numberAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Number'PrimaryWorkitem");
            when(numberAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Number");
            when(numberAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
            when(numberAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

            when(mockNumberAttribute.getDefinition()).thenReturn(numberAttributeDefinition);
            when(mockNumberAttribute.getValue()).thenReturn(card.getNumber());

            when(assetTypeAttributeDefinition.getName()).thenReturn("AssetType");
            when(assetTypeAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'AssetType'PrimaryWorkitem");
            when(assetTypeAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.AssetType");
            when(assetTypeAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.AssetType);
            when(assetTypeAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

            when(mockAssetTypeAttribute.getDefinition()).thenReturn(assetTypeAttributeDefinition);

            IAssetType storyAssetType = mock(IAssetType.class);
            when(storyAssetType.getDisplayName()).thenReturn("AssetType'Story");

            when(storyAssetType.getToken()).thenReturn(card.getStatus().equals("Feature") ? "Story" : "Defect");

            when(mockAssetTypeAttribute.getValue()).thenReturn(storyAssetType);

            when(descriptionAttributeDefinition.getName()).thenReturn("Description");
            when(descriptionAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Description'PrimaryWorkitem");
            when(descriptionAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Description");
            when(descriptionAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
            when(descriptionAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

            when(mockDescriptionAttribute.getDefinition()).thenReturn(descriptionAttributeDefinition);
            when(mockDescriptionAttribute.getValue()).thenReturn(card.getDescription());

            when(statusNameAttributeDefinition.getName()).thenReturn("Status.Name");
            when(statusNameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Status.Name'PrimaryWorkitem");
            when(statusNameAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Status.Name");
            when(statusNameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
            when(statusNameAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

            when(mockStatusNameAttribute.getDefinition()).thenReturn(statusNameAttributeDefinition);
            when(mockStatusNameAttribute.getValue()).thenReturn(card.getStatus());

            when(estimateAttributeDefinition.getName()).thenReturn("Estimate");
            when(estimateAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Estimate'PrimaryWorkitem");
            when(estimateAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Estimate");
            when(estimateAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Numeric);
            when(estimateAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

            when(mockEstimateAttribute.getDefinition()).thenReturn(estimateAttributeDefinition);
            when(mockEstimateAttribute.getValue()).thenReturn(card.getEstimate());

            when(ownersAttributeDefinition.getName()).thenReturn("Owners");
            when(ownersAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Owners'PrimaryWorkitem");
            when(ownersAttributeDefinition.getToken()).thenReturn("PrimaryWorkitem.Owners");
            when(ownersAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Relation);
            when(ownersAttributeDefinition.getAssetType()).thenReturn(primaryWorkitemType);

            when(mockOwnersAttribute.getDefinition()).thenReturn(ownersAttributeDefinition);

            Object[] mockMemberObjects = getMockMemberAssetsWithIdOnly(card.getAssignees());
            when(mockOwnersAttribute.getValues()).thenReturn(mockMemberObjects);

            when(mockAsset.getOid()).thenReturn(mockOid);

            when(mockAsset.getAttribute(any(IAttributeDefinition.class))).thenAnswer(new Answer<Attribute>() {
                @Override
                public Attribute answer(InvocationOnMock invocation) {
                    IAttributeDefinition attributeDefinition = (IAttributeDefinition) invocation.getArguments()[0];
                    Attribute attibute;
                    switch (attributeDefinition.getName()) {
                    case "Name":
                        attibute = mockNameAttribute;
                        break;
                    case "Number":
                        attibute = mockNumberAttribute;
                        break;
                    case "AssetType":
                        attibute = mockAssetTypeAttribute;
                        break;
                    case "Description":
                        attibute = mockDescriptionAttribute;
                        break;
                    case "Status.Name":
                        attibute = mockStatusNameAttribute;
                        break;
                    case "Estimate":
                        attibute = mockEstimateAttribute;
                        break;
                    case "Owners":
                        attibute = mockOwnersAttribute;
                        break;
                    default:
                        attibute = null;
                    }
                    return attibute;
                }
            });

            mockAssets.add(mockAsset);
        }
        return mockAssets.toArray(new Asset[mockAssets.size()]);
    }

    private Object[] getMockMemberAssetsWithIdOnly(List<Member> members) {
        List<Object> mockMemberObjects = new ArrayList<Object>();
        for (Member member : members) {
            Asset mockMemberObject = mock(Asset.class);
            doReturn("Member:" + member.getId()).when(mockMemberObject).toString();
            mockMemberObjects.add(mockMemberObject);
        }
        return mockMemberObjects.toArray(new Asset[mockMemberObjects.size()]);
    }

    private Asset[] getMockRemoteProductAssets() throws JsonParseException, JsonMappingException, IOException, APIException {
        List<Asset> mockAssets = new ArrayList<Asset>();
        for (RemoteProject remoteProject : mockRemoteProjects) {
            Asset mockAsset = mock(Asset.class);
            Oid mockOid = mock(Oid.class);
            Attribute mockNameAttribute = mock(Attribute.class);
            when(mockNameAttribute.getValue()).thenReturn(remoteProject.getName());
            when(mockOid.toString()).thenReturn("Scope:" + remoteProject.getId());
            when(mockAsset.getOid()).thenReturn(mockOid);
            when(mockAsset.getAttribute(any(IAttributeDefinition.class))).thenReturn(mockNameAttribute);
            mockAssets.add(mockAsset);
        }
        return mockAssets.toArray(new Asset[mockAssets.size()]);
    }

    private Asset[] getMockRemoteProductAssetByScopeId(String scopeId) throws JsonParseException, JsonMappingException, IOException, APIException {
        List<Asset> mockAssets = new ArrayList<Asset>();
        for (RemoteProject remoteProject : mockRemoteProjects) {
            if (remoteProject.getId().equals(scopeId)) {
                Asset mockAsset = mock(Asset.class);
                Oid mockOid = mock(Oid.class);
                Attribute mockNameAttribute = mock(Attribute.class);
                when(mockNameAttribute.getValue()).thenReturn(remoteProject.getName());
                when(mockOid.toString()).thenReturn("Scope:" + remoteProject.getId());
                when(mockAsset.getOid()).thenReturn(mockOid);
                when(mockAsset.getAttribute(any(IAttributeDefinition.class))).thenReturn(mockNameAttribute);
                mockAssets.add(mockAsset);
                break;
            }
        }
        return mockAssets.toArray(new Asset[mockAssets.size()]);
    }

    private Asset[] getMockMemberAsset(Member member, boolean withImage) throws APIException {
        Asset mockAsset = mock(Asset.class);
        Oid mockOid = mock(Oid.class);

        IAssetType memberType = mock(IAssetType.class);

        IAttributeDefinition nameAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition avatarAttributeDefinition = mock(IAttributeDefinition.class);

        Attribute mockNameAttribute = mock(Attribute.class);
        Attribute mockAvatarAttribute = mock(Attribute.class);

        when(mockOid.toString()).thenReturn("Member:" + member.getId());

        when(memberType.getDisplayName()).thenReturn("AssetType'Member");
        when(memberType.getToken()).thenReturn("Member");

        when(nameAttributeDefinition.getName()).thenReturn("Name");
        when(nameAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Name'Member");
        when(nameAttributeDefinition.getToken()).thenReturn("Member.Name");
        when(nameAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Text);
        when(nameAttributeDefinition.getAssetType()).thenReturn(memberType);

        when(mockNameAttribute.getDefinition()).thenReturn(nameAttributeDefinition);
        when(mockNameAttribute.getValue()).thenReturn(member.getName());

        when(avatarAttributeDefinition.getName()).thenReturn("Avatar");
        when(avatarAttributeDefinition.getDisplayName()).thenReturn("AttributeDefinition'Avatar'Member");
        when(avatarAttributeDefinition.getToken()).thenReturn("Member.Avatar");
        when(avatarAttributeDefinition.getAttributeType()).thenReturn(IAttributeDefinition.AttributeType.Relation);
        when(avatarAttributeDefinition.getAssetType()).thenReturn(memberType);

        when(mockAvatarAttribute.getDefinition()).thenReturn(avatarAttributeDefinition);

        Oid oid = mock(Oid.class);
        Oid assetOid = mock(Oid.class);

        when(oid.toString()).thenReturn(withImage ? "Image:" + member.getId() : "NULL");
        when(mockAvatarAttribute.getValue()).thenReturn(oid);

        when(mockAsset.getOid()).thenReturn(assetOid);
        when(assetOid.getToken()).thenReturn("token:assetOid");

        when(mockAsset.getAttribute(any(IAttributeDefinition.class))).thenAnswer(new Answer<Attribute>() {
            @Override
            public Attribute answer(InvocationOnMock invocation) {
                IAttributeDefinition attributeDefinition = (IAttributeDefinition) invocation.getArguments()[0];
                Attribute attibute;
                switch (attributeDefinition.getName()) {
                case "Name":
                    attibute = mockNameAttribute;
                    break;
                case "Avatar":
                    attibute = mockAvatarAttribute;
                    break;
                default:
                    attibute = null;
                }
                return attibute;
            }
        });

        return new Asset[] { mockAsset };
    }

    private Member getMockMemberById(String memberId) {
        for (Sprint sprint : mockActiveSprints) {
            for (Card card : sprint.getCards()) {
                for (Member member : card.getAssignees()) {
                    if (member.getId().equals(memberId)) {
                        return member;
                    }
                }
            }
        }
        return null;
    }

    private void assertRemoteProducts(List<RemoteProject> remoteProjects) {
        assertEquals("Incorrect number of remote projects!", mockRemoteProjects.size(), remoteProjects.size());
        for (int i = 0; i < mockRemoteProjects.size(); i++) {
            RemoteProject remoteProject = remoteProjects.get(i);
            RemoteProject mockRemoteProduct = mockRemoteProjects.get(i);
            assertEquals("Remote project has incorrect scope id!", mockRemoteProduct.getId(), remoteProject.getId());
            assertEquals("Remote project had incorrect name!", mockRemoteProduct.getName(), remoteProject.getName());
        }
    }

    private void assertActiveSprints(List<Sprint> activeSprints) {
        assertEquals("Incorrect number of active sprints!", mockActiveSprints.size(), activeSprints.size());
        for (int i = 0; i < mockActiveSprints.size(); i++) {
            Sprint activeSprint = activeSprints.get(i);
            Sprint mockActiveSprint = mockActiveSprints.get(i);
            assertEquals("Active sprint has incorrect id!", mockActiveSprint.getId(), activeSprint.getId());
            assertEquals("Active sprint had incorrect name!", mockActiveSprint.getName(), activeSprint.getName());
            assertEquals("Active sprint had incorrect product!", mockActiveSprint.getProduct(), activeSprint.getProduct());
        }
    }

    private void assertActiveSprintCards(List<Card> mockActiveSprintCards, List<Card> activeSprintCards) {
        assertEquals("Incorrect number of cards on active sprint!", mockActiveSprintCards.size(), activeSprintCards.size());
        activeSprintCards.forEach(activeSprintCard -> {
            for (Card mockActiveSprintCard : mockActiveSprintCards) {
                if (activeSprintCard.getId().equals(mockActiveSprintCard.getId())) {
                    assertEquals("Active sprint card has incorrect id!", mockActiveSprintCard.getId(), activeSprintCard.getId());
                    assertEquals("Active sprint card had incorrect name!", mockActiveSprintCard.getName(), activeSprintCard.getName());
                    assertEquals("Active sprint card had incorrect description!", mockActiveSprintCard.getDescription(), activeSprintCard.getDescription());
                    assertEquals("Active sprint card had incorrect status!", mockActiveSprintCard.getStatus(), activeSprintCard.getStatus());
                    assertEquals("Active sprint card had incorrect estimate!", mockActiveSprintCard.getEstimate(), activeSprintCard.getEstimate());
                    assertEquals("Active sprint card had incorrect number of assignees!", mockActiveSprintCard.getAssignees().size(), activeSprintCard.getAssignees().size());
                    break;
                }
            }
        });
    }

}