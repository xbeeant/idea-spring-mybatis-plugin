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

    private MybatisSpringGeneratorConfiguration mybatisSpringGeneratorConfiguration;

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
        TableConfig tableConfig = mybatisSpringGeneratorConfiguration.getTableConfig(tableName);

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

        // 显示配置界面UI

        MyBatisGenerateCommand myBatisGenerateCommand = new MyBatisGenerateCommand(tableConfig);
        // 执行代码生成
        RawConnectionConfig connectionConfig = dbDataSource.getConnectionConfig();
//        myBatisGenerateCommand.execute(project, connectionConfig);
    }

    private void initTabPanel(TableConfig tableConfig) {

        // 代码生成路径、对象等基础配置

        // 对象
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

        // 面板
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        generalPanel.add(new TitledSeparator("Domain"));

        JPanel domainPanel = new JPanel();
        domainPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        domainPanel.add(domainNamePanel);
        domainPanel.add(mapperNamePanel);

        generalPanel.add(domainPanel);

        // 包
        generalPanel.add(new TitledSeparator("Package"));
        JPanel packagePanel = new JPanel();
        packagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));


        generalPanel.add(packagePanel);
        generalPanel.setName("General");

        tabPanel.add(generalPanel);
        contentPane.add(tabPanel);
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

        String entityName = JavaBeansUtil.getCamelCaseString(tableName, true);
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
                tableConfig.setPrefix(prefix);
            }
        });
        tablePrefixField.setText(tableConfig.getPrefix());
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
