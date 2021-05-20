package org.xstudio.plugin.idea.ui;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbNamespaceImpl;
import com.intellij.database.psi.DbTableImpl;
import com.intellij.database.util.DasUtil;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.JBIterable;
import io.github.xbeeant.mybatis.po.ColumnProperty;
import org.jetbrains.annotations.Nullable;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.model.Credential;
import org.xstudio.plugin.idea.model.TableInfo;
import org.xstudio.plugin.idea.mybatis.MybatisCommander;
import org.xstudio.plugin.idea.mybatis.generator.PluginProperties;
import org.xstudio.plugin.idea.mybatis.generator.ProjectPersistentProperties;
import org.xstudio.plugin.idea.setting.ProjectPersistentConfiguration;
import org.xstudio.plugin.idea.util.ColumnsSettingHandler;
import org.xstudio.plugin.idea.util.DatabaseUtils;
import org.xstudio.plugin.idea.util.ModuleUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Beeant
 * @version 2020/6/7
 */
public class CodeGeneratorUI extends DialogWrapper {

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
    private java.util.List<DasColumn> columns = new ArrayList<>();
    private JTable columnsTable;
    private ModulesComboBox comboModule;
    private RawConnectionConfig connectionConfig;
    private DbDataSource dbDataSource;
    /**
     * idea 事件对象
     */
    private AnActionEvent event;
    private JPanel mainPanel;
    private Project project;
    private ProjectPersistentConfiguration projectPersistent;
    private ProjectPersistentProperties projectProperties;
    private JTextField tFacadeImplement;
    private JTextField tFacadeInterface;
    private JTextField tIdGenerator;
    private JTextField tIgnoreColumns;
    private JTextField tMapperInterface;
    private JTextField tNonFuzzySearchColumns;
    private JTextField tReplaceString;
    private JTextField tResponseObject;
    private JTextField tRootObject;
    private JTextField tRootPackage;
    private JTextField tSearchString;
    private JTextField tServiceImplement;
    private JTextField tServiceInterface;
    private JTabbedPane tabbedPane;
    private JTextField tpTableName;
    private JCheckBox chkBeginEnd;
    private JCheckBox chkDateTime;
    private java.util.List<ColumnProperty> introspectedColumnList;

    private static final Pattern HANDLER_PATTERN = Pattern.compile("#handler\\s*:\\s*([\\w\\W]*)#");

    public CodeGeneratorUI(AnActionEvent event, @Nullable Project project, TableInfo tableInfo) {
        super(project);
        this.project = project;
        this.event = event;
        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        // =================================
        // 从所选择的元素中获取数据库的配置
        // =================================

        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("Please select one table or more then one columns", Constant.TITLE, Messages.getWarningIcon());
            return;
        }
        PsiElement current = psiElements[0];
        dbDataSource = null;
        String databaseName = "";
        // 遍历父节点，判断是否已经到达顶层节点（数据库配置）
        while (current != null) {
            if (DbTableImpl.class.isAssignableFrom(current.getClass())) {
                JBIterable<? extends DasColumn> columnsIter = DasUtil.getColumns((DasObject) current);
                java.util.List<? extends DasColumn> dasColumns = columnsIter.toList();
                columns = new ArrayList<>(dasColumns.size());
                columns.addAll(dasColumns);
            }
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

        this.projectPersistent = ProjectPersistentConfiguration.getInstance(project);

        String tableName = tableInfo.getTableName();

        projectProperties = projectPersistent.getTableConfig(databaseName, tableName);
        projectProperties.setType(tableInfo.getTypeName());
        projectProperties.setDatabase(databaseName);
        projectProperties.setSchema(tableName);
        this.setTitle("Mybatis Spring Code Generator");
        mainPanel.setPreferredSize(new Dimension(800, 300));
        this.init();
        // initial panel defaults value
        initialPanel(projectProperties);

        //
        tpTableName.setText(tableName);

        // module
        assert project != null;
        comboModule.fillModules(project);

        ModuleManager moduleManager = ModuleManager.getInstance(project);
        Module[] modules = moduleManager.getSortedModules();
        for (Module module : modules) {
            VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
            if (contentRoots.length == 0) {
                continue;
            }
            // set [main] selected
            if (module.getName().contains(".main")) {
                comboModule.setSelectedModule(module);
            }

            String url = contentRoots[0].getPath();
            // todo equals
            if (url.equals("/scr/")) {
                comboModule.setSelectedModule(module);
                break;
            }
        }

        createColumnsTable();
    }

