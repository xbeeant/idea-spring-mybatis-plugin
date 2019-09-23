package org.xstudio.plugin.idea.mybatis;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.notification.*;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.model.DbType;
import org.xstudio.plugin.idea.model.TableConfig;
import org.xstudio.plugin.idea.mybatis.generator.MergeableShellCallback;

import javax.swing.event.HyperlinkEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author xiaobiao
 * @version 2019/9/20
 */
public class MyBatisGenerateCommand {
    private String username;
    /**
     * 数据库类型
     */
    private String databaseType;
    /**
     * 数据库驱动
     */
    private String driverClass;
    /**
     * 数据库连接url
     */
    private String databaseUrl;

    /**
     * 生成器对表的配置
     */
    private TableConfig generatorConfig;

    public MyBatisGenerateCommand(TableConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    /**
     * 自动生成的主逻辑
     *
     * @param project          {@link Project}
     * @param connectionConfig {@link RawConnectionConfig}
     */
    public void execute(Project project, RawConnectionConfig connectionConfig) {

        setDatabaseInfo(connectionConfig);

        // ========================================================
        // mybatis code generate configuration
        // ========================================================

        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        Configuration configuration = new Configuration();
        Context context = new Context(ModelType.CONDITIONAL);
        context.setId("id");
        configuration.addContext(context);
        context.addProperty("autoDelimitKeywords", "true");
        context.addProperty("beginningDelimiter", "`");
        context.addProperty("endingDelimiter", "`");
        context.addProperty("javaFileEncoding", "UTF-8");
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, "UTF-8");

        context.setTargetRuntime("MyBatis3");


        // ================================= 必须 配置 ===================================================================

        // 数据库设置
        JDBCConnectionConfiguration jdbcConfig = buildJdbcConfig();
        context.setJdbcConnectionConfiguration(jdbcConfig);
        // 生成表对象设置
        JavaModelGeneratorConfiguration modelConfig = buildModelConfig();
        context.setJavaModelGeneratorConfiguration(modelConfig);
        // 生成表设置
        TableConfiguration tableConfig = buildTableConfig(context);
        context.addTableConfiguration(tableConfig);

        SqlMapGeneratorConfiguration mapperConfig = buildMapperXmlConfig();
        context.setSqlMapGeneratorConfiguration(mapperConfig);
        // 生成 dao 接口 配置
        JavaClientGeneratorConfiguration daoConfig = buildMapperConfig();
        context.setJavaClientGeneratorConfiguration(daoConfig);
        // 生成 注释配置
        CommentGeneratorConfiguration commentConfig = buildCommentConfig();
        context.setCommentGeneratorConfiguration(commentConfig);

        // override=true
        ShellCallback shellCallback;
        if (this.generatorConfig.isOverride()) {
            shellCallback = new DefaultShellCallback(true);
        } else {
            shellCallback = new MergeableShellCallback(true);
        }
        // =====================================
        // plugins config
        // =====================================

        PluginConfiguration pluginConfiguration;

        // swagger2 注解插件
        if(generatorConfig.isSwagger2Plugin()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.ModelSwaggerPropertyAnnotationPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

        // 标记移除插件配置
        if(generatorConfig.isMarkDeletePlugin()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.ModelMarkDeleteFieldPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

        // 父对象插件配置
        if(generatorConfig.isRootObjectPlugin()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.ModelRootObjectPlugin");
            pluginConfiguration.addProperty("rootObject", generatorConfig.getBaseObject());
            pluginConfiguration.addProperty("generateGetSetKey", "true");
            pluginConfiguration.addProperty("excludeFields", generatorConfig.getIgnoreColumn());
            context.addPluginConfiguration(pluginConfiguration);

            // 给日期类型添加 ***Begin ***End 属性
            // ModelBeginEndFieldPlugin
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.ModelBeginEndFieldPlugin");
            context.addPluginConfiguration(pluginConfiguration);

            // 没有blob对象
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.ModelNoBlobsPlugin");
            context.addPluginConfiguration(pluginConfiguration);

            // dao 继承父类
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.ClientRootPlugin");
            pluginConfiguration.addProperty("rootClient", generatorConfig.getBaseObject());
            pluginConfiguration.addProperty("excludeMethods", "countByExample" +
                    ",deleteByExample,deleteByPrimaryKey," +
                    ",insert" +
                    ",insertSelective" +
                    ",selectByExample" +
                    ",selectByPrimaryKey" +
                    ",selectByExampleWithBLOBs" +
                    ",updateByExampleSelective" +
                    ",updateByExample" +
                    ",updateByPrimaryKeySelective" +
                    ",updateByExampleWithBLOBs" +
                    ",updateByPrimaryKeyWithBLOBs" +
                    ",updateByPrimaryKey");
            pluginConfiguration.addProperty("excludeMapper", "deleteByExample" +
                    ",insert" +
                    ",updateByExample" +
                    ",updateByPrimaryKey" +
                    ",updateByPrimaryKeyWithBLOBs" +
                    ",updateByExampleWithBLOBs"
            );
            context.addPluginConfiguration(pluginConfiguration);


            // mapper 模糊搜索插件
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.MapperFuzzySearchPlugin");
            pluginConfiguration.addProperty("nonFuzzyColumn", generatorConfig.getNonFuzzyColumn());
            context.addPluginConfiguration(pluginConfiguration);

            // service 代码服务
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.ServicePlugin");
            pluginConfiguration.addProperty("idGenerator", generatorConfig.getIdGenerator());
            pluginConfiguration.addProperty("rootClient", generatorConfig.getBaseObject());
            pluginConfiguration.addProperty("rootService", generatorConfig.getIService());
            pluginConfiguration.addProperty("rootServiceImpl", generatorConfig.getServiceImpl());
            pluginConfiguration.addProperty("package", generatorConfig.getBasePackage());
            pluginConfiguration.addProperty("generateGetSetKeyValue", "false");
            context.addPluginConfiguration(pluginConfiguration);
        }

        // json 序列化注解插件配置
        if(generatorConfig.isFastjsonPlugin()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.ModelFieldJsonSerializePlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

        // facade service 代码服务
        if (generatorConfig.isFacadePlugin()) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.FacadePlugin");
            pluginConfiguration.addProperty("rootClient", generatorConfig.getBasePackage());
            pluginConfiguration.addProperty("rootService", generatorConfig.getIService());
            pluginConfiguration.addProperty("rootFacadeService", generatorConfig.getIFacade());
            pluginConfiguration.addProperty("rootFacadeServiceImpl", generatorConfig.getFacadeImpl());
            pluginConfiguration.addProperty("package", generatorConfig.getFacadeImpl());
            context.addPluginConfiguration(pluginConfiguration);
        }

        // lombox
        if(generatorConfig.isLombokPlugin()){
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.xstudio.plugin.mybatis.ModelLombokPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

        // ===============================
        // 序列化插件配置
        // SerializablePlugin
        pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
        pluginConfiguration.addProperty("suppressJavaInterface", "false");
        pluginConfiguration.addProperty("addGWTInterface", "false");
        context.addPluginConfiguration(pluginConfiguration);

        try {
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, shellCallback, warnings);

            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
            Balloon balloon = JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder("Generating Code...", MessageType.INFO, null)
                    .createBalloon();
            balloon.show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);

            Task.Backgroundable generateTask = new Task.Backgroundable(project, Constant.TITLE, false) {
                @Override
                public void run(ProgressIndicator indicator) {
                    indicator.setText(Constant.TITLE);
                    indicator.setFraction(0.0);
                    indicator.setIndeterminate(true);
                    try {
                        // 生成代码
                        myBatisGenerator.generate(null);
                        VirtualFile baseDir = project.getWorkspaceFile();
                        // 刷新工程
                        baseDir.refresh(false, true);

                        NotificationGroup balloonNotifications = new NotificationGroup(Constant.TITLE, NotificationDisplayType.STICKY_BALLOON, true);

                        Notification notification = balloonNotifications.createNotification("Generate Successfully", "<html>" + String.join("<br/>", "todo") + "</html>", NotificationType.INFORMATION, (notification1, hyperlinkEvent) -> {
                            if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                new OpenFileDescriptor(project, Objects.requireNonNull(project.getBaseDir().findFileByRelativePath(hyperlinkEvent.getDescription()))).navigate(true);
                            }
                        });
                        Notifications.Bus.notify(notification);
                    } catch (Exception e) {
                        e.printStackTrace();
                        balloon.hide();
                        Notification notification = new Notification(Constant.TITLE, null, NotificationType.ERROR);
                        notification.setTitle("Generate Failed");
                        notification.setContent("Cause:" + e.getMessage());
                        Notifications.Bus.notify(notification);
                    }
                }
            };
            generateTask.setCancelText("Stop Generate Code").queue();
            generateTask.setCancelTooltipText("Stop generate mybatis spring code");
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showMessageDialog(e.getMessage(), "Generate Failure", Messages.getInformationIcon());
        }

    }

