package org.xstudio.plugin.idea.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiaobiao
 * @version 2019/9/23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PersistentConfig extends MybatisPluginConfig {
    /**
     * base package
     */
    private String basePackage;
    /**
     * ID generator
     */
    private String idGenerator;
    /**
     * 基础 service interface
     */
    private String iService;
    /**
     * 基础service实现
     */
    private String serviceImpl;
    /**
     * 基础facade interface
     */
    private String iFacade;
    /**
     * 基础facade实现
     */
    private String facadeImpl;
    /**
     * 基础dao
     */
    private String iDao;
    /**
     * 基础对象
     */
    private String baseObject;
    /**
     * 默认忽略的字段
     */
    private String ignoreColumn;
    /**
     * 不模糊搜索
     */
    private String nonFuzzyColumn;
    /**
     * 表前缀
     */
    private String tablePrefix;
    /**
     * 是否覆盖
     */
    private boolean override = false;
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
    private boolean comment = true;
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
    /**
     * 源码地址
     */
    private String sourcePath = "src/main/java";
    /**
     * 资源文件地址
     */
    private String resourcePath = "src/main/resources";

}