    private void initialPanel(ProjectPersistentProperties config) {
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
        tResponseObject.setText(config.getResponseObject());

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
            chkDateTime.setSelected(plugin.isChkDateTime());
            chkBeginEnd.setSelected(plugin.isChkBeginEnd());
        }
    }

    private void createColumnsTable() {
        /*
         * 初始化JTable里面各项的值，设置两个一模一样的实体"赵匡义"学生。
         */
        ColumnsSettingHandler handler = new ColumnsSettingHandler();
        introspectedColumnList = new ArrayList<>(columns.size());

        // convert to IntrospectedColumn
        int i = 0;
        int num = String.valueOf(columns.size()).length();
        for (DasColumn column : columns) {
            i += 1;
            ColumnProperty introspectedColumn = new ColumnProperty();
            introspectedColumn.setColumn(column.getName());
            introspectedColumn.setTypeHandler(typeHandler(column.getComment()));
            introspectedColumn.setOrder(String.format("%0" + num + "d", i));
            introspectedColumn.setFuzzySearch(false);
            introspectedColumnList.add(introspectedColumn);
        }


        handler.initTable(columnsTable, introspectedColumnList);
    }


    public static String typeHandler(String remarks) {
        Matcher matcher = HANDLER_PATTERN.matcher(remarks);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    protected void doOKAction() {
        saveProjectConfig();

        connectionConfig = dbDataSource.getConnectionConfig();

        Map<String, Credential> credentials = projectPersistent.getCredentials();
        Credential credential;
        if (credentials == null || !credentials.containsKey(connectionConfig.getUrl())) {
            boolean result = getDatabaseCredential(connectionConfig);
            if (result) {
                credentials = projectPersistent.getCredentials();
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
                String url = projectPersistent.getDatabaseUrl();
                CredentialAttributes credentialAttributes = new CredentialAttributes(Constant.PLUGIN_NAME + "-" + url, credential.getUsername(), this.getClass(), false);
                String password = PasswordSafe.getInstance().getPassword(credentialAttributes);
                try {
                    String databaseType = DatabaseUtils.testConnection(connectionConfig.getDriverClass(), connectionConfig.getUrl(), credential.getUsername(), password, chkMySQL8.getSelectedObjects() != null);
                    projectProperties.setType(databaseType);
                } catch (ClassNotFoundException | SQLException e) {
                    projectPersistent.setCredentials(null);
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
                    }
                }
            }
            return;
        }

        if (chkOverwrite.getSelectedObjects() != null) {
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

        MybatisCommander mybatisCommander = new MybatisCommander();

        projectProperties.setColumns(introspectedColumnList);

        mybatisCommander.generate(project, projectProperties, dbDataSource, comboModule.getSelectedModule());
    }



    private boolean getDatabaseCredential(RawConnectionConfig connectionConfig) {
        DatabaseCredentialUI databaseCredentialUI = new DatabaseCredentialUI(event.getProject(), connectionConfig.getUrl());
        return databaseCredentialUI.showAndGet();
    }

    private void saveProjectConfig() {
        projectProperties.setModulePath(ModuleUtil.getPath(comboModule.getSelectedModule(), "root"));
        projectProperties.setFacadeImpl(tFacadeImplement.getText());
        projectProperties.setFacadeInterface(tFacadeInterface.getText());
        projectProperties.setIdGenerator(tIdGenerator.getText());
        projectProperties.setIgnoreColumns(tIgnoreColumns.getText());
        projectProperties.setMapperInterface(tMapperInterface.getText());
        projectProperties.setNonFuzzySearchColumn(tNonFuzzySearchColumns.getText());
        projectProperties.setResponseObject(tResponseObject.getText());
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
        pluginProperties.setChkBeginEnd(chkBeginEnd.isSelected());
        pluginProperties.setChkDateTime(chkDateTime.isSelected());

        projectProperties.setPlugin(pluginProperties);
        projectProperties.setReplaceString(tReplaceString.getText());
        projectProperties.setRootClass(tRootObject.getText());
        projectProperties.setRootPackage(tRootPackage.getText());
        projectProperties.setSearchString(tSearchString.getText());
        projectProperties.setServiceImpl(tServiceImplement.getText());
        projectProperties.setServiceInterface(tServiceInterface.getText());

        projectPersistent.saveProjectConfig(projectProperties.getSchema(), projectProperties);
    }
}
