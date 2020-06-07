package org.xstudio.plugin.idea.model;

/**
 * @author xiaobiao
 * @version 2019/9/23
 */
public class PersistentConfig extends MybatisPluginConfig {
    /**
     * 配置名称
     */
    private String configName;
    /**
     * base package
     */
    private String rootPackage;
    /**
     * ID generator
     */
    private String idGenerator = "com.xstudio.core.IdWorker.getId";
    /**
     * 基础 service interface
     */
    private String serviceInterface = "com.xstudio.spring.mybatis.pagehelper.IMybatisPageHelperService";
    /**
     * 基础service实现
     */
    private String serviceImplement = "com.xstudio.discuzx.config.AbstractSecurityMybatisPageHelperServiceImpl";
    /**
     * 基础facade interface
     */
    private String facadeInterface;
    /**
     * 基础facade实现
     */
    private String facadeImplement;
    /**
     * 基础dao
     */
    private String daoInterface = "com.xstudio.spring.mybatis.pagehelper.IMybatisPageHelperDao";
    /**
     * 基础对象
     */
    private String baseObject = "com.xstudio.core.BaseModelObject";
    /**
     * 默认忽略的字段
     */
    private String ignoreColumn = "create_at, create_by, update_at, update_by";
    /**
     * 不模糊搜索
     */
    private String nonFuzzyColumn;
    /**
     * 表前缀
     */
    private String tablePrefix;

    /**
     * 工程目录
     */
    private String moduleRootPath;
    /**
     * 源码地址
     */
    private String sourcePath = "/src/main/java";
    /**
     * 资源文件地址
     */
    private String resourcePath = "/src/main/resources";

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }

    public String getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(String idGenerator) {
        this.idGenerator = idGenerator;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getServiceImplement() {
        return serviceImplement;
    }

    public void setServiceImplement(String serviceImplement) {
        this.serviceImplement = serviceImplement;
    }

    public String getFacadeInterface() {
        return facadeInterface;
    }

    public void setFacadeInterface(String facadeInterface) {
        this.facadeInterface = facadeInterface;
    }

    public String getFacadeImplement() {
        return facadeImplement;
    }

    public void setFacadeImplement(String facadeImplement) {
        this.facadeImplement = facadeImplement;
    }

    public String getDaoInterface() {
        return daoInterface;
    }

    public void setDaoInterface(String daoInterface) {
        this.daoInterface = daoInterface;
    }

    public String getBaseObject() {
        return baseObject;
    }

    public void setBaseObject(String baseObject) {
        this.baseObject = baseObject;
    }

    public String getIgnoreColumn() {
        return ignoreColumn;
    }

    public void setIgnoreColumn(String ignoreColumn) {
        this.ignoreColumn = ignoreColumn;
    }

    public String getNonFuzzyColumn() {
        return nonFuzzyColumn;
    }

    public void setNonFuzzyColumn(String nonFuzzyColumn) {
        this.nonFuzzyColumn = nonFuzzyColumn;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getModuleRootPath() {
        return moduleRootPath;
    }

    public void setModuleRootPath(String moduleRootPath) {
        this.moduleRootPath = moduleRootPath;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}
