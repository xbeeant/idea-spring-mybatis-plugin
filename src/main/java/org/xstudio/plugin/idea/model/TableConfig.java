package org.xstudio.plugin.idea.model;

import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 表的生成配置
 *
 * @author xiaobiao
 */
public class TableConfig extends PersistentConfig {
    /**
     * 数据库类型
     */
    private String databaseType;
    /**
     * 数据库名称
     */
    private String databaseName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 实体名
     */
    private String entityName;
    /**
     * 字段配置
     */
    private Map<String, ColumnSetting> columnSettings = new HashMap<>();

    public String getEntityName() {
        if (null != getTablePrefix() && null != tableName) {
            return JavaBeansUtil.getCamelCaseString(tableName.replace(getTablePrefix(), ""), true);
        }
        return entityName;
    }

    public String getDatabaseNamePackage() {
        return databaseName.replaceAll("_", ".");
    }

    public String getTargetPackage() {
        return getRootPackage() + "." + getDatabaseNamePackage();
    }

    public String getMapperTargetPackage() {
        return getResourcePath() + File.separator + "mybatis" + File.separator + databaseType + File.separator;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Map<String, ColumnSetting> getColumnSettings() {
        return columnSettings;
    }

    public void setColumnSettings(Map<String, ColumnSetting> columnSettings) {
        this.columnSettings = columnSettings;
    }
}
