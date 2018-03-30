package edu.tamu.app.utility;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.model.response.VersionProject;

public class JsonNodeUtility {

    public static String getVersionProjectName(JsonNode asset) {
        return asset.get("Attributes").get("Name").get("value").asText();
    }

    public static String getVersionProjectScopeId(JsonNode asset) {
        return asset.get("id").asText().replaceAll("Scope:", "");
    }

    public static List<VersionProject> getVersionProjects(JsonNode assets) {
        List<VersionProject> versionProjects = new ArrayList<VersionProject>();
        assets.forEach(asset -> {
            String name = getVersionProjectName(asset);
            String scopeId = getVersionProjectScopeId(asset);
            versionProjects.add(new VersionProject(name, scopeId));
        });
        return versionProjects;
    }

}
