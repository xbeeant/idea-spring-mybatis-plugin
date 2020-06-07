package org.xstudio.plugin.idea.ui;

import org.xstudio.plugin.idea.model.PersistentConfig;
import org.xstudio.plugin.idea.setting.DefaultPersistentConfiguration;

import javax.swing.*;
import java.util.Objects;

/**
 * @author Beeant
 * @version 2020/6/7
 */
public class DefaultSettingUI extends JDialog {

    private JPanel mainPanel;
    private JTextField tSrcPath;
    private JCheckBox chkComment;
    private JCheckBox chkOverwrite;
    private JCheckBox chkToString;
    private JCheckBox chkUseAlias;
    private JCheckBox chkLombok;
    private JCheckBox chkGenerateFacade;
    private JCheckBox chkRootEntityObject;
    private JCheckBox chkUseSchemaPrefix;
    private JCheckBox chkMySQL8;
    private JCheckBox chkSwaggerModel;
    private JCheckBox chkMarkDelete;
    private JCheckBox chkFastJson;
    private JTextField tIdGenerator;
    private JTextField tServiceInterface;
    private JTextField tServiceImplement;
    private JTextField tFacadeInterface;
    private JTextField tFacadeImplement;
    private JTextField tMapperInterface;
    private JTextField tRootObject;
    private JTextField tIgnoreColumns;
    private JTextField tNonFuzzySearchColumns;
    private JTextField tConfigurationName;
    private JTextField tResourcePath;
    private JTextField tRootPackage;
    private JTextField tTablePrefix;

    private final PersistentConfig persistentConfig;

    public DefaultSettingUI() {
        DefaultPersistentConfiguration instance = DefaultPersistentConfiguration.getInstance();
        assert instance != null;
        persistentConfig = instance.getPersistentConfig();
        if (null != persistentConfig) {
            initialPanel(persistentConfig);
        }
    }

