package org.xstudio.plugin.idea.ui;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbNamespaceImpl;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.model.Credential;
import org.xstudio.plugin.idea.model.TableConfig;
import org.xstudio.plugin.idea.model.TableInfo;
import org.xstudio.plugin.idea.mybatis.MyBatisGenerateCommand;
import org.xstudio.plugin.idea.setting.ProjectPersistentConfiguration;
import org.xstudio.plugin.idea.util.DatabaseUtils;
import org.xstudio.plugin.idea.util.JavaUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author xiaobiao
 * @version 2019/9/20
 */
public class ProjectCodeGeneratorUi extends DialogWrapper {
    /**
     * idea 事件对象
     */
    private AnActionEvent event;

    private TableInfo tableInfo;
    /**
     * IDEA当前工程对象
     */
    private Project project;
    /**
     * 项目模块管理
     */
    private ModuleManager moduleManager;

    private RawConnectionConfig connectionConfig;

    private DbDataSource dbDataSource;
    /**
     * 当前模块
     */
    private ModulesComboBox moduleRootField;


    /**
     * 表
     */
    private JTextField tableNameField = new JBTextField(20);
    /**
     * 前缀
     */
    private JTextField tablePrefixField = new JBTextField(20);

    /**
     * 对象名称
     */
    private JTextField tableMapperField = new JBTextField(20);
    private JTextField tableEntityField = new JBTextField(20);
    private JTextField tableServiceInterfaceField = new JBTextField(20);
    private JTextField tableServiceImplField = new JBTextField(20);
    private JTextField tableFacadeInterfaceField = new JBTextField(20);
    private JTextField tableFacadeImplField = new JBTextField(20);
    private JTextField tableControllerField = new JBTextField(20);
    private JTextField tableMapperFileField = new JBTextField(20);
    /**
     * Tab页面
     */
    private JBTabbedPane tabPanel = new JBTabbedPane();


    private JTextField idGeneratorField = new JBTextField(20);
    private JTextField serviceInterfaceField = new JBTextField(20);
    private JTextField serviceImplField = new JBTextField(20);
    private JTextField facadeInterfaceField = new JBTextField(20);
    private JTextField facadeImplField = new JBTextField(20);
    private JTextField daoInterfaceField = new JBTextField(20);
    private JTextField rootObjectField = new JBTextField(20);
    private JTextField ignoreColumnsField = new JBTextField(20);
    private JTextField nonFuzzySearchColumnsField = new JBTextField(20);


    // ===============
    // mybatis spring code generator config
    // ===============

    private JCheckBox commentBox = new JCheckBox("Comment");
    private JCheckBox overrideBox = new JCheckBox("Overwrite");
    private JCheckBox needToStringHashcodeEqualsBox = new JCheckBox("toString/hashCode/equals");
    private JCheckBox useSchemaPrefixBox = new JCheckBox("Use Schema Prefix");
    private JCheckBox useTableNameAliasBox = new JCheckBox("Use-Alias");
    private JCheckBox mysql8Box = new JCheckBox("MySQL 8");
    private JCheckBox lombokAnnotationBox = new JCheckBox("Lombok");
    private JCheckBox swaggerAnnotationBox = new JCheckBox("Swagger Model");
    private JCheckBox generateFacadeBox = new JCheckBox("Generate Facade");
    private JCheckBox markDeleteBox = new JCheckBox("Mark Delete");
    private JCheckBox rootObjectBox = new JCheckBox("Root Entity Object");
    private JCheckBox fastJsonBox = new JCheckBox("Fast Json");

    private ProjectPersistentConfiguration projectPersistentConfiguration;

    private TableConfig tableConfig;

    private boolean[] boxChanged;
    /**
     * 主面板
     */
    private JPanel contentPane = new JBPanel<>();

    public ProjectCodeGeneratorUi(AnActionEvent event, TableInfo tableInfo) {
        super(event.getData(PlatformDataKeys.PROJECT));

        this.project = event.getData(PlatformDataKeys.PROJECT);
        this.moduleManager = ModuleManager.getInstance(this.project);

        this.projectPersistentConfiguration = ProjectPersistentConfiguration.getInstance(this.project);

        this.event = event;
        this.tableInfo = tableInfo;

        boxChanged = new boolean[12];
        for (int i = 0; i < 12; i++) {
            boxChanged[i] = false;
        }

        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        // =================================
        // 从所选择的元素中获取数据库的配置
        // =================================

        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("Please select one table or more then one columns", Constant.TITLE, Messages.getWarningIcon());
            return;
        }

