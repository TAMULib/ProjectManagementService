package edu.tamu.app.service.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

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
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.RemoteProjectManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.Status;
import edu.tamu.app.model.repo.CardTypeRepo;
import edu.tamu.app.model.repo.EstimateRepo;
import edu.tamu.app.model.repo.StatusRepo;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.rest.TokenAuthRestTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
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

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class VersionOneServiceTest extends CacheMockTests {

    private static final String TEST_PROJECT_URL1 = "http://localhost/1";

    private static final String TEST_PROJECT_TOKEN1 = "0123456789";

    @Value("classpath:images/no_avatar.png")
    private Resource mockImage;

    private List<RemoteProject> mockRemoteProjects;

    private List<Sprint> mockActiveSprints;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private VersionOneService versionOneService;

    @Mock
    private TokenAuthRestTemplate restTemplate;

    @Mock
    private IServices services;

    @Mock
    private StatusRepo statusRepo;

    @Mock
    private CardTypeRepo cardTypeRepo;

    @Mock
    private EstimateRepo estimateRepo;

    @BeforeEach
    public void setup() throws JsonParseException, JsonMappingException, IOException, V1Exception {

        ManagementService managementService = new RemoteProjectManager("Version One", ServiceType.VERSION_ONE, TEST_PROJECT_URL1, TEST_PROJECT_TOKEN1);
        CardTypeMappingService cardTypeMappingService = mock(CardTypeMappingService.class, Mockito.CALLS_REAL_METHODS);
        StatusMappingService statusMappingService = mock(StatusMappingService.class, Mockito.CALLS_REAL_METHODS);
        EstimateMappingService estimateMappingService = mock(EstimateMappingService.class, Mockito.CALLS_REAL_METHODS);

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

        when(result.getAssets()).thenReturn(assets);

        when(scopeType.getAttributeDefinition("Name")).thenReturn(nameAttributeDefinition);

        when(metaModel.getAssetType("Scope")).thenReturn(scopeType);

        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        doReturn(2).when(versionOneService).getPrimaryWorkItemCount(matches("Request"), anyString());
        doReturn(3).when(versionOneService).getPrimaryWorkItemCount(matches("Issue"), anyString());
        doReturn(4).when(versionOneService).getPrimaryWorkItemCount(matches("Story"), anyString());
        doReturn(1).when(versionOneService).getPrimaryWorkItemCount(matches("Defect"), anyString());

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

        when(result.getAssets()).thenReturn(assets);

        when(scopeType.getAttributeDefinition("Name")).thenReturn(nameAttributeDefinition);

        when(metaModel.getAssetType("Scope")).thenReturn(scopeType);

        when(services.getOid(anyString())).thenReturn(oid);
        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        doReturn(2).when(versionOneService).getPrimaryWorkItemCount("Request", "1934");
        doReturn(3).when(versionOneService).getPrimaryWorkItemCount("Issue", "1934");
        doReturn(4).when(versionOneService).getPrimaryWorkItemCount("Story", "1934");
        doReturn(1).when(versionOneService).getPrimaryWorkItemCount("Defect", "1934");

        RemoteProject remoteProject = versionOneService.getRemoteProjectByScopeId("1934");

        assertEquals(mockRemoteProjects.get(0).getId(), remoteProject.getId(), "Remote project has incorrect scope id!");
        assertEquals(mockRemoteProjects.get(0).getName(), remoteProject.getName(), "Remote project had incorrect name!");
    }

    @Test
    public void testGetPrimaryWorkItemCount() throws ConnectionException, APIException, OidException, JsonParseException, JsonMappingException, IOException {
        testGetPrimaryWorkItemCount("Request");
        testGetPrimaryWorkItemCount("Story");
        testGetPrimaryWorkItemCount("Defect");
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

        when(nameAttributeDefinition.getName()).thenReturn("Name");

        when(scheduleScheduledScopesAttributeeDefinition.getName()).thenReturn("Schedule.ScheduledScopes");

        when(scheduleScheduledScopesNameAttributeeDefinition.getName()).thenReturn("Schedule.ScheduledScopes.Name");

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

        when(cardTypeRepo.findByMapping(anyString())).thenAnswer(new Answer<Optional<CardType>>() {
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

        when(statusRepo.findByMapping(anyString())).thenAnswer(new Answer<Optional<Status>>() {
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

        List<Card> mocKSprint1Cards = mockActiveSprints.get(0).getCards();
        Asset[] assets = getMockActiveSprintCardAssets(mocKSprint1Cards);

        when(nameAttributeDefinition.getName()).thenReturn("Name");

        when(numberAttributeDefinition.getName()).thenReturn("Number");
        when(assetTypeAttributeDefinition.getName()).thenReturn("AssetType");
        when(descriptionAttributeDefinition.getName()).thenReturn("Description");
        when(statusNameAttributeDefinition.getName()).thenReturn("Status.Name");
        when(estimateAttributeDefinition.getName()).thenReturn("Estimate");
        when(ownersAttributeDefinition.getName()).thenReturn("Owners");

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
                if (invocation.getArguments().length > 0) {
                    String memberId = (String) invocation.getArguments()[0];
                    return getMockMemberById(memberId);
                }

                return null;
            }
        }).when(versionOneService).getMember(anyString());

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
        when(nameAttributeDefinition.getName()).thenReturn("Name");
        when(avatarAttributeDefinition.getName()).thenReturn("Avatar");
        when(memberType.getAttributeDefinition("Name")).thenReturn(nameAttributeDefinition);
        when(memberType.getAttributeDefinition("Avatar")).thenReturn(avatarAttributeDefinition);
        when(metaModel.getAssetType("Member")).thenReturn(memberType);

        when(services.getOid("Member:0001")).thenReturn(oid);
        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        Member member = versionOneService.getMember("0001");

        assertEquals(mockMember.getId(), member.getId(), "Member had incorrect id!");
        assertEquals(mockMember.getName(), member.getName(), "Member had incorrect name!");
        assertEquals(mockMember.getAvatar(), member.getAvatar(), "Member had incorrect avatar!");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMemberWithAvatarImage() throws JsonParseException, JsonMappingException, APIException, IOException, OidException, ConnectionException {

        Oid oid = mock(Oid.class);
        QueryResult result = mock(QueryResult.class);
        IMetaModel metaModel = mock(IMetaModel.class);
        IAssetType memberType = mock(IAssetType.class);
        IAttributeDefinition nameAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition avatarAttributeDefinition = mock(IAttributeDefinition.class);

        Member mockMember = getMockMemberById("0003");
        Asset[] assets = getMockMemberAsset(mockMember, true);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenAnswer(new Answer<ResponseEntity<byte[]>>() {
            @Override
            public ResponseEntity<byte[]> answer(InvocationOnMock invocation) throws IOException {
                byte[] bytes = Files.readAllBytes(mockImage.getFile().toPath());
                return new ResponseEntity<byte[]>(bytes, HttpStatus.OK);
            }
        });

        when(result.getAssets()).thenReturn(assets);
        when(nameAttributeDefinition.getName()).thenReturn("Name");
        when(avatarAttributeDefinition.getName()).thenReturn("Avatar");
        when(memberType.getAttributeDefinition("Name")).thenReturn(nameAttributeDefinition);
        when(memberType.getAttributeDefinition("Avatar")).thenReturn(avatarAttributeDefinition);
        when(metaModel.getAssetType("Member")).thenReturn(memberType);

        when(services.getOid("Member:0003")).thenReturn(oid);
        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        Member member = versionOneService.getMember("0003");

        assertEquals(mockMember.getId(), member.getId(), "Member had incorrect id!");
        assertEquals(mockMember.getName(), member.getName(), "Member had incorrect name!");
        assertEquals(mockMember.getAvatar(), member.getAvatar(), "Member had incorrect avatar!");
    }

    @Test
    public void testPush() throws V1Exception {
        Oid oid = mock(Oid.class);
        Oid assetOid = mock(Oid.class);

        IMetaModel metaModel = mock(IMetaModel.class);
        IAssetType assetType = mock(IAssetType.class);
        IAttributeDefinition attributeDefinition = mock(IAttributeDefinition.class);

        Asset mockAsset = mock(Asset.class);

        when(assetType.getAttributeDefinition(anyString())).thenReturn(attributeDefinition);
        when(metaModel.getAssetType(anyString())).thenReturn(assetType);

        when(services.getOid(anyString())).thenReturn(oid);
        when(services.getMeta()).thenReturn(metaModel);
        when(services.createNew(any(IAssetType.class), any(Oid.class))).thenReturn(mockAsset);

        when(mockAsset.getOid()).thenReturn(assetOid);
        when(assetOid.getToken()).thenReturn("token:assetOid");

        doNothing().when(mockAsset).setAttributeValue(any(IAttributeDefinition.class), any(Object.class));

        Object request = versionOneService.push(new FeatureRequest("New Feature", "I would like to turn off service through API.", 1L, "0001"));

        assertNotNull(request);
    }

    private void testGetPrimaryWorkItemCount(String type) throws ConnectionException, APIException, OidException, JsonParseException, JsonMappingException, IOException {
        QueryResult result = mock(QueryResult.class);
        IMetaModel metaModel = mock(IMetaModel.class);
        IAssetType assetType = mock(IAssetType.class);
        IAttributeDefinition scopeAttributeDefinition = mock(IAttributeDefinition.class);
        IAttributeDefinition assetStateAttributeDefinition = mock(IAttributeDefinition.class);

        Asset[] assets = new Asset[0];

        when(assetType.getAttributeDefinition("Scope")).thenReturn(scopeAttributeDefinition);
        when(assetType.getAttributeDefinition("AssetState")).thenReturn(assetStateAttributeDefinition);

        when(metaModel.getAssetType(type)).thenReturn(assetType);

        when(result.getAssets()).thenReturn(assets);

        when(services.getMeta()).thenReturn(metaModel);
        when(services.retrieve(any(Query.class))).thenReturn(result);

        assertEquals(0, versionOneService.getPrimaryWorkItemCount(type, "1934"), "Incorrect number of " + type);
    }

    private Asset[] getMockActiveSprintAssets(String scopeId) throws JsonParseException, JsonMappingException, IOException, APIException {
        List<Asset> mockAssets = new ArrayList<Asset>();
        for (Sprint activeSprint : mockActiveSprints) {

            Asset mockAsset = mock(Asset.class);

            Oid mockOid = mock(Oid.class);

            Attribute mockNameAttribute = mock(Attribute.class);
            Attribute mockScheduledScopesAttribute = mock(Attribute.class);
            Attribute mockScheduledScopesNameAttribute = mock(Attribute.class);

            when(mockOid.toString()).thenReturn("Timebox:" + activeSprint.getId());

            when(mockNameAttribute.getValue()).thenReturn(activeSprint.getName());

            when(mockScheduledScopesAttribute.getValues()).thenReturn(new String[] { "Scope:" + scopeId });

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

            IAssetType storyAssetType = mock(IAssetType.class);

            Attribute mockNameAttribute = mock(Attribute.class);
            Attribute mockNumberAttribute = mock(Attribute.class);
            Attribute mockAssetTypeAttribute = mock(Attribute.class);
            Attribute mockDescriptionAttribute = mock(Attribute.class);
            Attribute mockStatusNameAttribute = mock(Attribute.class);
            Attribute mockEstimateAttribute = mock(Attribute.class);
            Attribute mockOwnersAttribute = mock(Attribute.class);

            when(mockOid.toString()).thenReturn("PrimaryWorkitem:" + card.getId());

            when(mockNameAttribute.getValue()).thenReturn(card.getName());
            when(mockNumberAttribute.getValue()).thenReturn(card.getNumber());
            when(storyAssetType.getToken()).thenReturn(card.getStatus().equals("Feature") ? "Story" : "Defect");

            when(mockAssetTypeAttribute.getValue()).thenReturn(storyAssetType);
            when(mockDescriptionAttribute.getValue()).thenReturn(card.getDescription());
            when(mockStatusNameAttribute.getValue()).thenReturn(card.getStatus());
            when(mockEstimateAttribute.getValue()).thenReturn(card.getEstimate());

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
                Attribute mockNameAttribute = mock(Attribute.class);
                when(mockNameAttribute.getValue()).thenReturn(remoteProject.getName());
                when(mockAsset.getAttribute(any(IAttributeDefinition.class))).thenReturn(mockNameAttribute);
                mockAssets.add(mockAsset);
                break;
            }
        }
        return mockAssets.toArray(new Asset[mockAssets.size()]);
    }

    private Asset[] getMockMemberAsset(Member member, boolean withImage) throws APIException {
        Asset mockAsset = mock(Asset.class);
        Attribute mockNameAttribute = mock(Attribute.class);
        Attribute mockAvatarAttribute = mock(Attribute.class);
        Oid oid = mock(Oid.class);

        when(mockNameAttribute.getValue()).thenReturn(member.getName());
        when(oid.toString()).thenReturn(withImage ? "Image:" + member.getId() : "NULL");
        when(mockAvatarAttribute.getValue()).thenReturn(oid);

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
        assertEquals(mockRemoteProjects.size(), remoteProjects.size(), "Incorrect number of remote projects!");
        for (int i = 0; i < mockRemoteProjects.size(); i++) {
            RemoteProject remoteProject = remoteProjects.get(i);
            RemoteProject mockRemoteProduct = mockRemoteProjects.get(i);
            assertEquals(mockRemoteProduct.getId(), remoteProject.getId(), "Remote project has incorrect scope id!");
            assertEquals(mockRemoteProduct.getName(), remoteProject.getName(), "Remote project had incorrect name!");
        }
    }

    private void assertActiveSprints(List<Sprint> activeSprints) {
        assertEquals(mockActiveSprints.size(), activeSprints.size(), "Incorrect number of active sprints!");
        for (int i = 0; i < mockActiveSprints.size(); i++) {
            Sprint activeSprint = activeSprints.get(i);
            Sprint mockActiveSprint = mockActiveSprints.get(i);
            assertEquals(mockActiveSprint.getId(), activeSprint.getId(), "Active sprint has incorrect id!");
            assertEquals(mockActiveSprint.getName(), activeSprint.getName(), "Active sprint had incorrect name!");
            assertEquals(mockActiveSprint.getProduct(), activeSprint.getProduct(), "Active sprint had incorrect product!");
        }
    }

    private void assertActiveSprintCards(List<Card> mockActiveSprintCards, List<Card> activeSprintCards) {
        assertEquals(mockActiveSprintCards.size(), activeSprintCards.size(), "Incorrect number of cards on active sprint!");
        activeSprintCards.forEach(activeSprintCard -> {
            for (Card mockActiveSprintCard : mockActiveSprintCards) {
                if (activeSprintCard.getId().equals(mockActiveSprintCard.getId())) {
                    assertEquals(mockActiveSprintCard.getId(), activeSprintCard.getId(), "Active sprint card has incorrect id!");
                    assertEquals(mockActiveSprintCard.getName(), activeSprintCard.getName(), "Active sprint card had incorrect name!");
                    assertEquals(mockActiveSprintCard.getDescription(), activeSprintCard.getDescription(), "Active sprint card had incorrect description!");
                    assertEquals(mockActiveSprintCard.getStatus(), activeSprintCard.getStatus(), "Active sprint card had incorrect status!");
                    assertEquals(mockActiveSprintCard.getEstimate(), activeSprintCard.getEstimate(), "Active sprint card had incorrect estimate!");
                    assertEquals(mockActiveSprintCard.getAssignees().size(), activeSprintCard.getAssignees().size(), "Active sprint card had incorrect number of assignees!");
                    break;
                }
            }
        });
    }

}