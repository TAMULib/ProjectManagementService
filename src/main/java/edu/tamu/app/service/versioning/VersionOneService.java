package edu.tamu.app.service.versioning;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.AttributeSelection;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1Connector;
import com.versionone.apiclient.filters.AndFilterTerm;
import com.versionone.apiclient.filters.FilterTerm;
import com.versionone.apiclient.filters.GroupFilterTerm;
import com.versionone.apiclient.interfaces.IAssetType;
import com.versionone.apiclient.interfaces.IAttributeDefinition;
import com.versionone.apiclient.interfaces.IServices;
import com.versionone.apiclient.services.QueryResult;

import edu.tamu.app.model.Assignee;
import edu.tamu.app.model.Card;
import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.Sprint;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.response.VersionProject;

public class VersionOneService implements VersionManagementSoftwareBean {

    // Inactive value is 128
    private static final int ACTIVE_ASSET_STATE = 64;

    private ManagementService managementService;

    private IServices services;

    public VersionOneService(ManagementService managementService) {
        this.managementService = managementService;
        try {
            V1Connector connector = V1Connector.withInstanceUrl(getUrl()).withUserAgentHeader("Project Management Testing", "GR-01001").withAccessToken(getPassword()).build();
            services = new Services(connector);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }

    }

    @Override
    public List<VersionProject> getVersionProjects() throws Exception {
        List<VersionProject> versionProjects = new ArrayList<VersionProject>();
        IAssetType scopeType = services.getMeta().getAssetType("Scope");
        Query query = new Query(scopeType);
        IAttributeDefinition nameAttribute = scopeType.getAttributeDefinition("Name");
        query.getSelection().add(nameAttribute);
        QueryResult result = services.retrieve(query);

        for (Asset scope : result.getAssets()) {
            versionProjects.add(new VersionProject(scope.getAttribute(nameAttribute).getValue().toString(), scope.getOid().toString().split(":")[1]));
        }

        return versionProjects;
    }

    @Override
    public VersionProject getVersionProjectByScopeId(String id) throws Exception {
        Oid scopeId = services.getOid("Scope:" + id);
        IAssetType scopeType = services.getMeta().getAssetType("Scope");
        Query query = new Query(scopeId);
        IAttributeDefinition nameAttribute = scopeType.getAttributeDefinition("Name");
        query.getSelection().add(nameAttribute);
        QueryResult result = services.retrieve(query);
        Asset scope = result.getAssets()[0];
        return new VersionProject(scope.getAttribute(nameAttribute).getValue().toString(), id);
    }

    @Override
    public List<Sprint> getActiveSprintsByProject(Project project) throws Exception {
        List<Sprint> sprints = new ArrayList<Sprint>();
        IAssetType timeboxType = services.getMeta().getAssetType("Timebox");
        IAttributeDefinition nameAttribute = timeboxType.getAttributeDefinition("Name");

        Query query = new Query(timeboxType);
        query.getSelection().add(nameAttribute);

        IAttributeDefinition scopeAttribute = timeboxType.getAttributeDefinition("Schedule.ScheduledScopes");
        IAttributeDefinition stateCodeAttribute = timeboxType.getAttributeDefinition("State.Code");
        FilterTerm scopeTerm = new FilterTerm(scopeAttribute);
        FilterTerm stateCodeTerm = new FilterTerm(stateCodeAttribute);
        scopeTerm.equal("Scope:" + project.getScopeId());
        stateCodeTerm.equal("ACTV");
        GroupFilterTerm groupFilter = new AndFilterTerm(scopeTerm, stateCodeTerm);
        query.setFilter(groupFilter);

        QueryResult result = services.retrieve(query);

        for (Asset sprint : result.getAssets()) {
            sprints.add(new Sprint(sprint.getOid().toString(), sprint.getAttribute(nameAttribute).getValue().toString(), project.getName(), getCardsBySprint(sprint.getOid().toString())));
        }

        return sprints;
    }

