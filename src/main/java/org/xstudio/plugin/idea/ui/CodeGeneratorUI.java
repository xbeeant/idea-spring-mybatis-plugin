package org.xstudio.plugin.idea.ui;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbNamespaceImpl;
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
import org.jetbrains.annotations.Nullable;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.model.Credential;
import org.xstudio.plugin.idea.model.PersistentConfig;
import org.xstudio.plugin.idea.model.TableConfig;
import org.xstudio.plugin.idea.model.TableInfo;
import org.xstudio.plugin.idea.mybatis.MyBatisGenerateCommand;
import org.xstudio.plugin.idea.setting.ProjectPersistentConfiguration;
import org.xstudio.plugin.idea.util.DatabaseUtils;
import org.xstudio.plugin.idea.util.ModuleUtil;

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
 * @author Beeant
 * @version 2020/6/7
 */
public class CodeGeneratorUI extends DialogWrapper {

    private JPanel mainPanel;
    private JTextField tpTableName;
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
    private JTextField ptModelEntity;
    private JTextField ptServiceInterface;
    private JTextField ptServiceImplement;
    private JTextField ptFacadeInterface;
    private JTextField ptFacadeImplement;
    private JTextField ptMapperInterface;
    private JTextField ptMapperImplement;
    private JTabbedPane tabbedPane;
    private ModulesComboBox comboModule;
    private JTextField tRootPackage;
    private JTextField tTablePrefix;

    /**
     * idea 事件对象
     */
    private AnActionEvent event;

    private Project project;
    private TableInfo tableInfo;

    private RawConnectionConfig connectionConfig;
    private DbDataSource dbDataSource;

    private ProjectPersistentConfiguration persistentConfiguration;
    private TableConfig tableConfig;

    public CodeGeneratorUI(AnActionEvent event, @Nullable Project project, TableInfo tableInfo) {
        super(project);
        this.project = project;
        this.event = event;
        this.tableInfo = tableInfo;
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

        this.persistentConfiguration = ProjectPersistentConfiguration.getInstance(project);

        String tableName = tableInfo.getTableName();

        tableConfig = persistentConfiguration.getTableConfig(tableName, databaseName);
        tableConfig.setDatabaseType(tableInfo.getTypeName());

        this.setTitle("Mybatis Spring Code Generator");
        mainPanel.setPreferredSize(new Dimension(800, 300));
        this.init();
        // initial panel defaults value
        initialPanel(tableConfig);

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

        tTablePrefix.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String prefix = tTablePrefix.getText();
                tableConfig.setTablePrefix(prefix);
                String entityName = JavaBeansUtil.getCamelCaseString(tableName.replace(prefix, ""), true);
                setPreviewPanel(entityName);
            }
        });

        String entityName = JavaBeansUtil.getCamelCaseString(tableName.replace(tableConfig.getTablePrefix(), ""), true);
        setPreviewPanel(entityName);
    }

    private void setPreviewPanel(String entityName) {
        tableConfig.setEntityName(entityName);

        ptModelEntity.setText(entityName);

        ptServiceInterface.setText(tableConfig.getRootPackage() + ".service.I" + entityName + "Service");
        ptServiceImplement.setText(tableConfig.getRootPackage() + ".service.impl." + entityName + "ServiceImpl");
        if (tableConfig.isFacadePlugin()) {
            ptFacadeInterface.setText(tableConfig.getRootPackage() + ".facade.I" + entityName + "FacadeService");
            ptFacadeImplement.setText(tableConfig.getRootPackage() + ".facade.impl." + entityName + "FacadeServiceImpl");
        }
        ptMapperInterface.setText(tableConfig.getRootPackage() + ".mapper.I" + entityName + "Mapper");
        ptMapperImplement.setText(tableConfig.getResourcePath() + "/mybatis/" + tableConfig.getDatabaseType() + "/" + tableConfig.getDatabaseName() + "/" + entityName + "Mapper.xml");
    }

    private void initialPanel(PersistentConfig config) {
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

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    protected void doOKAction() {
        saveProjectConfig();

        connectionConfig = dbDataSource.getConnectionConfig();

        Map<String, Credential> credentials = persistentConfiguration.getCredentials();
        Credential credential;
        if (credentials == null || !credentials.containsKey(connectionConfig.getUrl())) {
            boolean result = getDatabaseCredential(connectionConfig);
            if (result) {
                credentials = persistentConfiguration.getCredentials();
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
                String url = persistentConfiguration.getDatabaseUrl();
                CredentialAttributes credentialAttributes = new CredentialAttributes(Constant.PLUGIN_NAME + "-" + url, credential.getUsername(), this.getClass(), false);
                String password = PasswordSafe.getInstance().getPassword(credentialAttributes);
                try {
                    String databaseType = DatabaseUtils.testConnection(connectionConfig.getDriverClass(), connectionConfig.getUrl(), credential.getUsername(), password, chkMySQL8.getSelectedObjects() != null);
                    tableConfig.setDatabaseType(databaseType);
                } catch (ClassNotFoundException | SQLException e) {
                    persistentConfiguration.setCredentials(null);
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

        new MyBatisGenerateCommand(tableConfig).execute(project, comboModule.getSelectedModule(), connectionConfig);
    }

    private void saveProjectConfig() {
        tableConfig.setModuleRootPath(ModuleUtil.getPath(comboModule.getSelectedModule(), "root"));
        tableConfig.setTablePrefix(tTablePrefix.getText());
        tableConfig.setIdGenerator(tIdGenerator.getText());
        tableConfig.setServiceInterface(tServiceInterface.getText());
        tableConfig.setServiceImplement(tServiceImplement.getText());
        tableConfig.setFacadeInterface(tFacadeInterface.getText());
        tableConfig.setFacadeImplement(tFacadeImplement.getText());
        tableConfig.setDaoInterface(tMapperInterface.getText());
        tableConfig.setBaseObject(tRootObject.getText());
        tableConfig.setIgnoreColumn(tIgnoreColumns.getText());
        tableConfig.setNonFuzzyColumn(tNonFuzzySearchColumns.getText());

        tableConfig.setComment(chkComment.getSelectedObjects() != null);
        tableConfig.setOverride(chkOverwrite.getSelectedObjects() != null);
        tableConfig.setUseSchemaPrefix(chkUseSchemaPrefix.getSelectedObjects() != null);
        tableConfig.setToStringHashcodeEquals(chkToString.getSelectedObjects() != null);
        tableConfig.setUseTableNameAlias(chkUseAlias.getSelectedObjects() != null);
        tableConfig.setMysql8(chkMySQL8.getSelectedObjects() != null);
        tableConfig.setLombokPlugin(chkLombok.getSelectedObjects() != null);
        tableConfig.setSwagger2Plugin(chkSwaggerModel.getSelectedObjects() != null);
        tableConfig.setFacadePlugin(chkGenerateFacade.getSelectedObjects() != null);
        tableConfig.setFastjsonPlugin(chkFastJson.getSelectedObjects() != null);

        persistentConfiguration.saveProjectConfig(tableConfig.getTableName(), tableConfig);
    }

    private boolean getDatabaseCredential(RawConnectionConfig connectionConfig) {
        DatabaseCredentialUI databaseCredentialUI = new DatabaseCredentialUI(event.getProject(), connectionConfig.getUrl());
        return databaseCredentialUI.showAndGet();
    }
}
