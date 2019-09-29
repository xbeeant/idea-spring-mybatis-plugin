package org.xstudio.plugin.idea.ui;

import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPanel;
import lombok.Getter;
import lombok.Setter;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.model.PersistentConfig;
import org.xstudio.plugin.idea.setting.DefaultPersistentConfiguration;
import org.xstudio.plugin.idea.util.JavaUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author xiaobiao
 * @version 2019/9/23
 */
public class DefaultSettingUi extends JDialog {
    public JPanel contentPanel = new JBPanel<>();
    // =================
    // path
    // =================
    @Getter
    JPanel projectRootPanel = new JPanel();

    private DefaultPersistentConfiguration persistentConfiguration;

    @Setter
    @Getter
    private PersistentConfig persistentConfig;
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
    private JCheckBox generateFacadeBox = new JCheckBox("Generate Facade");
    private JCheckBox markDeleteBox = new JCheckBox("Mark Delete");
    private JCheckBox rootObjectBox = new JCheckBox("Root Entity Object");
    private JCheckBox fastJsonBox = new JCheckBox("Fast Json");

    private boolean[] boxChanged;

    public DefaultSettingUi() {
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

        persistentConfiguration = DefaultPersistentConfiguration.getInstance();
        persistentConfig = persistentConfiguration.getPersistentConfig();
        initPathPanel();
        initProjectPanel();
        initOptionsPanel();
    }

    private void initPathPanel() {

        JPanel sourcePathPanel = JavaUtil.panelField("Source Path:", sourcePathField, persistentConfig.getSourcePath()).getPanel();

        JPanel resourcePathPanel = JavaUtil.panelField("Resource Path:", resourcePathField, persistentConfig.getResourcePath()).getPanel();

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        TitledSeparator separator = new TitledSeparator();
        separator.setText("Path");
        pathPanel.add(sourcePathPanel);
        pathPanel.add(resourcePathPanel);
        contentPanel.add(separator);
        contentPanel.add(pathPanel);
    }