    /**
     * 设置数据库的信息
     *
     * @param connectionConfig {@link RawConnectionConfig}
     */
    private void setDatabaseInfo(RawConnectionConfig connectionConfig) {
        driverClass = connectionConfig.getDriverClass();
        databaseUrl = connectionConfig.getUrl();
        if (driverClass.contains("mysql")) {
            databaseType = "MySQL";
        } else if (driverClass.contains("oracle")) {
            databaseType = "Oracle";
        } else if (driverClass.contains("postgresql")) {
            databaseType = "PostgreSQL";
        } else if (driverClass.contains("sqlserver")) {
            databaseType = "SqlServer";
        } else if (driverClass.contains("sqlite")) {
            databaseType = "Sqlite";
        } else if (driverClass.contains("mariadb")) {
            databaseType = "MariaDB";
        }
    }

    /**
     * 生成数据库连接配置
     *
     * @return JDBCConnectionConfiguration
     */
    private JDBCConnectionConfiguration buildJdbcConfig() {
        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
        jdbcConfig.addProperty("nullCatalogMeansCurrent", "true");
        jdbcConfig.addProperty("remarks", "true");
        jdbcConfig.addProperty("useInformationSchema", "true");

        CredentialAttributes credentialAttributes = new CredentialAttributes("mybatis-generator-" + databaseUrl, username, this.getClass(), false);
        String password = PasswordSafe.getInstance().getPassword(credentialAttributes);

        jdbcConfig.setUserId(username);
        jdbcConfig.setPassword(password);

        Boolean isMysql8 = generatorConfig.isMysql8();
        if (isMysql8) {
            driverClass = DbType.MySQL_8.getDriverClass();
            databaseUrl += "?serverTimezone=UTC&useSSL=false";
        } else {
            databaseUrl += "?useSSL=false";
        }

        jdbcConfig.setDriverClass(driverClass);
        jdbcConfig.setConnectionURL(databaseUrl);
        return jdbcConfig;
    }

