package org.xstudio.plugins.idea.ui;

import java.util.HashMap;
import java.util.Map;

public class WebProperty {
    private String field;


    private Map<String, Object> properties = new HashMap<>();

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }
}

