package org.xstudio.plugin.idea.ui;

import com.xstudio.mybatis.po.Properties;
import org.xstudio.plugin.idea.mybatis.generator.PersistentProperties;
import org.xstudio.plugin.idea.mybatis.generator.PluginProperties;
import org.xstudio.plugin.idea.setting.DefaultPersistentConfiguration;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.Objects;

/**
 * @author Beeant
 * @version 2020/6/7
 */
public class DefaultSettingUI extends JDialog {

    private final PersistentProperties persistentConfig;
    private JCheckBox chkComment;
    private JCheckBox chkFastJson;
    private JCheckBox chkGenerateFacade;
    private JCheckBox chkLombok;
    private JCheckBox chkMarkDelete;
    private JCheckBox chkMySQL8;
    private JCheckBox chkOverwrite;
    private JCheckBox chkRootEntityObject;
    private JCheckBox chkSwaggerModel;
    private JCheckBox chkToString;
    private JCheckBox chkUseAlias;
    private JCheckBox chkUseSchemaPrefix;
    private JPanel mainPanel;
    private JTextField tCfgName;
    private JTextField tFacadeImplement;
    private JTextField tFacadeInterface;
    private JTextField tIdGenerator;
    private JTextField tIgnoreColumns;
    private JTextField tMapperInterface;
    private JTextField tNonFuzzySearchColumns;
    private JTextField tReplaceString;
    private JTextField tResourcePath;
    private JTextField tRootObject;
    private JTextField tRootPackage;
    private JTextField tSearchString;
    private JTextField tServiceImplement;
    private JTextField tServiceInterface;
    private JTextField tSrcPath;
    private JTextField tResponseObject;
    private JComboBox comboxCfgName;