    public void initProjectPanel() {
        PersistentConfig globalConfig = persistentConfiguration.getPersistentConfig();
        JPanel projectPanel = new JPanel();
        projectPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));


        // ========= module root
        projectRootPanel.setLayout(new BoxLayout(projectRootPanel, BoxLayout.X_AXIS));
        projectPanel.add(JavaUtil.panelField("Table Prefix:", tablePrefixField, globalConfig.getTablePrefix()).getPanel());
        projectPanel.add(JavaUtil.panelField("Code Package:", basePackageField, globalConfig.getBasePackage()).getPanel());
        projectPanel.add(JavaUtil.panelField("Id Generator:", idGeneratorField, globalConfig.getIdGenerator()).getPanel());
        projectPanel.add(JavaUtil.panelField("Service Interface:", serviceInterfaceField, globalConfig.getIService()).getPanel());
        projectPanel.add(JavaUtil.panelField("Service Impl:", serviceImplField, globalConfig.getServiceImpl()).getPanel());
        projectPanel.add(JavaUtil.panelField("Facade Interface:", facadeInterfaceField, globalConfig.getIFacade()).getPanel());
        projectPanel.add(JavaUtil.panelField("Facade Impl:", facadeImplField, globalConfig.getFacadeImpl()).getPanel());
        projectPanel.add(JavaUtil.panelField("Dao Interface:", daoInterfaceField, globalConfig.getIDao()).getPanel());
        projectPanel.add(JavaUtil.panelField("Root Object:", rootObjectField, globalConfig.getBaseObject()).getPanel());
        projectPanel.add(JavaUtil.panelField("Ignore Columns:", ignoreColumnsField, globalConfig.getIgnoreColumn()).getPanel());
        projectPanel.add(JavaUtil.panelField("Non Fuzzy Search Columns:", nonFuzzyColumnField, globalConfig.getNonFuzzyColumn()).getPanel());


        TitledSeparator separator = new TitledSeparator();
        separator.setText("Project Configuration");
        contentPanel.add(separator);
        contentPanel.add(projectPanel);
    }

    private void initOptionsPanel() {
        JBPanel optionsPanel = new JBPanel(new GridLayout(6, 2, 10, 10));

        commentBox.setSelected(persistentConfig.isComment());
        overrideBox.setSelected(persistentConfig.isOverride());
        needToStringHashcodeEqualsBox.setSelected(persistentConfig.isToStringHashcodeEquals());
        useSchemaPrefixBox.setSelected(persistentConfig.isUseSchemaPrefix());
        useTableNameAliasBox.setSelected(persistentConfig.isUseTableNameAlias());
        mysql8Box.setSelected(persistentConfig.isMysql8());
        lombokAnnotationBox.setSelected(persistentConfig.isLombokPlugin());
        swaggerAnnotationBox.setSelected(persistentConfig.isSwagger2Plugin());
        generateFacadeBox.setSelected(persistentConfig.isFacadePlugin());
        markDeleteBox.setSelected(persistentConfig.isMarkDeletePlugin());
        rootObjectBox.setSelected(persistentConfig.isRootObjectPlugin());
        fastJsonBox.setSelected(persistentConfig.isFastjsonPlugin());

        JavaUtil.boxListener(commentBox, Constant.CommentBox, "comment", boxChanged, persistentConfig);
        JavaUtil.boxListener(overrideBox, Constant.Overwrite, "override", boxChanged, persistentConfig);
        JavaUtil.boxListener(needToStringHashcodeEqualsBox, Constant.ToStringHashEquals, "toStringHashcodeEquals", boxChanged, persistentConfig);
        JavaUtil.boxListener(useSchemaPrefixBox, Constant.SchemaPrefix, "useSchemaPrefix", boxChanged, persistentConfig);
        JavaUtil.boxListener(useTableNameAliasBox, Constant.UseAlias, "useTableNameAlias", boxChanged, persistentConfig);
        JavaUtil.boxListener(mysql8Box, Constant.MySql8, "mysql8", boxChanged, persistentConfig);
        JavaUtil.boxListener(lombokAnnotationBox, Constant.Lombok, "lombokPlugin", boxChanged, persistentConfig);
        JavaUtil.boxListener(swaggerAnnotationBox, Constant.Swagger, "swagger2Plugin", boxChanged, persistentConfig);
        JavaUtil.boxListener(generateFacadeBox, Constant.Facade, "facadePlugin", boxChanged, persistentConfig);
        JavaUtil.boxListener(markDeleteBox, Constant.MarkDelete, "markDeletePlugin", boxChanged, persistentConfig);
        JavaUtil.boxListener(rootObjectBox, Constant.RootEntity, "rootObjectPlugin", boxChanged, persistentConfig);
        JavaUtil.boxListener(fastJsonBox, Constant.FastJson, "fastjsonPlugin", boxChanged, persistentConfig);

        optionsPanel.add(commentBox);
        optionsPanel.add(overrideBox);
        optionsPanel.add(needToStringHashcodeEqualsBox);
        optionsPanel.add(useSchemaPrefixBox);
        optionsPanel.add(useTableNameAliasBox);
        optionsPanel.add(mysql8Box);
        optionsPanel.add(lombokAnnotationBox);
        optionsPanel.add(swaggerAnnotationBox);
        optionsPanel.add(generateFacadeBox);
        optionsPanel.add(markDeleteBox);
        optionsPanel.add(rootObjectBox);
        optionsPanel.add(fastJsonBox);

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
        boolean modified = !this.sourcePathField.getText().equals(persistentConfig.getSourcePath());
        modified |= !this.resourcePathField.getText().equals(persistentConfig.getResourcePath());

        modified |= !this.tablePrefixField.getText().equals(persistentConfig.getTablePrefix());
        modified |= !this.basePackageField.getText().equals(persistentConfig.getBasePackage());
        modified |= !this.idGeneratorField.getText().equals(persistentConfig.getIdGenerator());
        modified |= !this.serviceInterfaceField.getText().equals(persistentConfig.getIService());
        modified |= !this.serviceImplField.getText().equals(persistentConfig.getServiceImpl());
        modified |= !this.facadeInterfaceField.getText().equals(persistentConfig.getIFacade());
        modified |= !this.facadeImplField.getText().equals(persistentConfig.getFacadeImpl());
        modified |= !this.daoInterfaceField.getText().equals(persistentConfig.getIDao());
        modified |= !this.rootObjectField.getText().equals(persistentConfig.getBaseObject());
        modified |= !this.ignoreColumnsField.getText().equals(persistentConfig.getIgnoreColumn());
        modified |= !this.nonFuzzyColumnField.getText().equals(persistentConfig.getNonFuzzyColumn());

        for (Boolean aBoolean : boxChanged) {
            modified |= aBoolean;
        }

        return modified;
    }

    public void apply() {
        persistentConfig.setSourcePath(sourcePathField.getText());
        persistentConfig.setResourcePath(resourcePathField.getText());

        persistentConfig.setTablePrefix(tablePrefixField.getText());
        persistentConfig.setBasePackage(basePackageField.getText());
        persistentConfig.setIdGenerator(idGeneratorField.getText());
        persistentConfig.setIService(serviceInterfaceField.getText());
        persistentConfig.setServiceImpl(serviceImplField.getText());
        persistentConfig.setIFacade(facadeInterfaceField.getText());
        persistentConfig.setFacadeImpl(facadeImplField.getText());
        persistentConfig.setIDao(daoInterfaceField.getText());
        persistentConfig.setBaseObject(rootObjectField.getText());
        persistentConfig.setIgnoreColumn(ignoreColumnsField.getText());
        persistentConfig.setNonFuzzyColumn(nonFuzzyColumnField.getText());

        persistentConfig.setComment(commentBox.getSelectedObjects() != null);
        persistentConfig.setOverride(overrideBox.getSelectedObjects() != null);
        persistentConfig.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
        persistentConfig.setToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
        persistentConfig.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
        persistentConfig.setMysql8(mysql8Box.getSelectedObjects() != null);
        persistentConfig.setLombokPlugin(lombokAnnotationBox.getSelectedObjects() != null);
        persistentConfig.setSwagger2Plugin(swaggerAnnotationBox.getSelectedObjects() != null);
        persistentConfig.setFacadePlugin(generateFacadeBox.getSelectedObjects() != null);
        persistentConfig.setFastjsonPlugin(fastJsonBox.getSelectedObjects() != null);

        for (int i = 0; i < 12; i++) {
            boxChanged[i] = false;
        }

        this.persistentConfiguration.setPersistentConfig(persistentConfig);
    }
}
