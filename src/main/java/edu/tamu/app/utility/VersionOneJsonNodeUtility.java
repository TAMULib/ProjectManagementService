package edu.tamu.app.utility;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.model.response.RemoteProject;

public class VersionOneJsonNodeUtility {

    public static String getRemoteProjectName(JsonNode asset) {
        return asset.get("Attributes").get("Name").get("value").asText();
    }

    public static String getRemoteProjectScopeId(JsonNode asset) {
        return asset.get("id").asText().replaceAll("Scope:", "");
    }

    public static List<RemoteProject> getRemoteProjects(JsonNode assets) {
        List<RemoteProject> versionProjects = new ArrayList<RemoteProject>();
        assets.forEach(asset -> {
            String name = getRemoteProjectName(asset);
            String scopeId = getRemoteProjectScopeId(asset);
            versionProjects.add(new RemoteProject(name, scopeId));
        });
        return versionProjects;
    }

}