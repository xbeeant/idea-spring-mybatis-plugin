package org.xstudio.plugin.idea.util;

/**
 * @author xiaobiao
 * @version 2021/5/20
 */
public class Field {
    private String name;

    private Class<?> type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Field(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }
}
