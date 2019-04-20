package org.xstudio.plugins.idea.ui;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import org.mybatis.generator.api.IntrospectedColumn;
import org.xstudio.plugins.idea.database.DatabaseUtil;
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
    Map<String, List<JCheckBox>> checkOptions = new HashMap<>();
    Map<String, List<JRadioButton>> radioOptions = new HashMap<>();
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
    private String tablesName;
    private List<IntrospectedColumn> columns = new ArrayList<>();
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

        JList<String> wordList = new JBList<>(tableNames);
        JScrollPane scrollPane = new JBScrollPane(wordList);
        scrollPane.setPreferredSize(new Dimension(SIDE_WIDTH, PANEL_HEIGHT));
        wordList.setDragEnabled(false);
        wordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        wordList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                for (String value : wordList.getSelectedValuesList()) {
                    tablesName = value;
                }
                columns = databaseUtil.getColumns(table, tablesName);
                // 生成代码配置
                actionPanel.removeAll();
                actionPanel.add(setGeneratorConfigUI(columns, tablesName));
                actionPanel.setVisible(true);
                actionPanel.revalidate();
                actionPanel.setVisible(true);
            }
        });

        mainPanel.add(scrollPane);
        mainPanel.add(actionPanel);
        setSize(new Dimension(JFRAME_WIDTH, JFRAME_HEIGHT));
        // 居中显示
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        // show the frame
        setVisible(true);
    }

    private void setCheckOption() {
        Set<Map.Entry<String, List<JCheckBox>>> checkOptionsSet = checkOptions.entrySet();
        for (Map.Entry<String, List<JCheckBox>> checkOption : checkOptionsSet) {
            String key = checkOption.getKey();
            StringBuilder value = new StringBuilder();
            List<JCheckBox> list = checkOption.getValue();
            for (JCheckBox jCheckBox : list) {
                if (jCheckBox.isSelected()) {
                    value.append(jCheckBox.getText());
                }
            }
            generatorConfig.put(key, value);
        }

        Set<Map.Entry<String, List<JRadioButton>>> radioOptionsSet = radioOptions.entrySet();
        for (Map.Entry<String, List<JRadioButton>> radioOption : radioOptionsSet) {
            String key = radioOption.getKey();
            StringBuilder value = new StringBuilder();
            List<JRadioButton> list = radioOption.getValue();
            for (JRadioButton radioButton : list) {
                if (radioButton.isSelected()) {
                    value.append(radioButton.getText());
                }
            }
            generatorConfig.put(key, value);
        }
    }

    private JScrollPane setGeneratorConfigUI(List<IntrospectedColumn> columns, String tablesName) {
        JBPanel panel = new JBPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setVisible(true);
        panel.setPreferredSize(new Dimension(CONTENT_WIDTH - 20, columns.size() * 70));
        panel.revalidate();

        Label label;
        JTextField input;
        JPanel item;
        JPanel itemDetail;
        JCheckBox checkBox;
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
        panel.add(item);
        for (IntrospectedColumn column : columns) {
            itemDetail = new JBPanel<>(new GridLayout(0, 6, 5, 5));
            itemDetail.setPreferredSize(new Dimension(CONTENT_WIDTH - 30, 60));
            itemDetail.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
            label = new Label(column.getActualColumnName() + " 中文");
            label.setPreferredSize(new Dimension(100, 25));
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

            input = new JTextField();
            input.setHorizontalAlignment(JTextField.LEFT);
            input.setText(column.getRemarks());
            itemDetail.add(input);

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

            label = new Label("表格中可宽度");
            label.setAlignment(Label.RIGHT);
            itemDetail.add(label);

            input = new JTextField();
            input.setHorizontalAlignment(JTextField.LEFT);
            itemDetail.add(input);

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
            itemDetail.setVisible(true);
//            panels.add(itemDetail);
            panel.add(itemDetail);
        }
        JScrollPane scrollPane = new JBScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(CONTENT_WIDTH - 10, PANEL_HEIGHT - 10));
        scrollPane.setVisible(true);
//        panel.add(scrollPane);
        return scrollPane;
    }
}