    private void initialPanel(PersistentConfig config) {
        tConfigurationName.setText(config.getConfigName());
        tSrcPath.setText(config.getSourcePath());
        tResourcePath.setText(config.getResourcePath());
        tTablePrefix.setText(config.getTablePrefix());
        tRootPackage.setText(config.getRootPackage());
        tIdGenerator.setText(config.getIdGenerator());
        tServiceInterface.setText(config.getServiceInterface());
        tServiceImplement.setText(config.getServiceImplement());
        tFacadeInterface.setText(config.getFacadeInterface());
        tFacadeImplement.setText(config.getFacadeImplement());
        tMapperInterface.setText(config.getDaoInterface());
        tRootObject.setText(config.getBaseObject());
        tIgnoreColumns.setText(config.getIgnoreColumn());
        tNonFuzzySearchColumns.setText(config.getNonFuzzyColumn());

        chkComment.setSelected(config.isComment());
        chkOverwrite.setSelected(config.isOverride());
        chkToString.setSelected(config.isToStringHashcodeEquals());
        chkUseSchemaPrefix.setSelected(config.isUseSchemaPrefix());
        chkUseAlias.setSelected(config.isUseTableNameAlias());
        chkMySQL8.setSelected(config.isMysql8());
        chkLombok.setSelected(config.isLombokPlugin());
        chkSwaggerModel.setSelected(config.isSwagger2Plugin());
        chkGenerateFacade.setSelected(config.isFacadePlugin());
        chkMarkDelete.setSelected(config.isMarkDeletePlugin());
        chkRootEntityObject.setSelected(config.isRootObjectPlugin());
        chkFastJson.setSelected(config.isFastjsonPlugin());
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public boolean isModified() {
        boolean modified = !tSrcPath.getText().equals(persistentConfig.getSourcePath());
        modified |= !tConfigurationName.getText().equals(persistentConfig.getConfigName());
        modified |= !tResourcePath.getText().equals(persistentConfig.getResourcePath());

        modified |= !tTablePrefix.getText().equals(persistentConfig.getTablePrefix());
        modified |= !tRootPackage.getText().equals(persistentConfig.getRootPackage());
        modified |= !tIdGenerator.getText().equals(persistentConfig.getIdGenerator());
        modified |= !tServiceInterface.getText().equals(persistentConfig.getServiceInterface());
        modified |= !tServiceImplement.getText().equals(persistentConfig.getServiceImplement());
        modified |= !tFacadeInterface.getText().equals(persistentConfig.getFacadeInterface());
        modified |= !tFacadeImplement.getText().equals(persistentConfig.getFacadeImplement());
        modified |= !tMapperInterface.getText().equals(persistentConfig.getDaoInterface());
        modified |= !tRootObject.getText().equals(persistentConfig.getBaseObject());
        modified |= !tIgnoreColumns.getText().equals(persistentConfig.getIgnoreColumn());
        modified |= !tNonFuzzySearchColumns.getText().equals(persistentConfig.getNonFuzzyColumn());

        modified |= chkComment.isSelected() != persistentConfig.isComment();
        modified |= chkFastJson.isSelected() != persistentConfig.isFastjsonPlugin();
        modified |= chkRootEntityObject.isSelected() != persistentConfig.isRootObjectPlugin();
        modified |= chkMarkDelete.isSelected() != persistentConfig.isMarkDeletePlugin();
        modified |= chkGenerateFacade.isSelected() != persistentConfig.isFacadePlugin();
        modified |= chkSwaggerModel.isSelected() != persistentConfig.isSwagger2Plugin();
        modified |= chkLombok.isSelected() != persistentConfig.isLombokPlugin();
        modified |= chkMySQL8.isSelected() != persistentConfig.isMysql8();
        modified |= chkUseAlias.isSelected() != persistentConfig.isUseTableNameAlias();
        modified |= chkOverwrite.isSelected() != persistentConfig.isOverride();
        modified |= chkToString.isSelected() != persistentConfig.isToStringHashcodeEquals();
        modified |= chkUseSchemaPrefix.isSelected() != persistentConfig.isUseSchemaPrefix();

        return modified;
    }

    public void apply() {
        persistentConfig.setSourcePath(tSrcPath.getText());
        persistentConfig.setConfigName(tConfigurationName.getText());
        persistentConfig.setResourcePath(tResourcePath.getText());
        persistentConfig.setTablePrefix(tTablePrefix.getText());
        persistentConfig.setRootPackage(tRootPackage.getText());
        persistentConfig.setIdGenerator(tIdGenerator.getText());
        persistentConfig.setServiceInterface(tServiceInterface.getText());
        persistentConfig.setServiceImplement(tServiceImplement.getText());
        persistentConfig.setFacadeInterface(tFacadeInterface.getText());
        persistentConfig.setFacadeImplement(tFacadeImplement.getText());
        persistentConfig.setDaoInterface(tMapperInterface.getText());
        persistentConfig.setBaseObject(tRootObject.getText());
        persistentConfig.setIgnoreColumn(tIgnoreColumns.getText());
        persistentConfig.setNonFuzzyColumn(tNonFuzzySearchColumns.getText());

        persistentConfig.setComment(chkComment.isSelected());
        persistentConfig.setFastjsonPlugin(chkFastJson.isSelected());
        persistentConfig.setRootObjectPlugin(chkRootEntityObject.isSelected());
        persistentConfig.setMarkDeletePlugin(chkMarkDelete.isSelected());
        persistentConfig.setFacadePlugin(chkGenerateFacade.isSelected());
        persistentConfig.setSwagger2Plugin(chkSwaggerModel.isSelected());
        persistentConfig.setLombokPlugin(chkLombok.isSelected());
        persistentConfig.setMysql8(chkMySQL8.isSelected());
        persistentConfig.setUseTableNameAlias(chkUseAlias.isSelected());
        persistentConfig.setOverride(chkOverwrite.isSelected());
        persistentConfig.setToStringHashcodeEquals(chkToString.isSelected());
        persistentConfig.setUseSchemaPrefix(chkUseSchemaPrefix.isSelected());

        Objects.requireNonNull(DefaultPersistentConfiguration.getInstance()).setPersistentConfig(persistentConfig);
    }
}
