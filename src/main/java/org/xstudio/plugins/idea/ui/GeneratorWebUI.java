package org.xstudio.plugins.idea.ui;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
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

    private Map<String, Map<String, Object>> webProperties = new HashMap<>();
    /**
     * 配置面板
     * 创建面板，这个类似于 HTML 的 div 标签
     * 我们可以创建多个面板并在 JFrame 中指定位置
     * 面板中我们可以添加文本字段，按钮及其他组件。
     */
    private JPanel mainPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));

    public GeneratorWebUI(AnActionEvent event) {
        this.actionEvent = event;
        this.project = event.getProject();
        this.setUI();
    }

    private void setUI() {
        GeneratorWebUI generatorWebUI = this;

        String projectFolder = project.getBasePath();
        String configPath = projectFolder + File.separator;

        generatorConfig = FileUtil.getConfig(configPath);

        setTitle("Spring Mybatis Code Generator");
        // 设置主界面
        Container contentPane = getContentPane();
        // 添加面板
        contentPane.add(mainPanel);

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
        JBPanel actionPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setPreferredSize(new Dimension(CONTENT_WIDTH - 10, PANEL_HEIGHT));

        JPanel buttonPanel = new JBPanel<>();
        buttonPanel.setPreferredSize(new Dimension(CONTENT_WIDTH - 10, 30));

        JBPanel contentPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));
        contentPanel.setPreferredSize(new Dimension(CONTENT_WIDTH - 10, PANEL_HEIGHT - 30));
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
        buttonPanel.add(buttonConfirm);

        actionPanel.add(buttonPanel);
        actionPanel.add(contentPanel);

        JList<String> wordList = new JBList<>(tableNames);
        JScrollPane scrollPane = new JBScrollPane(wordList);
        scrollPane.setPreferredSize(new Dimension(SIDE_WIDTH, PANEL_HEIGHT));
        wordList.setDragEnabled(false);
        wordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        wordList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                for (String value : wordList.getSelectedValuesList()) {
                    tableName = value;
                }
                columns = databaseUtil.getColumns(table, tableName);
                // 生成代码配置
                contentPanel.removeAll();
                contentPanel.add(setGeneratorConfigUI(columns, tableName));
                contentPanel.setVisible(true);
                contentPanel.revalidate();
                contentPanel.setVisible(true);
            }
        });

        mainPanel.add(scrollPane);
        mainPanel.add(actionPanel);
        setSize(new Dimension(JFRAME_WIDTH, JFRAME_HEIGHT));
        // 居中显示
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
//        setResizable(false);
        // show the frame
        setVisible(true);
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
        JRadioButton radioButton;
        JPanel buttonPanel;

        item = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));
        item.setPreferredSize(new Dimension(CONTENT_WIDTH - 70, 25));
        label = new Label("namespace");
        label.setAlignment(Label.RIGHT);
        item.add(label);

        input = new JTextField();
        input.setHorizontalAlignment(JTextField.LEFT);
        input.setText(tablesName);
        input.setPreferredSize(new Dimension(CONTENT_WIDTH - 200, 25));
        item.add(input);
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
            label.setPreferredSize(new Dimension(100, 25));
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

            input = new JTextField();
            input.setHorizontalAlignment(JTextField.LEFT);
            input.setText(column.getRemarks());
            itemDetail.add(input);
            columnMap.put("remarks", input);

            label = new Label("表格中显示");
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

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
            columnMap.put("showInTable", buttonGroup);

            label = new Label("表格中宽度");
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

            input = new JTextField();
            input.setHorizontalAlignment(JTextField.LEFT);
            itemDetail.add(input);
            columnMap.put("tableInWidth", buttonGroup);

            label = new Label("表格中可排序");
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

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
            columnMap.put("tableSorter", buttonGroup);

            label = new Label("简单搜索");
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

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
            columnMap.put("simpleSearch", buttonGroup);

            label = new Label("展开搜索");
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

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
            columnMap.put("advanceSearch", buttonGroup);

            label = new Label("可编辑");
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

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
            columnMap.put("editable", buttonGroup);

            label = new Label("编辑时校验唯一性");
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

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
            columnMap.put("uniqueValidate", buttonGroup);

            itemDetail.setVisible(true);
            panel.add(itemDetail);
            webProperties.put(column.getActualColumnName(), columnMap);
        }
        JScrollPane scrollPane = new JBScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(CONTENT_WIDTH - 15, PANEL_HEIGHT - 10));
        scrollPane.setVisible(true);
        panel.revalidate();
        return scrollPane;
    }
}
