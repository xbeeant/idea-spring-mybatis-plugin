package org.xstudio.plugin.idea.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import org.apache.commons.lang.StringUtils;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.model.ProjectConfig;
import org.xstudio.plugin.idea.setting.MybatisSpringGeneratorConfiguration;
import org.xstudio.plugin.idea.util.JavaUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author xiaobiao
 * @version 2019/9/23
 */
public class GeneratorSettingUi extends JDialog {
    public JPanel contentPanel = new JBPanel<>();
    private Project project;
    private MybatisSpringGeneratorConfiguration config;

    // =================
    // path
    // =================

    /**
     * source path
     */
    private JTextField sourcePathField = new JTextField();
    /**
     * resource path
     */
    private JTextField resourcePathField = new JTextField();

    // =================
    // project config
    // =================

    private TextFieldWithBrowseButton moduleRootField = new TextFieldWithBrowseButton();

    private JTextField basePackageField = new JTextField();
    private JTextField tablePrefixField = new JTextField();
    private JTextField idGeneratorField = new JTextField();
    private JTextField serviceInterfaceField = new JTextField();
    private JTextField serviceImplField = new JTextField();
    private JTextField facadeInterfaceField = new JTextField();
    private JTextField facadeImplField = new JTextField();
    private JTextField daoInterfaceField = new JTextField();
    private JTextField rootObjectField = new JTextField();
    private JTextField ignoreColumnsField = new JTextField();
    private JTextField nonFuzzyColumnField = new JTextField();

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

    private boolean[] boxChanged;

    public GeneratorSettingUi(Project project) {
        this.project = project;
        setContentPane(contentPanel);
        boxChanged = new boolean[12];
        for (int i = 0; i < 12; i++) {
            boxChanged[i] = false;
        }
        this.initUi();
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    private void initUi() {
        contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        config = MybatisSpringGeneratorConfiguration.getInstance(project);

        initPathPanel();
        initProjectPanel();
        initOptionsPanel();
    }

    private void initPathPanel() {
        ProjectConfig globalConfig = config.getProjectConfig();

        JPanel sourcePathPanel = JavaUtil.panelField("Source Path:", sourcePathField, globalConfig.getSourcePath());

        JPanel resourcePathPanel = JavaUtil.panelField("Resource Path:", resourcePathField, globalConfig.getResourcePath());

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        TitledSeparator separator = new TitledSeparator();
        separator.setText("Path");
        pathPanel.add(sourcePathPanel);
        pathPanel.add(resourcePathPanel);
        contentPanel.add(separator);
        contentPanel.add(pathPanel);
    }

    private void initProjectPanel() {
        ProjectConfig globalConfig = config.getProjectConfig();
        JPanel projectPanel = new JPanel();
        projectPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));


