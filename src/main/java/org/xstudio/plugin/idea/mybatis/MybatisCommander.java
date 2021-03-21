package org.xstudio.plugin.idea.mybatis;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.psi.DbDataSource;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.notification.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import io.github.xbeeant.mybatis.MybatisGenerator;
import io.github.xbeeant.mybatis.po.*;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.DomainObjectRenamingRule;
import org.mybatis.generator.config.ModelType;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.model.Credential;
import org.xstudio.plugin.idea.mybatis.generator.MergeableShellCallback;
import org.xstudio.plugin.idea.mybatis.generator.ProjectPersistentProperties;
import org.xstudio.plugin.idea.setting.ProjectPersistentConfiguration;
import org.xstudio.plugin.idea.util.ModuleUtil;

import javax.swing.event.HyperlinkEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author huangxiaobiao
 */
public class MybatisCommander {
    private static final Logger log = Logger.getInstance(MybatisCommander.class);

    public void generate(Project project, ProjectPersistentProperties projectProperties, DbDataSource dbDataSource, Module module) {
        MybatisGenerator mybatisGenerator = new MybatisGenerator();
        Properties properties = new Properties();
        ProjectPersistentConfiguration projectPersistent = ProjectPersistentConfiguration.getInstance(project);
        Map<String, Credential> credentials = projectPersistent.getCredentials();
        String username = credentials.get(projectPersistent.getDatabaseUrl()).getUsername();

        String database = projectProperties.getDatabase();
        String databasePkg = database.replace("_", ".").replace("_", ".");
        CredentialAttributes credentialAttributes = new CredentialAttributes(
                Constant.PLUGIN_NAME + "-" + projectPersistent.getDatabaseUrl(),
                username, this.getClass(), false);
        String password = PasswordSafe.getInstance().getPassword(credentialAttributes);

        ConnectionProperty connectionProperty = new ConnectionProperty();
        connectionProperty.setDriverClass(((LocalDataSource) (dbDataSource.getDelegate())).getDriverClass());
        connectionProperty.setPassword(password);
        connectionProperty.setUrl(projectPersistent.getDatabaseUrl());
        connectionProperty.setUser(username);
        properties.setConnectionProperty(connectionProperty);

        JavaClientProperty javaClientProperty = new JavaClientProperty();
        javaClientProperty.setTargetProject(projectProperties.getModulePath() + projectProperties.getSrcPath());
        javaClientProperty.setTargetPackage(projectProperties.getRootPackage() + "." + databasePkg + ".mapper");
        javaClientProperty.setRootInterface(projectProperties.getMapperInterface());

        properties.setJavaClientProperty(javaClientProperty);

        JavaModelProperty javaModelProperty = new JavaModelProperty();
        javaModelProperty.setTargetProject(projectProperties.getModulePath() + projectProperties.getSrcPath());
        javaModelProperty.setTargetPackage(projectProperties.getRootPackage() + "." + databasePkg + ".model");
        javaModelProperty.setRootClass(projectProperties.getRootClass());
        javaModelProperty.setTrimStrings(true);
        properties.setJavaModelProperty(javaModelProperty);

        SqlMapProperty sqlMapProperty = new SqlMapProperty();
        sqlMapProperty.setTargetProject(projectProperties.getModulePath() + projectProperties.getResourcePath());
        sqlMapProperty.setTargetPackage("mybatis." + projectProperties.getType() + "." + database);
        properties.setSqlMapProperty(sqlMapProperty);

        TableProperty tableProperty = new TableProperty();
        tableProperty.setTableName(projectProperties.getSchema());
        tableProperty.setSchema(projectProperties.getDatabase());
        tableProperty.setModelType(ModelType.FLAT);

        DomainObjectRenamingRule domainObjectRenamingRule = new DomainObjectRenamingRule();
        domainObjectRenamingRule.setReplaceString(projectProperties.getReplaceString());
        domainObjectRenamingRule.setSearchString(projectProperties.getSearchString());
        if (null != projectProperties.getSearchString() && !"".equals(projectProperties.getSearchString())) {
            tableProperty.setRenamingRule(domainObjectRenamingRule);
        }

        properties.setTableProperty(tableProperty);

        XstudioProperty xstudioProperty = new XstudioProperty();
        xstudioProperty.setServiceRootInterface(projectProperties.getServiceInterface());
        xstudioProperty.setServiceTargetPackage(projectProperties.getRootPackage() + "." + databasePkg + ".service");
        xstudioProperty.setServiceImplementRootInterface(projectProperties.getServiceImpl());
        xstudioProperty.setServiceImplementTargetPackage(projectProperties.getRootPackage() + "." + databasePkg + ".service.impl");
        xstudioProperty.setRootClient(projectProperties.getMapperInterface());
        xstudioProperty.setIdGenerator(projectProperties.getIdGenerator());
        xstudioProperty.setResponseObject(projectProperties.getResponseObject());
        xstudioProperty.setDateTime(projectProperties.getPlugin().isChkDateTime() ? "true" : "false");
        xstudioProperty.setBeginEnd(projectProperties.getPlugin().isChkBeginEnd() ? "true" : "false");
        properties.setXstudioProperty(xstudioProperty);

        PluginProperty pluginProperty = new PluginProperty();

        pluginProperty.setEnableMapperAnnotationPlugin(true);
        pluginProperty.setEnableSerializablePlugin(true);
        pluginProperty.setEnableUnmergeableXmlMappersPlugin(true);
        pluginProperty.setEnableJavaClientRootInterfaceKeyTypeArgumentPlugin(true);
        pluginProperty.setEnableJavaModelRootClassKeyTypeArgumentPlugin(true);
        pluginProperty.setEnableJavaModelNoExamplePlugin(true);
        pluginProperty.setEnableXstudioMapperPlugin(true);
        pluginProperty.setEnableXstudioModelPlugin(true);
        pluginProperty.setEnableXstudioServicePlugin(true);

        properties.setPluginProperty(pluginProperty);

        properties.setCallback(new MergeableShellCallback(projectProperties.getPlugin().isChekOverwrite()));
        try {
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
                        MyBatisGenerator myBatisGenerator = mybatisGenerator.generate(properties);
                        // 刷新工程

                        project.getBaseDir().refresh(false, true);

                        NotificationGroup balloonNotifications = new NotificationGroup(Constant.TITLE, NotificationDisplayType.STICKY_BALLOON, true);

                        List<String> result = new ArrayList<>();
                        List<GeneratedJavaFile> generatedJavaFiles = myBatisGenerator.getGeneratedJavaFiles();
                        for (GeneratedJavaFile generatedJavaFile : generatedJavaFiles) {
                            String link = String.format("<a href=\"%s%s%s%s%s\">%s</a>",
                                    projectProperties.getSrcPath(),
                                    Matcher.quoteReplacement(File.separator),
                                    generatedJavaFile.getTargetPackage().replace(".", Matcher.quoteReplacement(File.separator)),
                                    Matcher.quoteReplacement(File.separator),
                                    generatedJavaFile.getFileName(),
                                    generatedJavaFile.getFileName());
                            result.add(link);
                        }

                        for (GeneratedXmlFile generatedXmlFile : myBatisGenerator.getGeneratedXmlFiles()) {
                            String link = String.format("<a href=\"%s%s%s%s%s\">%s</a>",
                                    projectProperties.getResourcePath(),
                                    Matcher.quoteReplacement(File.separator),
                                    generatedXmlFile.getTargetPackage().replace(".", Matcher.quoteReplacement(File.separator)),
                                    Matcher.quoteReplacement(File.separator),
                                    generatedXmlFile.getFileName(),
                                    generatedXmlFile.getFileName());
                            result.add(link);
                        }

                        Notification notification = balloonNotifications.createNotification("Generate Successfully", "<html>" + String.join("<br/>", result) + "</html>", NotificationType.INFORMATION, (notification1, hyperlinkEvent) -> {
                            if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                new OpenFileDescriptor(project, ModuleUtil.getModule(module, "root").findFileByRelativePath(hyperlinkEvent.getDescription())).navigate(true);
                            }
                        });
                        Notifications.Bus.notify(notification);
                    } catch (Exception e) {
                        log.error(e);
                        balloon.hide();
                    }
                }
            };
            generateTask.setCancelText("Stop Generate Code").queue();
            generateTask.setCancelTooltipText("Stop generate mybatis spring code");
        } catch (Exception e) {
            Messages.showMessageDialog(e.getMessage(), "Generate Failure", Messages.getInformationIcon());
        }
    }
}