        // =================================
        // UI
        // =================================
        setTitle(Constant.TITLE);
        // 设置大小
        pack();
        setModal(true);
        VerticalFlowLayout layoutManager = new VerticalFlowLayout(VerticalFlowLayout.TOP);
        layoutManager.setHgap(0);
        layoutManager.setVgap(0);
        contentPane.setLayout(layoutManager);
        contentPane.setBorder(JBUI.Borders.empty());

        PsiElement current = psiElements[0];

        dbDataSource = null;
        String databaseName = "";
        // 遍历父节点，判断是否已经到达顶层节点（数据库配置）
        while (current != null) {
            if (DbNamespaceImpl.class.isAssignableFrom(current.getClass())) {
                databaseName = ((DbNamespaceImpl) current).getName();
            }
            if (DbDataSource.class.isAssignableFrom(current.getClass())) {
                dbDataSource = (DbDataSource) current;
                break;
            }
            current = current.getParent();
        }

        if (dbDataSource == null) {
            Messages.showMessageDialog(project, "Cannot get datasource", "Mybatis Generator Plus", Messages.getErrorIcon());
            return;
        }

        String tableName = this.tableInfo.getTableName();
        tableConfig = projectPersistentConfiguration.getTableConfig(tableName, databaseName);
        tableConfig.setModuleRootPath(project.getBasePath());
        // 初始化数据库的信息界面
        this.initTablePanel(tableConfig);

        // 初始化代码生成的配置界面
        this.initTabPanel(tableConfig);

        this.init();

        projectPersistentConfiguration.addTableConfig(tableConfig.getTableName(), tableConfig);

