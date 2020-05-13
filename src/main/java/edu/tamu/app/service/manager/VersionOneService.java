package edu.tamu.app.service.manager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1Connector;
import com.versionone.apiclient.exceptions.APIException;
import com.versionone.apiclient.exceptions.ConnectionException;
import com.versionone.apiclient.exceptions.OidException;
import com.versionone.apiclient.exceptions.V1Exception;
import com.versionone.apiclient.filters.AndFilterTerm;
import com.versionone.apiclient.filters.FilterTerm;
import com.versionone.apiclient.filters.GroupFilterTerm;
import com.versionone.apiclient.interfaces.IAssetType;
import com.versionone.apiclient.interfaces.IAttributeDefinition;
import com.versionone.apiclient.interfaces.IServices;
import com.versionone.apiclient.services.QueryResult;

import edu.tamu.app.cache.model.Card;
import edu.tamu.app.cache.model.Member;
import edu.tamu.app.cache.model.RemoteProduct;
import edu.tamu.app.cache.model.Sprint;
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.rest.BasicAuthRestTemplate;
import edu.tamu.app.rest.TokenAuthRestTemplate;

public class VersionOneService extends MappingRemoteProductManagerBean {

    private static final Logger logger = Logger.getLogger(VersionOneService.class);

    private final ManagementService managementService;

    private final IServices services;

    private final RestTemplate restTemplate;

    private final Map<String, Member> members;

    public VersionOneService(ManagementService managementService) throws MalformedURLException, V1Exception {
        this.managementService = managementService;
        services = new Services(buildConnector());
        restTemplate = getRestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        members = new HashMap<String, Member>();
    }

    @Override
    public List<RemoteProduct> getRemoteProduct() throws ConnectionException, APIException, OidException {
        logger.info("Fetching remote products");
        List<RemoteProduct> remoteProducts = new ArrayList<RemoteProduct>();
        IAssetType scopeType = services.getMeta().getAssetType("Scope");
        IAttributeDefinition nameAttributeDefinition = scopeType.getAttributeDefinition("Name");
        Query query = new Query(scopeType);
        query.getSelection().add(nameAttributeDefinition);
        QueryResult result = services.retrieve(query);
        for (Asset product : result.getAssets()) {
            String scopeId = parseId(product.getOid());
            String name = product.getAttribute(nameAttributeDefinition).getValue().toString();
            int requestCount = getPrimaryWorkItemCount("Request", scopeId);
            int issueCount = getPrimaryWorkItemCount("Issue", scopeId);
            int storyCount = getPrimaryWorkItemCount("Story", scopeId);
            int defectCount = getPrimaryWorkItemCount("Defect", scopeId);
            remoteProducts.add(new RemoteProduct(scopeId, name, requestCount, issueCount, storyCount, defectCount));
        }
        return remoteProducts;
    }

    @Override
    public RemoteProduct getRemoteProductByScopeId(final String scopeId) throws ConnectionException, APIException, OidException {
        logger.info("Fetching remote product by scope id " + scopeId);
        Oid oid = services.getOid("Scope:" + scopeId);
        IAssetType scopeType = services.getMeta().getAssetType("Scope");
        IAttributeDefinition nameAttributeDefinition = scopeType.getAttributeDefinition("Name");
        Query query = new Query(oid);
        query.getSelection().add(nameAttributeDefinition);
        QueryResult result = services.retrieve(query);
        Asset product = result.getAssets()[0];
        String name = product.getAttribute(nameAttributeDefinition).getValue().toString();
        int requestCount = getPrimaryWorkItemCount("Request", scopeId);
        int issueCount = getPrimaryWorkItemCount("Issue", scopeId);
        int storyCount = getPrimaryWorkItemCount("Story", scopeId);
        int defectCount = getPrimaryWorkItemCount("Defect", scopeId);
        return new RemoteProduct(scopeId, name, requestCount, issueCount, storyCount, defectCount);
    }

    public int getPrimaryWorkItemCount(final String type, final String scopeId) throws ConnectionException, APIException, OidException {
        IAssetType assetType = services.getMeta().getAssetType(type);
        IAttributeDefinition scopeAttributeDefinition = assetType.getAttributeDefinition("Scope");
        IAttributeDefinition assetStateAttributeDefinition = assetType.getAttributeDefinition("AssetState");

        FilterTerm scopeTerm = new FilterTerm(scopeAttributeDefinition);
        scopeTerm.equal("Scope:" + scopeId);

        FilterTerm assetStateTerm = new FilterTerm(assetStateAttributeDefinition);
        assetStateTerm.equal(64);

        GroupFilterTerm groupFilter = new AndFilterTerm(scopeTerm, assetStateTerm);
        Query query = new Query(assetType);
        query.setFilter(groupFilter);
        QueryResult result = services.retrieve(query);
        return result.getAssets().length;
    }

