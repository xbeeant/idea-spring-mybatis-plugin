package org.xstudio.plugin.idea.mybatis.generator;

import java.io.Serializable;

/**
 * @author huangxiaobiao
 */
public class PersistentProperties implements Serializable {

    private String cfgName = "pagehelper";
    private String facadeImpl = "";
    private String facadeInterface = "";
    private String idGenerator = "io.github.xbeeant.core.IdWorker";
    private String ignoreColumns = "create_by,update_by,create_at,update_at";
    private String mapperInterface = "io.github.xbeeant.spring.mybatis.pagehelper.IMybatisPageHelperDao";
    private String nonFuzzySearchColumn = "create_by, update_by";
    private PluginProperties plugin = new PluginProperties();
    private String replaceString = "";
    private String resourcePath = "/src/main/resources";
    private String rootClass = "io.github.xbeeant.core.BaseModelObject";
    private String rootPackage = "io.github.xbeeant";
    private String searchString = "";
    private String serviceImpl = "io.github.xbeeant.config.AbstractSecurityMybatisPageHelperServiceImpl";
    private String serviceInterface = "io.github.xbeeant.spring.mybatis.pagehelper.IMybatisPageHelperService";
    private String srcPath = "/src/main/java";
    private String responseObject = "io.github.xbeeant.ApiResponse";

    public String getCfgName() {
        return cfgName;
    }

    public void setCfgName(String cfgName) {
        this.cfgName = cfgName;
    }

    public String getFacadeImpl() {
        return facadeImpl;
    }

    public void setFacadeImpl(String facadeImpl) {
        this.facadeImpl = facadeImpl;
    }

    public String getFacadeInterface() {
        return facadeInterface;
    }

    public void setFacadeInterface(String facadeInterface) {
        this.facadeInterface = facadeInterface;
    }

    public String getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(String idGenerator) {
        this.idGenerator = idGenerator;
    }

    public String getIgnoreColumns() {
        return ignoreColumns;
    }

    public void setIgnoreColumns(String ignoreColumns) {
        this.ignoreColumns = ignoreColumns;
    }

    public String getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(String mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public String getNonFuzzySearchColumn() {
        return nonFuzzySearchColumn;
    }

    public void setNonFuzzySearchColumn(String nonFuzzySearchColumn) {
        this.nonFuzzySearchColumn = nonFuzzySearchColumn;
    }

    public PluginProperties getPlugin() {
        return plugin;
    }

    public void setPlugin(PluginProperties plugin) {
        this.plugin = plugin;
    }

    public String getReplaceString() {
        return replaceString;
    }

    public void setReplaceString(String replaceString) {
        this.replaceString = replaceString;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getRootClass() {
        return rootClass;
    }

    public void setRootClass(String rootClass) {
        this.rootClass = rootClass;
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    private String tableNamePkg(String tableName) {
        return tableName.replaceAll("_", ".").replaceAll("-", ".");
    }

    public String getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(String responseObject) {
        this.responseObject = responseObject;
    }
}
