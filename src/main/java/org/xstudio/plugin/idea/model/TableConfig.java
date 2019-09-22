package org.xstudio.plugin.idea.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 表的生成配置
 *
 * @author xiaobiao
 */
@Data
public class TableConfig {

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
     * 表前缀
     */
    private String prefix;

    private boolean override;
    /**
     * 是否使用Example
     */
    private boolean useExample;
    /**
     * 是否使用Schema前缀
     */
    private boolean useSchemaPrefix;
    /**
     * 是否使用实际的列名
     */
    private boolean useActualColumnNames;

    /**
     * 是否启用as别名查询
     */
    private boolean useTableNameAlias;
    /**
     * 是否生成实体注释（来自表）
     */
    private boolean comment;
    /**
     * 是否生成JPA注解
     */
    private boolean annotation;
    /**
     * 是否是mysql8数据库
     */
    private boolean mysql8;


    /**
     * 工程目录
     */
    private String moduleRootPath;

    private String sourcePath;
    private String resourcePath;
    private String xmlPackage;
    private String mapperPackage;


}
