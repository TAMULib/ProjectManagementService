package edu.tamu.app.service.versioning;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1Connector;
import com.versionone.apiclient.filters.FilterTerm;
import com.versionone.apiclient.interfaces.IAssetType;
import com.versionone.apiclient.interfaces.IAttributeDefinition;
import com.versionone.apiclient.interfaces.IServices;
import com.versionone.apiclient.services.QueryResult;

import edu.tamu.app.model.ManagementService;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.Sprint;
import edu.tamu.app.model.request.FeatureRequest;
import edu.tamu.app.model.response.VersionProject;

public class VersionOneService implements VersionManagementSoftwareBean {

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
        System.out.println("\n\n\nin method\n\n\n");
        List<Sprint> sprints = new ArrayList<Sprint>();
        IAssetType timeboxType = services.getMeta().getAssetType("Timebox");
        IAttributeDefinition nameAttribute = timeboxType.getAttributeDefinition("Name");

        Query query = new Query(timeboxType);
        query.getSelection().add(nameAttribute);

        String scheduleId = getScheduleIdByScope(project.getScopeId());
        IAttributeDefinition scheduleAttribute = timeboxType.getAttributeDefinition("Schedule");
        FilterTerm scheduleTerm = new FilterTerm(scheduleAttribute);
        scheduleTerm.equal(scheduleId);
        query.setFilter(scheduleTerm);

        QueryResult result = services.retrieve(query);

        for (Asset sprint : result.getAssets()) {
            sprints.add(new Sprint(sprint.getAttribute(nameAttribute).toString(), project.getName()));
            System.out.println("\n\nProject: " + project.getName() + "\n\n");
        }

        return sprints;
    }

    private String getScheduleIdByScope(String scopeId) throws Exception {
        Oid oid = services.getOid("Scope:" + scopeId);
        IAssetType scopeType = services.getAssetType("Scope");
        Query query = new Query(oid);
        IAttributeDefinition scheduleAttribute = scopeType.getAttributeDefinition("Schedule");
        query.getSelection().add(scheduleAttribute);
        QueryResult result = services.retrieve(query);
        Asset scope = result.getAssets()[0];
        System.out.println("\n\n" + scope.getAttribute(scheduleAttribute).toString() + "\n\n");
        return scope.getAttribute(scheduleAttribute).toString();
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
