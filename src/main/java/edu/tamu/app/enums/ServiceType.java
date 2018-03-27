package edu.tamu.app.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ServiceType {

    VERSION_ONE("Version One");

    private String gloss;

    private static List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    static {
        for (ServiceType type : ServiceType.values()) {
            Map<String, String> m = new HashMap<String, String>();
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
    
    public Map<String, Object> getScaffold() {
        Map<String, Object> scaffold = new HashMap<String, Object>();
        switch(this) {
        case VERSION_ONE:
            scaffold.put("url", "String");
            scaffold.put("username", "String");
            scaffold.put("password", "String");
            break;
        default:
            break;
        
        }
        return scaffold;
    }

    public static List<Map<String, String>> map() {
        return list;
    }

}