    @Override
    public List<Sprint> getActiveSprintsByProductIdAndName(final String productScopeId, final String productName) throws ConnectionException, APIException, OidException, IOException {
        logger.info("Fetching active sprints for product with scope id " + productScopeId);
        List<Sprint> activeSprints = new ArrayList<Sprint>();
        IAssetType timeboxType = services.getMeta().getAssetType("Timebox");
        IAttributeDefinition nameAttributeDefinition = timeboxType.getAttributeDefinition("Name");
        IAttributeDefinition stateCodeAttributeDefinition = timeboxType.getAttributeDefinition("State.Code");
        IAttributeDefinition scheduleScheduledScopesAttributeDefinition = timeboxType.getAttributeDefinition("Schedule.ScheduledScopes");
        IAttributeDefinition scheduleScheduledScopesNameAttributeDefinition = timeboxType.getAttributeDefinition("Schedule.ScheduledScopes.Name");

        FilterTerm stateCodeTerm = new FilterTerm(stateCodeAttributeDefinition);
        stateCodeTerm.equal("ACTV");

        FilterTerm scheduleScheduledScopesTerm = new FilterTerm(scheduleScheduledScopesAttributeDefinition);
        scheduleScheduledScopesTerm.equal("Scope:" + productScopeId);

        GroupFilterTerm groupFilter = new AndFilterTerm(stateCodeTerm, scheduleScheduledScopesTerm);

        Query query = new Query(timeboxType);
        query.getSelection().add(nameAttributeDefinition);
        query.getSelection().add(scheduleScheduledScopesAttributeDefinition);
        query.getSelection().add(scheduleScheduledScopesNameAttributeDefinition);
        query.setFilter(groupFilter);

        clearMembers();

        QueryResult result = services.retrieve(query);
        for (Asset sprint : result.getAssets()) {
            String id = parseId(sprint.getOid());
            String name = sprint.getAttribute(nameAttributeDefinition).getValue().toString();

            List<Card> cards = getActiveSprintsCards(id);
            activeSprints.add(new Sprint(id, name, productName, cards));
        }
        return activeSprints;
    }

    @Override
    public List<Sprint> getAdditionalActiveSprints() throws Exception {
        // Returns empty array to satisfy interface requirement
        return new ArrayList<Sprint>();
    }

    public List<Card> getActiveSprintsCards(final String timeboxId) throws ConnectionException, APIException, OidException, IOException {
        List<Card> activeSprintsCards = new ArrayList<Card>();

        IAssetType primaryWorkitemType = services.getMeta().getAssetType("PrimaryWorkitem");

        IAttributeDefinition nameAttributeDefinition = primaryWorkitemType.getAttributeDefinition("Name");
        IAttributeDefinition numberAttributeDefinition = primaryWorkitemType.getAttributeDefinition("Number");
        IAttributeDefinition assetTypeAttributeDefinition = primaryWorkitemType.getAttributeDefinition("AssetType");
        IAttributeDefinition descriptionAttributeDefinition = primaryWorkitemType.getAttributeDefinition("Description");
        IAttributeDefinition statusNameAttributeDefinition = primaryWorkitemType.getAttributeDefinition("Status.Name");
        IAttributeDefinition estimateAttributeDefinition = primaryWorkitemType.getAttributeDefinition("Estimate");
        IAttributeDefinition ownersAttributeDefinition = primaryWorkitemType.getAttributeDefinition("Owners");

        IAttributeDefinition timeboxAttributeDefinition = primaryWorkitemType.getAttributeDefinition("Timebox");
        IAttributeDefinition assetStateAttributeDefinition = primaryWorkitemType.getAttributeDefinition("AssetState");

        FilterTerm timboxTerm = new FilterTerm(timeboxAttributeDefinition);
        timboxTerm.equal("Timebox:" + timeboxId);

        FilterTerm assetStateTerm = new FilterTerm(assetStateAttributeDefinition);
        assetStateTerm.equal("64");

        GroupFilterTerm groupFilter = new AndFilterTerm(timboxTerm, assetStateTerm);

        Query query = new Query(primaryWorkitemType);
        query.getSelection().add(nameAttributeDefinition);
        query.getSelection().add(numberAttributeDefinition);
        query.getSelection().add(assetTypeAttributeDefinition);
        query.getSelection().add(descriptionAttributeDefinition);
        query.getSelection().add(statusNameAttributeDefinition);
        query.getSelection().add(estimateAttributeDefinition);
        query.getSelection().add(ownersAttributeDefinition);
        query.setFilter(groupFilter);

        QueryResult result = services.retrieve(query);
        for (Asset card : result.getAssets()) {
            Object temp;
            String id = parseId(card.getOid());
            String name = card.getAttribute(nameAttributeDefinition).getValue().toString();
            String number = card.getAttribute(numberAttributeDefinition).getValue().toString();
            String type = ((IAssetType) card.getAttribute(assetTypeAttributeDefinition).getValue()).getToken();
            String description = (temp = card.getAttribute(descriptionAttributeDefinition).getValue()) != null ? temp.toString() : null;
            String status = (temp = card.getAttribute(statusNameAttributeDefinition).getValue()) != null ? temp.toString() : null;
            String estimate = (temp = card.getAttribute(estimateAttributeDefinition).getValue()) != null ? temp.toString() : null;
            List<Member> assignees = new ArrayList<Member>();
            for (Object member : card.getAttribute(ownersAttributeDefinition).getValues()) {
                String memberId = parseId(member.toString());
                assignees.add(getMember(memberId));
            }
            activeSprintsCards.add(new Card(id, number, mapCardType(type), name, description, mapStatus(status), mapEstimate(estimate), assignees));
        }
        return activeSprintsCards;
    }

