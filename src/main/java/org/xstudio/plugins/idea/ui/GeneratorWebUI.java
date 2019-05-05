package org.xstudio.plugins.idea.ui;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.IntrospectedColumn;
import org.xstudio.plugins.idea.database.DatabaseUtil;
import org.xstudio.plugins.idea.generator.CodeGenerator;
import org.xstudio.plugins.idea.generator.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;

/**
 * @author xiaobiao
 * @version 2019/3/20
 */
public class GeneratorWebUI extends JFrame {
    /**
     * 事件
     */
    private AnActionEvent actionEvent;
    /**
     * 当前项目
     */
    private Project project;
    private JSONObject generatorConfig;
    private Integer JFRAME_WIDTH = 1024;
    private Integer JFRAME_HEIGHT = 600;
    private Integer PANEL_HEIGHT = JFRAME_HEIGHT - 50;
    private Integer SIDE_WIDTH = 200;
    private Integer CONTENT_WIDTH = JFRAME_WIDTH - SIDE_WIDTH - 30;
    private String tableName;
    private List<IntrospectedColumn> columns = new ArrayList<>();

    private JSplitPane sp;

    private JBPanel leftPanel = new JBPanel(new BorderLayout());

    private JBPanel rightPanel = new JBPanel(new BorderLayout());

    private JBPanel topPanel = new JBPanel();

    private JBPanel configPanel = new JBPanel();

    private Map<String, Map<String, Object>> webProperties = new HashMap<>();

    public GeneratorWebUI(AnActionEvent event) {
        this.actionEvent = event;
        this.project = event.getProject();
        this.setUI();
    }

    private void setUI() {
        String projectFolder = project.getBasePath();
        String configPath = projectFolder + File.separator;

        generatorConfig = FileUtil.getConfig(configPath);

        setTitle("ant design 代码生成");

        setLeftPanel();
        setRightPanel(projectFolder);

        sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftPanel, rightPanel);

        //让分隔线显示出箭头
        sp.setOneTouchExpandable(true);
        //操作箭头，重绘图形
        sp.setContinuousLayout(true);
        sp.setLeftComponent(leftPanel);
        //设置分割线的宽度
        sp.setDividerSize(1);
        sp.setRightComponent(rightPanel);
        //  禁止拖动
        sp.setEnabled(false);
        sp.setVisible(true);