        // ========= module root
        JPanel projectRootPanel = new JPanel();
        projectRootPanel.setLayout(new BoxLayout(projectRootPanel, BoxLayout.X_AXIS));
        JBLabel projectRootLabel = new JBLabel("Module Root:");
        projectRootLabel.setPreferredSize(new Dimension(200, 20));
        moduleRootField.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                moduleRootField.setText(moduleRootField.getText().replaceAll("\\\\", "/"));
            }
        });
        if (globalConfig != null && !StringUtils.isEmpty(globalConfig.getModuleRootPath())) {
            moduleRootField.setText(globalConfig.getModuleRootPath());
        } else {
            moduleRootField.setText(project.getBasePath());
        }
        projectRootPanel.add(projectRootLabel);
        projectRootPanel.add(moduleRootField);
        projectPanel.add(projectRootLabel);

        projectPanel.add(JavaUtil.panelField("Table Prefix:", tablePrefixField, globalConfig.getTablePrefix()));
        projectPanel.add(JavaUtil.panelField("Code Package:", basePackageField, globalConfig.getBasePackage()));
        projectPanel.add(JavaUtil.panelField("Id Generator:", idGeneratorField, globalConfig.getIdGenerator()));
        projectPanel.add(JavaUtil.panelField("Service Interface:", serviceInterfaceField, globalConfig.getIService()));
        projectPanel.add(JavaUtil.panelField("Service Impl:", serviceImplField, globalConfig.getServiceImpl()));
        projectPanel.add(JavaUtil.panelField("Facade Interface:", facadeInterfaceField, globalConfig.getIFacade()));
        projectPanel.add(JavaUtil.panelField("Facade Impl:", facadeImplField, globalConfig.getFacadeImpl()));
        projectPanel.add(JavaUtil.panelField("Dao Interface:", daoInterfaceField, globalConfig.getIDao()));
        projectPanel.add(JavaUtil.panelField("Root Object:", rootObjectField, globalConfig.getBaseObject()));
        projectPanel.add(JavaUtil.panelField("Ignore Columns:", ignoreColumnsField, globalConfig.getIgnoreColumn()));
        projectPanel.add(JavaUtil.panelField("Non Fuzzy Search Columns:", nonFuzzyColumnField, globalConfig.getNonFuzzyColumn()));


        TitledSeparator separator = new TitledSeparator();
        separator.setText("Project Configuration");
        contentPanel.add(separator);
        contentPanel.add(projectPanel);
    }

    private void initOptionsPanel() {
        JBPanel optionsPanel = new JBPanel(new GridLayout(6, 2, 10, 10));

        commentBox.setSelected(this.config.getProjectConfig().isComment());
        overrideBox.setSelected(this.config.getProjectConfig().isOverride());
        needToStringHashcodeEqualsBox.setSelected(this.config.getProjectConfig().isToStringHashcodeEquals());
        useSchemaPrefixBox.setSelected(this.config.getProjectConfig().isUseSchemaPrefix());
        useTableNameAliasBox.setSelected(this.config.getProjectConfig().isUseTableNameAlias());
        mysql8Box.setSelected(this.config.getProjectConfig().isMysql8());
        lombokAnnotationBox.setSelected(this.config.getProjectConfig().isLombokPlugin());
        swaggerAnnotationBox.setSelected(this.config.getProjectConfig().isSwagger2Plugin());
        generateFacade.setSelected(this.config.getProjectConfig().isFacadePlugin());
        markDelete.setSelected(this.config.getProjectConfig().isMarkDeletePlugin());
        rootObject.setSelected(this.config.getProjectConfig().isRootObjectPlugin());
        fastJson.setSelected(this.config.getProjectConfig().isFastjsonPlugin());

        JavaUtil.boxListener(commentBox, Constant.CommentBox, "comment", boxChanged, this.config.getProjectConfig());
        JavaUtil.boxListener(overrideBox, Constant.Overwrite, "override", boxChanged, this.config.getProjectConfig());
        JavaUtil.boxListener(needToStringHashcodeEqualsBox, Constant.ToStringHashEquals, "toStringHashcodeEquals", boxChanged, this.config.getProjectConfig());
        JavaUtil.boxListener(useSchemaPrefixBox, Constant.SchemaPrefix, "useSchemaPrefix", boxChanged, this.config.getProjectConfig());
        JavaUtil.boxListener(useTableNameAliasBox, Constant.UseAlias, "useTableNameAlias", boxChanged, this.config.getProjectConfig());
        JavaUtil.boxListener(mysql8Box, Constant.MySql8, "mysql8", boxChanged, this.config.getProjectConfig());
        JavaUtil.boxListener(lombokAnnotationBox, Constant.Lombok, "lombokPlugin", boxChanged, this.config.getProjectConfig());
        JavaUtil.boxListener(swaggerAnnotationBox, Constant.Swagger, "swagger2Plugin", boxChanged, this.config.getProjectConfig());
        JavaUtil.boxListener(markDelete, Constant.MarkDelete, "markDeletePlugin", boxChanged, this.config.getProjectConfig());
        JavaUtil.boxListener(rootObject, Constant.RootEntity, "rootObjectPlugin", boxChanged, this.config.getProjectConfig());
        JavaUtil.boxListener(fastJson, Constant.FastJson, "fastjsonPlugin", boxChanged, this.config.getProjectConfig());

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

        TitledSeparator separator = new TitledSeparator();
        separator.setText("Options");
        contentPanel.add(separator);
        contentPanel.add(optionsPanel);
    }



    /**
     * 界面是否有变化
     *
     * @return boolean
     */
    public boolean isModified() {
        boolean modified = !this.moduleRootField.getText().equals(this.config.getProjectConfig().getModuleRootPath());
        modified |= !this.sourcePathField.getText().equals(this.config.getProjectConfig().getSourcePath());
        modified |= !this.resourcePathField.getText().equals(this.config.getProjectConfig().getResourcePath());

        modified |= !this.tablePrefixField.getText().equals(this.config.getProjectConfig().getTablePrefix());
        modified |= !this.basePackageField.getText().equals(this.config.getProjectConfig().getBasePackage());
        modified |= !this.idGeneratorField.getText().equals(this.config.getProjectConfig().getIdGenerator());
        modified |= !this.serviceInterfaceField.getText().equals(this.config.getProjectConfig().getIService());
        modified |= !this.serviceImplField.getText().equals(this.config.getProjectConfig().getServiceImpl());
        modified |= !this.facadeInterfaceField.getText().equals(this.config.getProjectConfig().getIFacade());
        modified |= !this.facadeImplField.getText().equals(this.config.getProjectConfig().getFacadeImpl());
        modified |= !this.daoInterfaceField.getText().equals(this.config.getProjectConfig().getIDao());
        modified |= !this.rootObjectField.getText().equals(this.config.getProjectConfig().getBaseObject());
        modified |= !this.ignoreColumnsField.getText().equals(this.config.getProjectConfig().getIgnoreColumn());
        modified |= !this.nonFuzzyColumnField.getText().equals(this.config.getProjectConfig().getNonFuzzyColumn());

        for (Boolean aBoolean : boxChanged) {
            modified |= aBoolean;
        }

        return modified;
    }

    public void apply() {
        ProjectConfig globalConfig = new ProjectConfig();
        globalConfig.setModuleRootPath(moduleRootField.getText());
        globalConfig.setSourcePath(sourcePathField.getText());
        globalConfig.setResourcePath(resourcePathField.getText());

        globalConfig.setTablePrefix(tablePrefixField.getText());
        globalConfig.setBasePackage(basePackageField.getText());
        globalConfig.setIdGenerator(idGeneratorField.getText());
        globalConfig.setIService(serviceInterfaceField.getText());
        globalConfig.setServiceImpl(serviceImplField.getText());
        globalConfig.setIFacade(facadeInterfaceField.getText());
        globalConfig.setFacadeImpl(facadeImplField.getText());
        globalConfig.setIDao(daoInterfaceField.getText());
        globalConfig.setBaseObject(rootObjectField.getText());
        globalConfig.setIgnoreColumn(ignoreColumnsField.getText());
        globalConfig.setNonFuzzyColumn(nonFuzzyColumnField.getText());

        globalConfig.setComment(commentBox.getSelectedObjects() != null);
        globalConfig.setOverride(overrideBox.getSelectedObjects() != null);
        globalConfig.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
        globalConfig.setToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
        globalConfig.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
        globalConfig.setMysql8(mysql8Box.getSelectedObjects() != null);
        globalConfig.setLombokPlugin(lombokAnnotationBox.getSelectedObjects() != null);
        globalConfig.setSwagger2Plugin(swaggerAnnotationBox.getSelectedObjects() != null);
        globalConfig.setFacadePlugin(generateFacade.getSelectedObjects() != null);
        globalConfig.setFastjsonPlugin(fastJson.getSelectedObjects() != null);

        for (int i = 0; i < 12; i++) {
            boxChanged[i] = false;
        }

        this.config.setProjectConfig(globalConfig);
    }
}