    public DefaultSettingUI() {
        DefaultPersistentConfiguration instance = DefaultPersistentConfiguration.getInstance();
        assert instance != null;
        Map<String, PersistentProperties> configs = instance.getConfigs();
        persistentConfig = instance.getPersistentConfig();
        if (null != persistentConfig) {
            initialPanel(persistentConfig);
        }
        comboxCfgName.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    Object item = event.getItem();
                    // do something with object
                }
            }
        });
    }

    private void initialPanel(PersistentProperties config) {
        tCfgName.setText(config.getCfgName());
        tSrcPath.setText(config.getSrcPath());
        tResourcePath.setText(config.getResourcePath());
        tSearchString.setText(config.getSearchString());
        tReplaceString.setText(config.getReplaceString());
        tRootPackage.setText(config.getRootPackage());
        tIdGenerator.setText(config.getIdGenerator());
        tServiceInterface.setText(config.getServiceInterface());
        tServiceImplement.setText(config.getServiceImpl());
        tFacadeInterface.setText(config.getFacadeInterface());
        tFacadeImplement.setText(config.getFacadeImpl());
        tMapperInterface.setText(config.getMapperInterface());

        tRootObject.setText(config.getRootClass());

        tIgnoreColumns.setText(config.getIgnoreColumns());
        tNonFuzzySearchColumns.setText(config.getNonFuzzySearchColumn());

        PluginProperties plugin = config.getPlugin();
        if (null != plugin) {
            chkComment.setSelected(plugin.isChkComment());
            chkFastJson.setSelected(plugin.isChkFastJson());
            chkGenerateFacade.setSelected(plugin.isChkGenerateFacade());
            chkLombok.setSelected(plugin.isChkLombok());
            chkMarkDelete.setSelected(plugin.isChkMarkDelete());
            chkMySQL8.setSelected(plugin.isChkMySQL8());
            chkOverwrite.setSelected(plugin.isChekOverwrite());
            chkRootEntityObject.setSelected(plugin.isChkRootEntityObject());
            chkSwaggerModel.setSelected(plugin.isChkSwaggerModel());
            chkToString.setSelected(plugin.isChkToString());
            chkUseAlias.setSelected(plugin.isChkUseAlias());
            chkUseSchemaPrefix.setSelected(plugin.isChkUseSchemaPrefix());
        }
    }

    public void apply() {
        persistentConfig.setCfgName(tCfgName.getText());
        persistentConfig.setFacadeImpl(tFacadeImplement.getText());
        persistentConfig.setFacadeInterface(tFacadeInterface.getText());
        persistentConfig.setIdGenerator(tIdGenerator.getText());
        persistentConfig.setIgnoreColumns(tIgnoreColumns.getText());
        persistentConfig.setMapperInterface(tMapperInterface.getText());
        persistentConfig.setNonFuzzySearchColumn(tNonFuzzySearchColumns.getText());
        PluginProperties pluginProperties = new PluginProperties();
        pluginProperties.setChkComment(chkComment.isSelected());
        pluginProperties.setChkToString(chkToString.isSelected());
        pluginProperties.setChkUseAlias(chkUseAlias.isSelected());
        pluginProperties.setChkLombok(chkLombok.isSelected());
        pluginProperties.setChkGenerateFacade(chkGenerateFacade.isSelected());
        pluginProperties.setChkRootEntityObject(chkRootEntityObject.isSelected());
        pluginProperties.setChekOverwrite(chkOverwrite.isSelected());
        pluginProperties.setChkUseSchemaPrefix(chkUseSchemaPrefix.isSelected());
        pluginProperties.setChkMySQL8(chkMySQL8.isSelected());
        pluginProperties.setChkSwaggerModel(chkSwaggerModel.isSelected());
        pluginProperties.setChkMarkDelete(chkMarkDelete.isSelected());
        pluginProperties.setChkFastJson(chkFastJson.isSelected());

        persistentConfig.setPlugin(pluginProperties);
        persistentConfig.setReplaceString(tReplaceString.getText());
        persistentConfig.setResourcePath(tResourcePath.getText());
        persistentConfig.setRootClass(tRootObject.getText());
        persistentConfig.setRootPackage(tRootPackage.getText());
        persistentConfig.setSearchString(tSearchString.getText());
        persistentConfig.setServiceImpl(tServiceImplement.getText());
        persistentConfig.setServiceInterface(tServiceInterface.getText());
        persistentConfig.setSrcPath(tSrcPath.getText());
        persistentConfig.setResponseObject(tResponseObject.getText());

        Objects.requireNonNull(DefaultPersistentConfiguration.getInstance()).setPersistentConfig(persistentConfig);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public boolean isModified() {

        boolean modified = !tSrcPath.getText().equals(persistentConfig.getSrcPath());
        modified |= !tCfgName.getText().equals(persistentConfig.getCfgName());
        modified |= !tResourcePath.getText().equals(persistentConfig.getResourcePath());

        modified |= !tSearchString.getText().equals(persistentConfig.getSearchString());
        modified |= !tReplaceString.getText().equals(persistentConfig.getReplaceString());
        modified |= !tRootPackage.getText().equals(persistentConfig.getRootPackage());
        modified |= !tIdGenerator.getText().equals(persistentConfig.getIdGenerator());
        modified |= !tServiceInterface.getText().equals(persistentConfig.getServiceInterface());
        modified |= !tServiceImplement.getText().equals(persistentConfig.getServiceImpl());
        modified |= !tFacadeInterface.getText().equals(persistentConfig.getFacadeInterface());
        modified |= !tFacadeImplement.getText().equals(persistentConfig.getFacadeImpl());
        modified |= !tMapperInterface.getText().equals(persistentConfig.getMapperInterface());
        modified |= !tRootObject.getText().equals(persistentConfig.getRootClass());
        modified |= !tResponseObject.getText().equals(persistentConfig.getResponseObject());
        modified |= !tIgnoreColumns.getText().equals(persistentConfig.getIgnoreColumns());
        modified |= !tNonFuzzySearchColumns.getText().equals(persistentConfig.getNonFuzzySearchColumn());

        PluginProperties plugin = persistentConfig.getPlugin();
        modified |= chkComment.isSelected() != plugin.isChkComment();
        modified |= chkFastJson.isSelected() != plugin.isChkFastJson();
        modified |= chkRootEntityObject.isSelected() != plugin.isChkRootEntityObject();
        modified |= chkMarkDelete.isSelected() != plugin.isChkMarkDelete();
        modified |= chkGenerateFacade.isSelected() != plugin.isChkGenerateFacade();
        modified |= chkSwaggerModel.isSelected() != plugin.isChkSwaggerModel();
        modified |= chkLombok.isSelected() != plugin.isChkLombok();
        modified |= chkMySQL8.isSelected() != plugin.isChkMySQL8();
        modified |= chkUseAlias.isSelected() != plugin.isChkUseAlias();
        modified |= chkOverwrite.isSelected() != plugin.isChekOverwrite();
        modified |= chkToString.isSelected() != plugin.isChkToString();
        modified |= chkUseSchemaPrefix.isSelected() != plugin.isChkUseSchemaPrefix();

        return modified;
    }
}
