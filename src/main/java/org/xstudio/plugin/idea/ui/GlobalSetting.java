package org.xstudio.plugin.idea.ui;

import org.xstudio.plugin.idea.model.PersistentConfig;
import org.xstudio.plugin.idea.setting.DefaultPersistentConfiguration;

import javax.swing.*;

/**
 * default setting
 *
 * @author xiaobiao
 * @version 2020/5/5
 */
public class GlobalSetting {
    private JPanel mainPanel;
    private JTextField srcPathField;
    private JTextField resourcesPathField;
    private JTextField tablePrefixField;
    private JTextField codePackageField;
    private JTextField idGeneratorField;
    private JTextField serviceInterfaceField;
    private JTextField serviceImplField;
    private JTextField facadeInterfaceField;
    private JTextField facadeImplField;
    private JTextField daoInterfaceField;
    private JTextField rootObjectField;
    private JTextField ignoreColumnsField;
    private JTextField nonFuzzySearchField;
    private JCheckBox commentCheckBox;
    private JCheckBox overwriteCheckBox;
    private JCheckBox toStringHashCodeEqualsCheckBox;
    private JCheckBox userSchemaPrefixCheckBox;
    private JCheckBox useAliasCheckBox;
    private JCheckBox lombokCheckBox;
    private JCheckBox generateFacadeCheckBox;
    private JCheckBox rootEntityObjectCheckBox;
    private JCheckBox mysql8CheckBox;
    private JCheckBox swaggerModelCheckBox;
    private JCheckBox markDeleteCheckBox;
    private JCheckBox fastJsonCheckBox;
    private JTextField typeField;

    private DefaultPersistentConfiguration persistentConfiguration;

    private PersistentConfig persistentConfig;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public JTextField getSrcPathField() {
        return srcPathField;
    }

    public JTextField getResourcesPathField() {
        return resourcesPathField;
    }

    public JTextField getTablePrefixField() {
        return tablePrefixField;
    }

    public JTextField getCodePackageField() {
        return codePackageField;
    }

    public JTextField getIdGeneratorField() {
        return idGeneratorField;
    }

    public JTextField getServiceInterfaceField() {
        return serviceInterfaceField;
    }

    public JTextField getServiceImplField() {
        return serviceImplField;
    }

    public JTextField getFacadeInterfaceField() {
        return facadeInterfaceField;
    }

    public JTextField getFacadeImplField() {
        return facadeImplField;
    }

    public JTextField getDaoInterfaceField() {
        return daoInterfaceField;
    }

    public JTextField getRootObjectField() {
        return rootObjectField;
    }

    public JTextField getIgnoreColumnsField() {
        return ignoreColumnsField;
    }

    public JTextField getNonFuzzySearchField() {
        return nonFuzzySearchField;
    }

    public JCheckBox getCommentCheckBox() {
        return commentCheckBox;
    }

    public JCheckBox getOverwriteCheckBox() {
        return overwriteCheckBox;
    }

    public JCheckBox getToStringHashCodeEqualsCheckBox() {
        return toStringHashCodeEqualsCheckBox;
    }

    public JCheckBox getUserSchemaPrefixCheckBox() {
        return userSchemaPrefixCheckBox;
    }

    public JCheckBox getUseAliasCheckBox() {
        return useAliasCheckBox;
    }

    public JCheckBox getLombokCheckBox() {
        return lombokCheckBox;
    }

    public JCheckBox getGenerateFacadeCheckBox() {
        return generateFacadeCheckBox;
    }

    public JCheckBox getRootEntityObjectCheckBox() {
        return rootEntityObjectCheckBox;
    }

    public JCheckBox getMysql8CheckBox() {
        return mysql8CheckBox;
    }

    public JCheckBox getSwaggerModelCheckBox() {
        return swaggerModelCheckBox;
    }

    public JCheckBox getMarkDeleteCheckBox() {
        return markDeleteCheckBox;
    }

    public JCheckBox getFastJsonCheckBox() {
        return fastJsonCheckBox;
    }

    public JTextField getTypeField() {
        return typeField;
    }

    public boolean isModified() {
        boolean modified = !this.srcPathField.getText().equals(persistentConfig.getSourcePath());
        modified |= !this.typeField.getText().equals(persistentConfig.getName());
        modified |= !this.resourcesPathField.getText().equals(persistentConfig.getResourcePath());

        modified |= !this.tablePrefixField.getText().equals(persistentConfig.getTablePrefix());
        modified |= !this.codePackageField.getText().equals(persistentConfig.getBasePackage());
        modified |= !this.idGeneratorField.getText().equals(persistentConfig.getIdGenerator());
        modified |= !this.serviceInterfaceField.getText().equals(persistentConfig.getIService());
        modified |= !this.serviceImplField.getText().equals(persistentConfig.getServiceImpl());
        modified |= !this.facadeInterfaceField.getText().equals(persistentConfig.getIFacade());
        modified |= !this.facadeImplField.getText().equals(persistentConfig.getFacadeImpl());
        modified |= !this.daoInterfaceField.getText().equals(persistentConfig.getIDao());
        modified |= !this.rootObjectField.getText().equals(persistentConfig.getBaseObject());
        modified |= !this.ignoreColumnsField.getText().equals(persistentConfig.getIgnoreColumn());
        modified |= !this.nonFuzzySearchField.getText().equals(persistentConfig.getNonFuzzyColumn());

        // todo box changed

        return modified;
    }

    public void apply() {

    }
}
