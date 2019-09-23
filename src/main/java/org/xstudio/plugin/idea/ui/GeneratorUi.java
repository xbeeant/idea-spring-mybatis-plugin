package org.xstudio.plugin.idea.ui;

import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DbDataSource;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.psi.PsiElement;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.model.TableConfig;
import org.xstudio.plugin.idea.model.TableInfo;
import org.xstudio.plugin.idea.mybatis.MyBatisGenerateCommand;
import org.xstudio.plugin.idea.setting.MybatisSpringGeneratorConfiguration;
import org.xstudio.plugin.idea.util.JavaUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author xiaobiao
 * @version 2019/9/20
 */
public class GeneratorUi extends DialogWrapper {
    /**
     * idea 事件对象
     */
    private AnActionEvent event;

    private TableInfo tableInfo;
    /**
     * IDEA当前工程对象
     */
    private Project project;

    private RawConnectionConfig connectionConfig;
    /**
     * 当前模块
     */
    private TextFieldWithBrowseButton moduleRootField = new TextFieldWithBrowseButton();
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
    private JTextField mapperNameField = new JBTextField(20);
    private JTextField domainNameField = new JBTextField(20);
    /**
     * Tab页面
     */
    private JBTabbedPane tabPanel = new JBTabbedPane();


    private JTextField idGeneratorField =  new JBTextField(20);
    private JTextField serviceInterfaceField =  new JBTextField(20);
    private JTextField serviceImplField =  new JBTextField(20);
    private JTextField facadeInterfaceField =  new JBTextField(20);
    private JTextField facadeImplField =  new JBTextField(20);
    private JTextField daoInterfaceField =  new JBTextField(20);
    private JTextField rootObjectField =  new JBTextField(20);
    private JTextField ignoreColumnsField =  new JBTextField(20);
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
    private JCheckBox generateFacade = new JCheckBox("Generate Facade");
    private JCheckBox markDelete = new JCheckBox("Mark Delete");
    private JCheckBox rootObject = new JCheckBox("Root Entity Object");
    private JCheckBox fastJson = new JCheckBox("Fast Json");

    private MybatisSpringGeneratorConfiguration mybatisSpringGeneratorConfiguration;

    private TableConfig tableConfig;

    private boolean[] boxChanged;
    /**
     * 主面板
     */
    private JPanel contentPane = new JBPanel<>();