        setSize(new Dimension(JFRAME_WIDTH, JFRAME_HEIGHT));
        setContentPane(sp);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void setLeftPanel() {
        String table = generatorConfig.getString("table");
        String url = "jdbc:mysql://" + generatorConfig.getString("server")
                + ":" + generatorConfig.getString("port") + "/"
                + table + "?serverTimezone=UTC";

        DatabaseUtil databaseUtil = new DatabaseUtil(url,
                generatorConfig.getString("username"),
                generatorConfig.getString("password"),
                generatorConfig.getString("driverClass"));

        List<String> tableNames;
        try {
            tableNames = databaseUtil.getTableNames();
        } catch (Exception e) {
            tableNames = new ArrayList<>();
        }
        JList<String> wordList = new JBList<>(tableNames);
        wordList.setDragEnabled(false);
        wordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        wordList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                for (String value : wordList.getSelectedValuesList()) {
                    tableName = value;
                }
                columns = databaseUtil.getColumns(table, tableName);
                // 生成代码配置
                configPanel.removeAll();
                configPanel.add(setGeneratorConfigUI(columns, tableName));
                configPanel.setVisible(true);
                configPanel.revalidate();
                configPanel.setVisible(true);
            }
        });

        JScrollPane scrollPane = new JBScrollPane(wordList);
        leftPanel.add(scrollPane);

        leftPanel.setPreferredSize(new Dimension(SIDE_WIDTH, PANEL_HEIGHT));
    }

    private void setRightPanel(String projectFolder) {

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, topPanel, configPanel);

        // 保存按钮
        JButton buttonConfirm = new JButton("生成代码");
        buttonConfirm.setPreferredSize(new Dimension(100, 26));
        buttonConfirm.addActionListener(actionEvent -> {
            boolean error = false;
            List<WebProperty> webPropertyList = setCheckOption();
            try {
                generatorConfig.put("tableName", tableName);
                generatorConfig.put("web", webPropertyList);
                CodeGenerator.generate(projectFolder, generatorConfig, project);
            } catch (Exception e) {
                error = true;
                Messages.showMessageDialog("表" + tableName + "代码生成异常" + e.getMessage(), "错误", Messages.getErrorIcon());
            }

            if (!error) {
                Messages.showMessageDialog("代码生成完成", "提醒", Messages.getInformationIcon());
            }
        });
        topPanel.add(buttonConfirm);

        // 配置面板


        // 总体设置
        topPanel.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
        configPanel.setPreferredSize(new Dimension(CONTENT_WIDTH, PANEL_HEIGHT - 30));


        // 让分隔线显示出箭头
        splitPane.setOneTouchExpandable(true);
        //操作箭头，重绘图形
        splitPane.setContinuousLayout(true);
        //设置分割线的宽度
        splitPane.setDividerSize(1);
        //  禁止拖动
        splitPane.setEnabled(false);
        rightPanel.setPreferredSize(new Dimension(CONTENT_WIDTH, PANEL_HEIGHT));
        rightPanel.add(splitPane);
    }

    private List<WebProperty> setCheckOption() {
        List<WebProperty> webPropertyList = new ArrayList<>();
        Set<Map.Entry<String, Map<String, Object>>> webPropertiesSet = webProperties.entrySet();
        for (Map.Entry<String, Map<String, Object>> stringMapEntry : webPropertiesSet) {
            Set<Map.Entry<String, Object>> properties = stringMapEntry.getValue().entrySet();
            WebProperty webProperty = new WebProperty();
            webProperty.setField(stringMapEntry.getKey());
            for (Map.Entry<String, Object> property : properties) {
                Object value = property.getValue();
                if (value instanceof ButtonGroup) {
                    Enumeration<AbstractButton> elements = ((ButtonGroup) value).getElements();
                    int buttonCount = ((ButtonGroup) value).getButtonCount();
                    for (int i = 0; i < buttonCount; i++) {
                        AbstractButton abstractButton = elements.nextElement();
                        if (abstractButton.isSelected()) {
                            if ("是".equalsIgnoreCase(abstractButton.getText())) {
                                webProperty.addProperty(property.getKey(), true);
                            } else {
                                webProperty.addProperty(property.getKey(), false);
                            }
                            break;
                        }
                    }
                } else if (value instanceof JTextField) {
                    webProperty.addProperty(property.getKey(), ((JTextField) value).getText());
                }
            }
            webPropertyList.add(webProperty);
        }

        return webPropertyList;
    }

    private JScrollPane setGeneratorConfigUI(List<IntrospectedColumn> columns, String tablesName) {
        JBPanel panel = new JBPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setVisible(true);
        panel.setPreferredSize(new Dimension(CONTENT_WIDTH - 30, columns.size() * 85));

        Label label;
        JTextField input;
        JPanel item;
        JPanel itemDetail;
        ButtonGroup buttonGroup;

        item = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));
        item.setPreferredSize(new Dimension(CONTENT_WIDTH - 70, 25));
        label = new Label("namespace");
        label.setAlignment(Label.RIGHT);
        item.add(label);

        input = new JTextField();
        input.setHorizontalAlignment(JTextField.LEFT);
        input.setText(tablesName);
        input.setPreferredSize(new Dimension(CONTENT_WIDTH - 200, 25));
        item.add(input, 1);
        Map<String, Object> namespace = new HashMap<>();
        namespace.put("namespace", input);
        webProperties.put("namespace", namespace);

        panel.add(item);
        Map<String, Object> columnMap;
        for (IntrospectedColumn column : columns) {
            columnMap = new HashMap<>();
            itemDetail = new JBPanel<>(new GridLayout(0, 6, 5, 5));
            itemDetail.setPreferredSize(new Dimension(CONTENT_WIDTH - 30, 75));
            itemDetail.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
            label = new Label(column.getActualColumnName() + " 中文");
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

            input = new JTextField();
            input.setHorizontalAlignment(JTextField.LEFT);
            input.setText(column.getRemarks());
            itemDetail.add(input);
            columnMap.put("remarks", input);

            buttonGroup = getButtonGroup(itemDetail, "表格中显示");
            columnMap.put("showInTable", buttonGroup);

            label = new Label("表格中宽度");
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

            input = new JTextField();
            input.setHorizontalAlignment(JTextField.LEFT);
            itemDetail.add(input);
            columnMap.put("tableInWidth", buttonGroup);

            buttonGroup = getButtonGroup(itemDetail, "表格中可排序");
            columnMap.put("tableSorter", buttonGroup);

            buttonGroup = getButtonGroup(itemDetail, "简单搜索");
            columnMap.put("simpleSearch", buttonGroup);

            buttonGroup = getButtonGroup(itemDetail, "展开搜索");
            columnMap.put("advanceSearch", buttonGroup);

            buttonGroup = getButtonGroup(itemDetail, "可编辑");
            columnMap.put("editable", buttonGroup);

            buttonGroup = getButtonGroup(itemDetail, "编辑时校验唯一性");
            columnMap.put("uniqueValidate", buttonGroup);

            itemDetail.setVisible(true);
            panel.add(itemDetail);
            webProperties.put(column.getActualColumnName(), columnMap);
        }
        JScrollPane scrollPane = new JBScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(CONTENT_WIDTH - 15, PANEL_HEIGHT - 40));
        scrollPane.setVisible(true);
        panel.revalidate();
        return scrollPane;
    }

    @NotNull
    private ButtonGroup getButtonGroup(JPanel itemDetail, String text) {
        Label label = new Label(text);
        label.setAlignment(Label.RIGHT);
        itemDetail.add(label);

        ButtonGroup buttonGroup;
        JPanel buttonPanel;
        JRadioButton radioButton;
        buttonGroup = new ButtonGroup();
        buttonPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));
        radioButton = new JRadioButton("是");
        radioButton.setHorizontalAlignment(JTextField.LEFT);
        buttonGroup.add(radioButton);
        buttonPanel.add(radioButton);

        radioButton = new JRadioButton("否");
        radioButton.setHorizontalAlignment(JTextField.LEFT);
        radioButton.setSelected(true);
        buttonGroup.add(radioButton);
        buttonPanel.add(radioButton);
        itemDetail.add(buttonPanel);
        return buttonGroup;
    }
}