    public Member getMember(final String id) throws ConnectionException, APIException, OidException, IOException {
        Member member;
        Optional<Member> cachedMember = getCachedMember(id);
        if (cachedMember.isPresent()) {
            member = cachedMember.get();
        } else {
            Oid oid = services.getOid("Member:" + id);
            IAssetType memberType = services.getMeta().getAssetType("Member");
            IAttributeDefinition nameAttributeDefinition = memberType.getAttributeDefinition("Name");
            IAttributeDefinition avatarAttributeDefinition = memberType.getAttributeDefinition("Avatar");

            Query query = new Query(oid);
            query.getSelection().add(nameAttributeDefinition);
            query.getSelection().add(avatarAttributeDefinition);

            QueryResult result = services.retrieve(query);
            Asset asset = result.getAssets()[0];
            String name = asset.getAttribute(nameAttributeDefinition).getValue().toString();

            String avatarPath = parseAvatarUrlPath((Oid) asset.getAttribute(avatarAttributeDefinition).getValue());

            Optional<URL> avatarUrl = Optional.ofNullable(getClass().getResource("/images/" + avatarPath));
            if (!avatarUrl.isPresent()) {
                storeAvatar(avatarPath);
            }

            member = new Member(id, name, avatarPath);

            cacheMember(id, member);
        }
        return member;
    }

    @Override
    public Object push(FeatureRequest featureRequest) throws V1Exception {
        logger.info("Submitting feature request " + featureRequest.getTitle() + " to product with scope id " + featureRequest.getScopeId());
        IAssetType requestType = services.getMeta().getAssetType("Request");
        IAttributeDefinition nameAttributeDefinition = requestType.getAttributeDefinition("Name");
        IAttributeDefinition descriptionAttributeDefinition = requestType.getAttributeDefinition("Description");
        IAttributeDefinition scopeAttributeDefinition = requestType.getAttributeDefinition("Scope");

        Oid scopeId = services.getOid("Scope:" + featureRequest.getScopeId());

        Asset request = services.createNew(requestType, scopeId);

        request.setAttributeValue(nameAttributeDefinition, featureRequest.getTitle());
        request.setAttributeValue(descriptionAttributeDefinition, featureRequest.getDescription());
        request.setAttributeValue(scopeAttributeDefinition, "Scope:" + featureRequest.getScopeId());

        services.save(request);

        return request;
    }

    private void clearMembers() {
        members.clear();
    }

    private Optional<Member> getCachedMember(final String id) {
        return Optional.ofNullable(members.get(id));
    }

    private void cacheMember(String id, Member member) {
        members.put(id, member);
    }

    private String parseId(Object oid) {
        String id = oid.toString();
        return id.substring(id.indexOf(":") + 1);
    }

    private String parseAvatarUrlPath(Oid imageOid) throws APIException {
        String id = imageOid.toString();
        String url;
        if (id.equals("NULL")) {
            url = "no_avatar.png";
        } else {
            url = parseId(id);
        }
        return url;
    }

    private void storeAvatar(String avatarPath) throws IOException {
        URL imagesPath = getClass().getResource("/images");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(getUrl() + "/image.img/" + avatarPath, HttpMethod.GET, entity, byte[].class, "1");
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            File file = new File(imagesPath.getFile() + "/" + avatarPath);
            Files.write(file.toPath(), response.getBody());
        }
    }

    private String getUrl() {
        String url = getSettingValue("url");
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private V1Connector buildConnector() throws MalformedURLException, V1Exception {
        Optional<String> token = getToken();
        V1Connector connector;
        if (token.isPresent()) {
            // @formatter:off
            connector = V1Connector.withInstanceUrl(getUrl())
                    .withUserAgentHeader("Product Management Service",
                            "1.0")
                .withAccessToken(getToken().get())
                .build();
            // @formatter:on
        } else {
            // @formatter:off
            connector = V1Connector.withInstanceUrl(getUrl())
                    .withUserAgentHeader("Product Management Service", "1.0")
                .withUsernameAndPassword(getUsername(), getPassword())
                .build();
            // @formatter:on
        }
        return connector;
    }

    private RestTemplate getRestTemplate() {
        Optional<String> token = getToken();
        return token.isPresent() ? new TokenAuthRestTemplate(token.get()) : new BasicAuthRestTemplate(getUsername(), getPassword());
    }

    private String getUsername() {
        return getSettingValue("username");
    }

    private String getPassword() {
        return getSettingValue("password");
    }

    private Optional<String> getToken() {
        return managementService.getSettingValue("token");
    }

    private String getSettingValue(String key) {
        Optional<String> setting = managementService.getSettingValue(key);
        if (setting.isPresent()) {
            return setting.get();
        }
        throw new RuntimeException("No setting " + key + " found in settings for service " + managementService.getName());
    }

}