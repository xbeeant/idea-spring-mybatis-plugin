package org.xstudio.plugin.idea.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 表的生成配置
 *
 * @author xiaobiao
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
}