        connectionConfig = dbDataSource.getConnectionConfig();
    }

    private boolean getDatabaseCredential(RawConnectionConfig connectionConfig) {
        DatabaseCredentialUI databaseCredentialUI = new DatabaseCredentialUI(event.getProject(), connectionConfig.getUrl());
        return databaseCredentialUI.showAndGet();
    }

    private void initTabPanel(TableConfig tableConfig) {

        // 代码生成路径、对象等基础配置

        // 对象
        this.initPackagePanel();

        // 生成器配置
        this.initOptionsPanel();
    }

    private void initPackagePanel() {
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        // ===========================
        // domain panel
        // ===========================
        JPanel domainPanel = new JPanel();
        domainPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        domainPanel.add(JavaUtil.panelField("Model Entity:", tableEntityField, null).getPanel());
        domainPanel.add(JavaUtil.panelField("Service Interface:", tableServiceInterfaceField, tableConfig.getServiceInterfaceClass()).getPanel());
        domainPanel.add(JavaUtil.panelField("Service Impl:", tableServiceImplField, tableConfig.getServiceImplClass()).getPanel());
        domainPanel.add(JavaUtil.panelField("Facade Interface:", tableFacadeInterfaceField, tableConfig.getFacadeInterfaceClass()).getPanel());
        domainPanel.add(JavaUtil.panelField("Facade Impl:", tableFacadeImplField, tableConfig.getFacadeImplClass()).getPanel());
        domainPanel.add(JavaUtil.panelField("Mapper Interface:", tableMapperField, tableConfig.getMapperClass()).getPanel());
        domainPanel.add(JavaUtil.panelField("Mapper Impl:", tableMapperFileField, tableConfig.getMapperImplClass()).getPanel());

        generalPanel.add(domainPanel);


        generalPanel.setName("General");

        tabPanel.add(generalPanel);
        contentPane.add(tabPanel);
    }

    @Override
    protected void doOKAction() {
        saveProjectConfig();

        connectionConfig = dbDataSource.getConnectionConfig();

        Map<String, Credential> credentials = projectPersistentConfiguration.getCredentials();
        Credential credential;
        if (credentials == null || !credentials.containsKey(connectionConfig.getUrl())) {
            boolean result = getDatabaseCredential(connectionConfig);
            if (result) {
                credentials = projectPersistentConfiguration.getCredentials();
                credential = credentials.get(connectionConfig.getUrl());
            } else {
                return;
            }
        } else {
            credential = credentials.get(connectionConfig.getUrl());
        }

        Callable<Exception> callable = new Callable<Exception>() {
            @Override
            public Exception call() {
                String url = projectPersistentConfiguration.getDatabaseUrl();
                CredentialAttributes credentialAttributes = new CredentialAttributes(Constant.PLUGIN_NAME + "-" + url, credential.getUsername(), this.getClass(), false);
                String password = PasswordSafe.getInstance().getPassword(credentialAttributes);
                try {
                    String databaseType = DatabaseUtils.testConnection(connectionConfig.getDriverClass(), connectionConfig.getUrl(), credential.getUsername(), password, mysql8Box.getSelectedObjects() != null);
                    tableConfig.setDatabaseType(databaseType);
                } catch (ClassNotFoundException | SQLException e) {
                    projectPersistentConfiguration.setCredentials(null);
                    return e;
                }
                return null;
            }
        };
        FutureTask<Exception> future = new FutureTask<>(callable);
        ProgressManager.getInstance().runProcessWithProgressSynchronously(future, "Connect to Database", true, project);
        Exception exception;
        try {
            exception = future.get();
        } catch (InterruptedException | ExecutionException e) {
            Messages.showMessageDialog(project, "Failed to connect to database \n " + e.getMessage(), Constant.TITLE, Messages.getErrorIcon());
            return;
        }
        if (exception != null) {
            Messages.showMessageDialog(project, "Failed to connect to database \n " + exception.getMessage(), Constant.TITLE, Messages.getErrorIcon());
            if (exception.getClass().equals(SQLException.class)) {
                SQLException sqlException = (SQLException) exception;
                if (sqlException.getErrorCode() == 1045) {
                    boolean result = getDatabaseCredential(connectionConfig);
                    if (result) {
                        this.doOKAction();
                        return;
                    }
                }
            }
            return;
        }

        if (overrideBox.getSelectedObjects() != null) {
            int confirm = Messages.showOkCancelDialog("The exists file will be overwrite ,Confirm generate?", Constant.TITLE, "Ok", "Cancel", Messages.getQuestionIcon());
            if (confirm == 2) {
                return;
            }
        } else {
            int confirm = Messages.showOkCancelDialog("Confirm generate mybatis spring code?", Constant.TITLE, "Ok", "Cancel", Messages.getQuestionIcon());
            if (confirm == 2) {
                return;
            }
        }

        super.doOKAction();

        new MyBatisGenerateCommand(tableConfig).execute(project, moduleRootField.getSelectedModule(), connectionConfig);
    }

    private void saveProjectConfig() {
        String path = ModuleRootManager.getInstance(moduleRootField.getSelectedModule()).getContentRoots()[0].getPath();
        tableConfig.setModuleRootPath(path);
        tableConfig.setTablePrefix(tablePrefixField.getText());
        tableConfig.setIdGenerator(idGeneratorField.getText());
        tableConfig.setIService(serviceInterfaceField.getText());
        tableConfig.setServiceImpl(serviceImplField.getText());
        tableConfig.setIFacade(facadeInterfaceField.getText());
        tableConfig.setFacadeImpl(facadeImplField.getText());
        tableConfig.setIDao(daoInterfaceField.getText());
        tableConfig.setBaseObject(rootObjectField.getText());
        tableConfig.setIgnoreColumn(ignoreColumnsField.getText());
        tableConfig.setNonFuzzyColumn(nonFuzzySearchColumnsField.getText());

        tableConfig.setComment(commentBox.getSelectedObjects() != null);
        tableConfig.setOverride(overrideBox.getSelectedObjects() != null);
        tableConfig.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
        tableConfig.setToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
        tableConfig.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
        tableConfig.setMysql8(mysql8Box.getSelectedObjects() != null);
        tableConfig.setLombokPlugin(lombokAnnotationBox.getSelectedObjects() != null);
        tableConfig.setSwagger2Plugin(swaggerAnnotationBox.getSelectedObjects() != null);
        tableConfig.setFacadePlugin(generateFacadeBox.getSelectedObjects() != null);
        tableConfig.setFastjsonPlugin(fastJsonBox.getSelectedObjects() != null);

        projectPersistentConfiguration.saveProjectConfig(tableConfig.getTableName(), tableConfig);
    }

    private void initOptionsPanel() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        JBPanel checkBoxPanel = new JBPanel(new GridLayout(6, 2, 10, 10));
        commentBox.setSelected(tableConfig.isComment());
        overrideBox.setSelected(tableConfig.isOverride());
        needToStringHashcodeEqualsBox.setSelected(tableConfig.isToStringHashcodeEquals());
        useSchemaPrefixBox.setSelected(tableConfig.isUseSchemaPrefix());
        useTableNameAliasBox.setSelected(tableConfig.isUseTableNameAlias());
        mysql8Box.setSelected(tableConfig.isMysql8());
        lombokAnnotationBox.setSelected(tableConfig.isLombokPlugin());
        swaggerAnnotationBox.setSelected(tableConfig.isSwagger2Plugin());
        generateFacadeBox.setSelected(tableConfig.isFacadePlugin());
        markDeleteBox.setSelected(tableConfig.isMarkDeletePlugin());
        rootObjectBox.setSelected(tableConfig.isRootObjectPlugin());
        fastJsonBox.setSelected(tableConfig.isFastjsonPlugin());

        JavaUtil.boxListener(commentBox, Constant.CommentBox, "comment", boxChanged, tableConfig);
        JavaUtil.boxListener(overrideBox, Constant.Overwrite, "override", boxChanged, tableConfig);
        JavaUtil.boxListener(needToStringHashcodeEqualsBox, Constant.ToStringHashEquals, "toStringHashcodeEquals", boxChanged, tableConfig);
        JavaUtil.boxListener(useSchemaPrefixBox, Constant.SchemaPrefix, "useSchemaPrefix", boxChanged, tableConfig);
        JavaUtil.boxListener(useTableNameAliasBox, Constant.UseAlias, "useTableNameAlias", boxChanged, tableConfig);
        JavaUtil.boxListener(mysql8Box, Constant.MySql8, "mysql8", boxChanged, tableConfig);
        JavaUtil.boxListener(lombokAnnotationBox, Constant.Lombok, "lombokPlugin", boxChanged, tableConfig);
        JavaUtil.boxListener(swaggerAnnotationBox, Constant.Swagger, "swagger2Plugin", boxChanged, tableConfig);
        JavaUtil.boxListener(generateFacadeBox, Constant.Facade, "facadePlugin", boxChanged, tableConfig);
        JavaUtil.boxListener(markDeleteBox, Constant.MarkDelete, "markDeletePlugin", boxChanged, tableConfig);
        JavaUtil.boxListener(rootObjectBox, Constant.RootEntity, "rootObjectPlugin", boxChanged, tableConfig);
        JavaUtil.boxListener(fastJsonBox, Constant.FastJson, "fastjsonPlugin", boxChanged, tableConfig);


        checkBoxPanel.add(commentBox);
        checkBoxPanel.add(overrideBox);
        checkBoxPanel.add(needToStringHashcodeEqualsBox);
        checkBoxPanel.add(useSchemaPrefixBox);
        checkBoxPanel.add(useTableNameAliasBox);
        checkBoxPanel.add(mysql8Box);
        checkBoxPanel.add(lombokAnnotationBox);
        checkBoxPanel.add(swaggerAnnotationBox);
        checkBoxPanel.add(generateFacadeBox);
        checkBoxPanel.add(markDeleteBox);
        checkBoxPanel.add(rootObjectBox);
        checkBoxPanel.add(fastJsonBox);
        optionsPanel.add(checkBoxPanel);

        JBPanel optionsInputPanel = new JBPanel();
        optionsInputPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        optionsInputPanel.add(JavaUtil.panelField("Id Generator:", idGeneratorField, tableConfig.getIdGenerator()).getPanel());
        optionsInputPanel.add(JavaUtil.panelField("Service Interface:", serviceInterfaceField, tableConfig.getIService()).getPanel());
        optionsInputPanel.add(JavaUtil.panelField("Service Impl:", serviceImplField, tableConfig.getServiceImpl()).getPanel());
        optionsInputPanel.add(JavaUtil.panelField("Facade Interface:", facadeInterfaceField, tableConfig.getIFacade()).getPanel());
        optionsInputPanel.add(JavaUtil.panelField("Facade Impl:", facadeImplField, tableConfig.getFacadeImpl()).getPanel());
        optionsInputPanel.add(JavaUtil.panelField("Dao Interface:", daoInterfaceField, tableConfig.getIDao()).getPanel());
        optionsInputPanel.add(JavaUtil.panelField("Root Object:", rootObjectField, tableConfig.getBaseObject()).getPanel());
        optionsInputPanel.add(JavaUtil.panelField("Ignore Columns:", ignoreColumnsField, tableConfig.getIgnoreColumn()).getPanel());
        optionsInputPanel.add(JavaUtil.panelField("Non Fuzzy Search Columns:", nonFuzzySearchColumnsField, tableConfig.getNonFuzzyColumn()).getPanel());

        optionsPanel.add(optionsInputPanel);

        optionsPanel.setName("Options");
        tabPanel.add(optionsPanel);

    }

    private TableConfig initTablePanel(TableConfig tableConfig) {

        JPanel headerPanel = new JBPanel<>();
        headerPanel.setBorder(JBUI.Borders.empty(0, 5));
        VerticalFlowLayout layout = new VerticalFlowLayout(VerticalFlowLayout.TOP);
        layout.setVgap(0);
        headerPanel.setLayout(layout);
        JPanel moduleRootPanel = new JPanel();
        moduleRootPanel.setLayout(new BoxLayout(moduleRootPanel, BoxLayout.X_AXIS));
        JBLabel projectRootLabel = new JBLabel("Module Root:");
        projectRootLabel.setPreferredSize(new Dimension(150, 10));

        moduleRootField = new ModulesComboBox();
        moduleRootField.fillModules(this.project);

        Module[] modules = moduleManager.getSortedModules();
        for (Module module : modules) {
            VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
            if (contentRoots.length == 0) {
                continue;
            }
            String url = contentRoots[0].getPath();
            if (url.equals(tableConfig.getModuleRootPath())) {
                moduleRootField.setSelectedModule(module);
                break;
            }
        }
        moduleRootPanel.add(projectRootLabel);
        moduleRootPanel.add(moduleRootField);


        // Table
        JPanel tableNamePanel = JavaUtil.panelField("Table Name:", tableNameField, null, new Dimension(150, 10)).getPanel();

        String tableName = this.tableInfo.getTableName();
        tableNameField.setText(tableName);
        tableNameField.setEditable(false);
        tableConfig.setTableName(tableName);

        String entityName = JavaBeansUtil.getCamelCaseString(tableName, true);
        if (null != tableConfig.getTablePrefix()) {
            entityName = JavaBeansUtil.getCamelCaseString(tableName.replace(tableConfig.getTablePrefix(), ""), true);
        }
        tableEntityField.setText(entityName);
        tableMapperField.setText(entityName + "Mapper");

        JPanel tablePrefixPanel = new JPanel();
        tablePrefixPanel.setLayout(new BoxLayout(tablePrefixPanel, BoxLayout.X_AXIS));
        JLabel tablePrefixLabel = new JLabel("   Table Prefix:");
        tablePrefixLabel.setLabelFor(tablePrefixField);
        tablePrefixLabel.setPreferredSize(new Dimension(150, 10));
        tableNamePanel.add(tablePrefixLabel);
        tableNamePanel.add(tablePrefixField);
        tablePrefixField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String prefix = tablePrefixField.getText();
                String entityName = JavaBeansUtil.getCamelCaseString(tableName.replace(prefix, ""), true);
                tableEntityField.setText(entityName);
                tableMapperField.setText(entityName + "Mapper");
                tableConfig.setEntityName(entityName);
                tableConfig.setTablePrefix(prefix);

                projectPersistentConfiguration.setTableGenerateTarget(tableConfig, entityName);

                tableServiceInterfaceField.setText(tableConfig.getServiceInterfaceClass());
                tableServiceImplField.setText(tableConfig.getServiceImplClass());
                tableFacadeInterfaceField.setText(tableConfig.getFacadeInterfaceClass());
                tableFacadeImplField.setText(tableConfig.getFacadeImplClass());
                tableEntityField.setText(tableConfig.getModelClass());
                tableMapperField.setText(tableConfig.getMapperClass());
                tableMapperFileField.setText(tableConfig.getMapperImplClass());
            }
        });
        tablePrefixField.setText(tableConfig.getTablePrefix());
        tablePrefixField.setEditable(true);
        headerPanel.add(moduleRootPanel);
        headerPanel.add(tableNamePanel);
        headerPanel.add(tablePrefixPanel);
        contentPane.add(headerPanel);

        return tableConfig;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