    public GeneratorUi(AnActionEvent e, TableInfo tableInfo) {
        super(e.getData(PlatformDataKeys.PROJECT));

        this.project = e.getData(PlatformDataKeys.PROJECT);
        mybatisSpringGeneratorConfiguration = MybatisSpringGeneratorConfiguration.getInstance(this.project);

        this.event = e;
        this.tableInfo = tableInfo;

        boxChanged = new boolean[12];
        for (int i = 0; i < 12; i++) {
            boxChanged[i] = false;
        }

        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
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
        String tableName = this.tableInfo.getTableName();
        tableConfig = mybatisSpringGeneratorConfiguration.getTableConfig(tableName);

        // 初始化数据库的信息界面
        this.initTablePanel(tableConfig);

        DbDataSource dbDataSource = null;
        // 遍历父节点，判断是否已经到达顶层节点（数据库配置）
        while (current != null) {
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

        // 初始化代码生成的配置界面
        this.initTabPanel(tableConfig);

        this.init();

        mybatisSpringGeneratorConfiguration.addTableConfig(tableConfig.getTableName(), tableConfig);

        connectionConfig = dbDataSource.getConnectionConfig();
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
        generalPanel.add(new TitledSeparator("Domain"));

        JPanel domainNamePanel = new JPanel();
        domainNamePanel.setLayout(new BoxLayout(domainNamePanel, BoxLayout.X_AXIS));
        JLabel entityNameLabel = new JLabel("Domain Name:");
        entityNameLabel.setPreferredSize(new Dimension(150, 10));
        domainNamePanel.add(entityNameLabel);
        domainNamePanel.add(domainNameField);

        // MapperName
        JPanel mapperNamePanel = new JPanel();
        mapperNamePanel.setLayout(new BoxLayout(mapperNamePanel, BoxLayout.X_AXIS));
        JLabel mapperNameLabel = new JLabel("Mapper Name:");
        mapperNameLabel.setPreferredSize(new Dimension(150, 10));
        mapperNameLabel.setLabelFor(domainNameField);
        mapperNamePanel.add(mapperNameLabel);
        mapperNamePanel.add(mapperNameField);

        JPanel domainPanel = new JPanel();
        domainPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        domainPanel.add(domainNamePanel);
        domainPanel.add(mapperNamePanel);

        generalPanel.add(domainPanel);

        // 包
        generalPanel.add(new TitledSeparator("Package"));
        JPanel packagePanel = new JPanel();
        packagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        packagePanel.add(JavaUtil.panelField("Model Entity:", idGeneratorField, tableConfig.getModelClass()));
        packagePanel.add(JavaUtil.panelField("Service Interface:", serviceInterfaceField, tableConfig.getIServiceClass()));
        packagePanel.add(JavaUtil.panelField("Service Impl:", serviceImplField, tableConfig.getServiceImplClass()));
        packagePanel.add(JavaUtil.panelField("Facade Interface:", facadeInterfaceField, tableConfig.getIFacadeClass()));
        packagePanel.add(JavaUtil.panelField("Facade Impl:", facadeImplField, tableConfig.getFacadeImplClass()));
        packagePanel.add(JavaUtil.panelField("Mapper Interface:", daoInterfaceField, tableConfig.getMapperClass()));
        packagePanel.add(JavaUtil.panelField("Mapper Impl:", daoInterfaceField, tableConfig.getMapperImpl()));

        generalPanel.add(packagePanel);
        generalPanel.setName("General");

        tabPanel.add(generalPanel);
        contentPane.add(tabPanel);
    }

    @Override
    protected void doOKAction() {
        if (overrideBox.getSelectedObjects() != null) {
            int confirm = Messages.showOkCancelDialog(project, "The exists file will be overwrite ,Confirm generate?", Constant.TITLE, Messages.getQuestionIcon());
            if (confirm == 2) {
                return;
            }
        } else {
            int confirm = Messages.showOkCancelDialog(project, "Confirm generate mybatis spring code?", Constant.TITLE, Messages.getQuestionIcon());
            if (confirm == 2) {
                return;
            }
        }

        super.doOKAction();

        new MyBatisGenerateCommand(tableConfig).execute(project, connectionConfig);
    }

    private void initOptionsPanel() {
        JBPanel optionsPanel = new JBPanel(new GridLayout(6, 2, 10, 10));
        commentBox.setSelected(tableConfig.isComment());
        overrideBox.setSelected(tableConfig.isOverride());
        needToStringHashcodeEqualsBox.setSelected(tableConfig.isToStringHashcodeEquals());
        useSchemaPrefixBox.setSelected(tableConfig.isUseSchemaPrefix());
        useTableNameAliasBox.setSelected(tableConfig.isUseTableNameAlias());
        mysql8Box.setSelected(tableConfig.isMysql8());
        lombokAnnotationBox.setSelected(tableConfig.isLombokPlugin());
        swaggerAnnotationBox.setSelected(tableConfig.isSwagger2Plugin());
        generateFacade.setSelected(tableConfig.isFacadePlugin());
        markDelete.setSelected(tableConfig.isMarkDeletePlugin());
        rootObject.setSelected(tableConfig.isRootObjectPlugin());
        fastJson.setSelected(tableConfig.isFastjsonPlugin());

        JavaUtil.boxListener(commentBox, Constant.CommentBox, "comment", boxChanged, tableConfig);
        JavaUtil.boxListener(overrideBox, Constant.Overwrite, "override", boxChanged, tableConfig);
        JavaUtil.boxListener(needToStringHashcodeEqualsBox, Constant.ToStringHashEquals, "toStringHashcodeEquals", boxChanged, tableConfig);
        JavaUtil.boxListener(useSchemaPrefixBox, Constant.SchemaPrefix, "useSchemaPrefix", boxChanged, tableConfig);
        JavaUtil.boxListener(useTableNameAliasBox, Constant.UseAlias, "useTableNameAlias", boxChanged, tableConfig);
        JavaUtil.boxListener(mysql8Box, Constant.MySql8, "mysql8", boxChanged, tableConfig);
        JavaUtil.boxListener(lombokAnnotationBox, Constant.Lombok, "lombokPlugin", boxChanged, tableConfig);
        JavaUtil.boxListener(swaggerAnnotationBox, Constant.Swagger, "swagger2Plugin", boxChanged, tableConfig);
        JavaUtil.boxListener(markDelete, Constant.MarkDelete, "markDeletePlugin", boxChanged, tableConfig);
        JavaUtil.boxListener(rootObject, Constant.RootEntity, "rootObjectPlugin", boxChanged, tableConfig);
        JavaUtil.boxListener(fastJson, Constant.FastJson, "fastjsonPlugin", boxChanged, tableConfig);


        optionsPanel.add(commentBox);
        optionsPanel.add(overrideBox);
        optionsPanel.add(needToStringHashcodeEqualsBox);
        optionsPanel.add(useSchemaPrefixBox);
        optionsPanel.add(useTableNameAliasBox);
        optionsPanel.add(mysql8Box);
        optionsPanel.add(lombokAnnotationBox);
        optionsPanel.add(swaggerAnnotationBox);
        optionsPanel.add(generateFacade);
        optionsPanel.add(markDelete);
        optionsPanel.add(rootObject);
        optionsPanel.add(fastJson);

        optionsPanel.add(JavaUtil.panelField("Id Generator:", idGeneratorField, tableConfig.getIdGenerator()));
        optionsPanel.add(JavaUtil.panelField("Service Interface:", serviceInterfaceField, tableConfig.getIService()));
        optionsPanel.add(JavaUtil.panelField("Service Impl:", serviceImplField, tableConfig.getServiceImpl()));
        optionsPanel.add(JavaUtil.panelField("Facade Interface:", facadeInterfaceField, tableConfig.getIFacade()));
        optionsPanel.add(JavaUtil.panelField("Facade Impl:", facadeImplField, tableConfig.getFacadeImpl()));
        optionsPanel.add(JavaUtil.panelField("Dao Interface:", daoInterfaceField, tableConfig.getIDao()));
        optionsPanel.add(JavaUtil.panelField("Root Object:", rootObjectField, tableConfig.getBaseObject()));
        optionsPanel.add(JavaUtil.panelField("Ignore Columns:", ignoreColumnsField, tableConfig.getIgnoreColumn()));
        optionsPanel.add(JavaUtil.panelField("Non Fuzzy Search Columns:", nonFuzzySearchColumnsField, tableConfig.getNonFuzzyColumn()));


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
        moduleRootField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                moduleRootField.setText(moduleRootField.getText().replaceAll("\\\\", "/"));
            }
        });

        moduleRootField.setText(project.getBasePath());
        moduleRootPanel.add(projectRootLabel);
        moduleRootPanel.add(moduleRootField);

        // Table
        JPanel tableNamePanel = new JPanel();
        tableNamePanel.setLayout(new BoxLayout(tableNamePanel, BoxLayout.X_AXIS));
        JLabel tableLabel = new JLabel("Table Name:");
        tableLabel.setLabelFor(tableNameField);
        tableLabel.setPreferredSize(new Dimension(150, 10));
        tableNamePanel.add(tableLabel);
        tableNamePanel.add(tableNameField);

        String tableName = this.tableInfo.getTableName();
        tableNameField.setText(tableName);
        tableNameField.setEditable(false);
        tableConfig.setTableName(tableName);

        String entityName = JavaBeansUtil.getCamelCaseString(tableName.replace(tableConfig.getTablePrefix(), ""), true);
        domainNameField.setText(entityName);
        mapperNameField.setText(entityName + "Mapper");

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
                domainNameField.setText(entityName);
                mapperNameField.setText(entityName + "Mapper");
                tableConfig.setEntityName(entityName);
                tableConfig.setMapperName(entityName + "Mapper");
                tableConfig.setTablePrefix(prefix);
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
