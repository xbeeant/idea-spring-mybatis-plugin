package org.xstudio.plugin.idea.mybatis.generator;

/**
 * @author huangxiaobiao
 */
public class ProjectPersistentProperties extends PersistentProperties {
    private String schema;

    private String database;

    private String type;

    private String modulePath;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }
}
