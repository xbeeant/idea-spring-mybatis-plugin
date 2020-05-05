package org.xstudio.plugin.idea.model;

import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 表的生成配置
 *
 * @author xiaobiao
 */
public class TableConfig extends PersistentConfig {
    /**
     * 配置名称
     */
    private String name;
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

    private String modelClass;
    private String serviceInterfaceClass;
    private String serviceImplClass;
    private String facadeInterfaceClass;
    private String facadeImplClass;
    private String mapperClass;
    private String mapperImplClass;

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
        return getBasePackage() + "." + getDatabaseNamePackage();
    }

    public String getMapperTargetPackage() {
        return getResourcePath() + "/mybatis/" + databaseType + "/";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getModelClass() {
        return modelClass;
    }

    public void setModelClass(String modelClass) {
        this.modelClass = modelClass;
    }

    public String getServiceInterfaceClass() {
        return serviceInterfaceClass;
    }

    public void setServiceInterfaceClass(String serviceInterfaceClass) {
        this.serviceInterfaceClass = serviceInterfaceClass;
    }

    public String getServiceImplClass() {
        return serviceImplClass;
    }

    public void setServiceImplClass(String serviceImplClass) {
        this.serviceImplClass = serviceImplClass;
    }

    public String getFacadeInterfaceClass() {
        return facadeInterfaceClass;
    }

    public void setFacadeInterfaceClass(String facadeInterfaceClass) {
        this.facadeInterfaceClass = facadeInterfaceClass;
    }

    public String getFacadeImplClass() {
        return facadeImplClass;
    }

    public void setFacadeImplClass(String facadeImplClass) {
        this.facadeImplClass = facadeImplClass;
    }

    public String getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(String mapperClass) {
        this.mapperClass = mapperClass;
    }

    public String getMapperImplClass() {
        return mapperImplClass;
    }

    public void setMapperImplClass(String mapperImplClass) {
        this.mapperImplClass = mapperImplClass;
    }
}