    @Override
    public List<Card> getCardsBySprint(String sprintId) throws Exception {
        List<Card> cards = new ArrayList<Card>();
        IAssetType primaryWorkitemAsset = services.getMeta().getAssetType("PrimaryWorkitem");
        AttributeSelection selection = new AttributeSelection();
        IAttributeDefinition statusNameAttribute = primaryWorkitemAsset.getAttributeDefinition("Status.Name");
        IAttributeDefinition nameAttribute = primaryWorkitemAsset.getAttributeDefinition("Name");
        IAttributeDefinition numberAttribute = primaryWorkitemAsset.getAttributeDefinition("Number");
        IAttributeDefinition descriptionAttribute = primaryWorkitemAsset.getAttributeDefinition("Description");
        IAttributeDefinition ownersAttribute = primaryWorkitemAsset.getAttributeDefinition("Owners");
        IAttributeDefinition assetTypeAttribute = primaryWorkitemAsset.getAttributeDefinition("AssetType");
        IAttributeDefinition timeboxAttribute = primaryWorkitemAsset.getAttributeDefinition("Timebox");
        selection.add(nameAttribute);
        selection.add(numberAttribute);
        selection.add(descriptionAttribute);
        selection.add(ownersAttribute);
        selection.add(statusNameAttribute);
        selection.add(assetTypeAttribute);

        selection.add(timeboxAttribute);

        Query query = new Query(primaryWorkitemAsset);
        query.getSelection().addAll(selection);

        IAttributeDefinition assetStateAttribute = primaryWorkitemAsset.getAttributeDefinition("AssetState");
        FilterTerm timeboxTerm = new FilterTerm(timeboxAttribute);
        FilterTerm assetStateTerm = new FilterTerm(assetStateAttribute);
        assetStateTerm.equal(ACTIVE_ASSET_STATE);
        timeboxTerm.equal(sprintId);
        GroupFilterTerm groupFilter = new AndFilterTerm(assetStateTerm, timeboxTerm);
        query.setFilter(groupFilter);
        QueryResult result = services.retrieve(query);

        for (Asset card : result.getAssets()) {
            Object number = card.getAttribute(numberAttribute).getValue().toString();
            Object name = card.getAttribute(nameAttribute).getValue().toString();
            Object description = card.getAttribute(descriptionAttribute).getValue();
            Object status = card.getAttribute(statusNameAttribute).getValue();
            Object cardType = ((IAssetType) card.getAttribute(assetTypeAttribute).getValue()).getToken();
            cards.add(new Card(number == null ? "" : number.toString(), name == null ? "" : name.toString(), description == null ? "" : description.toString(), new ArrayList<Assignee>(), status == null ? "" : status.toString(), cardType == null ? "" : cardType.toString()));
        }

        return cards;
    }

    @Override
    public FeatureRequest push(FeatureRequest request) throws Exception {
        Oid scopeId = services.getOid("Scope:" + request.getScopeId());
        IAssetType requestType = services.getMeta().getAssetType("Request");
        Asset newRequest = services.createNew(requestType, scopeId);
        IAttributeDefinition nameAttribute = requestType.getAttributeDefinition("Name");
        IAttributeDefinition descriptionAttribute = requestType.getAttributeDefinition("Description");
        IAttributeDefinition scopeAttribute = requestType.getAttributeDefinition("Scope");

        newRequest.setAttributeValue(nameAttribute, request.getTitle());
        newRequest.setAttributeValue(descriptionAttribute, request.getDescription());
        newRequest.setAttributeValue(scopeAttribute, "Scope:" + request.getScopeId());

        services.save(newRequest);

        return request;
    }

    private String getUrl() {
        return getSettingValue("url");
    }

    private String getPassword() {
        return getSettingValue("password");
    }

    private String getSettingValue(String key) {
        Optional<String> setting = managementService.getSettingValue(key);
        if (setting.isPresent()) {
            return setting.get();
        }
        throw new RuntimeException("No setting " + key + " found in settings for service " + managementService.getName());
    }

}
