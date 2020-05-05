package org.xstudio.plugin.idea.model;

/**
 * @author xiaobiao
 * @version 2019/9/23
 */
public class PersistentConfig extends MybatisPluginConfig {
    private String name;
    /**
     * base package
     */
    private String basePackage;
    /**
     * ID generator
     */
    private String idGenerator = "com.xstudio.core.IdWorker.getId";
    /**
     * 基础 service interface
     */
    private String iService = "com.xstudio.spring.mybatis.pagehelper.IMybatisPageHelperService";
    /**
     * 基础service实现
     */
    private String serviceImpl = "com.xstudio.discuzx.config.AbstractSecurityMybatisPageHelperServiceImpl";
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
    private String iDao = "com.xstudio.spring.mybatis.pagehelper.IMybatisPageHelperDao";
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
     * 是否覆盖
     */
    private boolean override = false;
    /**
     * 是否使用Example
     */
    private boolean useExample = false;
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
    private String sourcePath = "/src/main/java";
    /**
     * 资源文件地址
     */
    private String resourcePath = "/src/main/resources";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(String idGenerator) {
        this.idGenerator = idGenerator;
    }

    public String getIService() {
        return iService;
    }

    public void setIService(String iService) {
        this.iService = iService;
    }

    public String getServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    public String getIFacade() {
        return iFacade;
    }

    public void setIFacade(String iFacade) {
        this.iFacade = iFacade;
    }

    public String getFacadeImpl() {
        return facadeImpl;
    }

    public void setFacadeImpl(String facadeImpl) {
        this.facadeImpl = facadeImpl;
    }

    public String getIDao() {
        return iDao;
    }

    public void setIDao(String iDao) {
        this.iDao = iDao;
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

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public boolean isUseExample() {
        return useExample;
    }

    public void setUseExample(boolean useExample) {
        this.useExample = useExample;
    }

    public boolean isUseSchemaPrefix() {
        return useSchemaPrefix;
    }

    public void setUseSchemaPrefix(boolean useSchemaPrefix) {
        this.useSchemaPrefix = useSchemaPrefix;
    }

    public boolean isUseActualColumnNames() {
        return useActualColumnNames;
    }

    public void setUseActualColumnNames(boolean useActualColumnNames) {
        this.useActualColumnNames = useActualColumnNames;
    }

    public boolean isUseTableNameAlias() {
        return useTableNameAlias;
    }

    public void setUseTableNameAlias(boolean useTableNameAlias) {
        this.useTableNameAlias = useTableNameAlias;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isAnnotation() {
        return annotation;
    }

    public void setAnnotation(boolean annotation) {
        this.annotation = annotation;
    }

    public boolean isMysql8() {
        return mysql8;
    }

    public void setMysql8(boolean mysql8) {
        this.mysql8 = mysql8;
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