    /**
     * 生成实体类配置
     *
     * @return {@link JavaModelGeneratorConfiguration}
     */
    private JavaModelGeneratorConfiguration buildModelConfig() {
        String projectFolder = generatorConfig.getModuleRootPath();
        String entityPackage = generatorConfig.getModelClass();
        String sourcePath = generatorConfig.getSourcePath();

        JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();

        if (!StringUtils.isEmpty(entityPackage)) {
            modelConfig.setTargetPackage(entityPackage);
        } else {
            modelConfig.setTargetPackage("");
        }
        modelConfig.setTargetProject(projectFolder + "/" + sourcePath + "/");
        return modelConfig;
    }

    /**
     * 生成mapper.xml文件配置
     *
     * @return {@link SqlMapGeneratorConfiguration}
     */
    private SqlMapGeneratorConfiguration buildMapperXmlConfig() {

        String projectFolder = generatorConfig.getModuleRootPath();
        String mappingXmlPackage = generatorConfig.getXmlPackage();
        String resourcePath = generatorConfig.getResourcePath();

        SqlMapGeneratorConfiguration mapperConfig = new SqlMapGeneratorConfiguration();

        if (!StringUtils.isEmpty(mappingXmlPackage)) {
            mapperConfig.setTargetPackage(mappingXmlPackage);
        } else {
            mapperConfig.setTargetPackage("");
        }

        mapperConfig.setTargetProject(projectFolder + "/" + resourcePath + "/");

        if (generatorConfig.isOverride()) {
            String mappingXmlFilePath = getMappingXmlFilePath(generatorConfig);
            File mappingXmlFile = new File(mappingXmlFilePath);
            if (mappingXmlFile.exists()) {
                mappingXmlFile.delete();
            }
        }

        return mapperConfig;
    }

    /**
     * 获取xml文件路径 用以删除之前的xml
     *
     * @param tableConfig {@link TableConfig}
     * @return mapper path
     */
    private String getMappingXmlFilePath(TableConfig tableConfig) {
        StringBuilder sb = new StringBuilder();
        String mappingXmlPackage = tableConfig.getXmlPackage();
        String xmlMvnPath = tableConfig.getResourcePath();
        sb.append(tableConfig.getModuleRootPath() + "/" + xmlMvnPath + "/");

        if (!StringUtils.isEmpty(mappingXmlPackage)) {
            sb.append(mappingXmlPackage.replace(".", "/")).append("/");
        }
        if (!StringUtils.isEmpty(tableConfig.getMapperName())) {
            sb.append(tableConfig.getMapperName()).append(".xml");
        } else {
            sb.append(tableConfig.getEntityName()).append("Mapper.xml");
        }

        return sb.toString();
    }

