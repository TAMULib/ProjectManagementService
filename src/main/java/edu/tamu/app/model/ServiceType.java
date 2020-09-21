package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ServiceType {

    VERSION_ONE("Version One"),
    GITHUB("GitHub");

    private String gloss;

    private static List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    static {
        for (ServiceType type : ServiceType.values()) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("value", type.toString());
            m.put("gloss", type.gloss);
            list.add(m);
        }
    }

    ServiceType(String gloss) {
        this.gloss = gloss;
    }

    public String getGloss() {
        return this.gloss;
    }

    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

    public static List<Map<String, Object>> map() {
        return list;
    }

    public static class Setting {
        private final String type;
        private final String key;
        private final String gloss;
        private final boolean visible;

        public Setting(String type, String key, String gloss, boolean visible) {
            this.type = type;
            this.key = key;
            this.gloss = gloss;
            this.visible = visible;
        }

        public String getType() {
            return type;
        }

        public String getKey() {
            return key;
        }

        public String getGloss() {
            return gloss;
        }

        public boolean isVisible() {
            return visible;
        }

    }

}
