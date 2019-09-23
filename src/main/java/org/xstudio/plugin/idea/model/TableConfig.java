package org.xstudio.plugin.idea.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 表的生成配置
 *
 * @author xiaobiao
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableConfig extends ProjectConfig {
    /**
     * 配置名称
     */
    private String name;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 实体名
     */
    private String entityName;
    /**
     * dao名称
     */
    private String mapperName;
    /**
     * 字段配置
     */
    private Map<String, ColumnSetting> columnSettings = new HashMap<>();

    private String modelClass;
    private String iServiceClass;
    private String serviceImplClass;
    private String iFacadeClass;
    private String facadeImplClass;
    private String mapperClass;
    private String mapperImpl;
}