    /**
     * 生成table配置
     *
     * @param context {@link Context}
     * @return TableConfiguration
     */
    private TableConfiguration buildTableConfig(Context context) {
        TableConfiguration tableConfig = new TableConfiguration(context);
        tableConfig.setTableName(this.generatorConfig.getTableName());
        tableConfig.setDomainObjectName(this.generatorConfig.getEntityName());
        if(null != generatorConfig.getTablePrefix() && !"".equals(generatorConfig.getTablePrefix())){
            String camelCaseString = JavaBeansUtil.getCamelCaseString(generatorConfig.getTablePrefix(), true);
            DomainObjectRenamingRule domainObjectRenamingRule = new DomainObjectRenamingRule();
            domainObjectRenamingRule.setSearchString("^" + camelCaseString);
            domainObjectRenamingRule.setReplaceString("");
            tableConfig.setDomainObjectRenamingRule(domainObjectRenamingRule);
        }
        String schema;
        if (databaseType.equals(DbType.MySQL.name())) {
            String[] nameSplit = databaseUrl.split("/");
            schema = nameSplit[nameSplit.length - 1];
            tableConfig.setSchema(schema);
        } else if (databaseType.equals(DbType.Oracle.name())) {
            String[] nameSplit = databaseUrl.split(":");
            schema = nameSplit[nameSplit.length - 1];
            tableConfig.setCatalog(schema);
        } else {
            String[] nameSplit = databaseUrl.split("/");
            schema = nameSplit[nameSplit.length - 1];
            tableConfig.setCatalog(schema);
        }

        if (!this.generatorConfig.isUseExample()) {
            tableConfig.setUpdateByExampleStatementEnabled(false);
            tableConfig.setCountByExampleStatementEnabled(false);
            tableConfig.setDeleteByExampleStatementEnabled(false);
            tableConfig.setSelectByExampleStatementEnabled(false);
        }

        if (this.generatorConfig.isUseSchemaPrefix()) {
            if (DbType.MySQL.name().equals(databaseType)) {
                tableConfig.setSchema(schema);
            } else if (DbType.Oracle.name().equals(databaseType)) {
                //Oracle的schema为用户名，如果连接用户拥有dba等高级权限，若不设schema，会导致把其他用户下同名的表也生成一遍导致mapper中代码重复
                tableConfig.setSchema(username);
            } else {
                tableConfig.setCatalog(schema);
            }
        }

        if ("org.postgresql.Driver".equals(driverClass)) {
            tableConfig.setDelimitIdentifiers(true);
        }


        if (this.generatorConfig.isUseActualColumnNames()) {
            tableConfig.addProperty("useActualColumnNames", "true");
        }

        if (this.generatorConfig.isUseTableNameAlias()) {
            tableConfig.setAlias(this.generatorConfig.getTableName());
        }
        tableConfig.setMapperName(this.generatorConfig.getMapperName());


        return tableConfig;
    }

    /**
     * 生成dao接口文件配置
     *
     * @return {@link JavaClientGeneratorConfiguration}
     */
    private JavaClientGeneratorConfiguration buildMapperConfig() {

        String projectFolder = generatorConfig.getModuleRootPath();
        String mapperPackage = generatorConfig.getMapperPackage();
        String mapperPath = generatorConfig.getSourcePath();

        JavaClientGeneratorConfiguration mapperConfig = new JavaClientGeneratorConfiguration();
        mapperConfig.setConfigurationType("XMLMAPPER");
        mapperConfig.setTargetPackage("");

        mapperConfig.addProperty("enableSubPackages", "false");
        if (!StringUtils.isEmpty(mapperPackage)) {
            mapperConfig.setTargetPackage(mapperPackage);
        }

        mapperConfig.setTargetProject(projectFolder + "/" + mapperPath + "/");

        return mapperConfig;
    }

    /**
     * 生成注释配置
     *
     * @return {@link CommentGeneratorConfiguration}
     */
    private CommentGeneratorConfiguration buildCommentConfig() {
        CommentGeneratorConfiguration commentConfig = new CommentGeneratorConfiguration();
        commentConfig.addProperty("suppressAllComments", "false");
        commentConfig.addProperty("addRemarkComments", "true");
        commentConfig.addProperty("suppressDate", "true");

        if (generatorConfig.isComment()) {
            commentConfig.addProperty("columnRemarks", "true");
        }
        if (generatorConfig.isAnnotation()) {
            commentConfig.addProperty("annotations", "true");
        }

        return commentConfig;
    }
}
