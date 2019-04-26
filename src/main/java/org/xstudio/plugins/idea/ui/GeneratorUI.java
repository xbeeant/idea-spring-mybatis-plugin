package org.xstudio.plugins.idea.ui;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import org.xstudio.plugins.idea.database.DatabaseUtil;
import org.xstudio.plugins.idea.generator.CodeGenerator;
import org.xstudio.plugins.idea.generator.EnConfigField;
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
public class GeneratorUI extends JFrame {
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
    private Integer PANEL_WIDTH = 900;
    private List<String> tables = new ArrayList<>();
    /**
     * 配置面板
     * 创建面板，这个类似于 HTML 的 div 标签
     * 我们可以创建多个面板并在 JFrame 中指定位置
     * 面板中我们可以添加文本字段，按钮及其他组件。
     */
    private JPanel mainPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));

    public GeneratorUI(AnActionEvent event) {
        this.actionEvent = event;
        this.project = event.getProject();
        this.setUI();
    }

    private void setUI() {
        GeneratorUI ui = this;

        String projectFolder = project.getBasePath();
        String configPath = projectFolder + File.separator;

        generatorConfig = FileUtil.getConfig(configPath);

        setTitle("SpringBoot Mybatis Code Generator");
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

        JList<String> wordList = new JBList<>(tableNames);
        JScrollPane scrollPane = new JBScrollPane(wordList);
        scrollPane.setPreferredSize(new Dimension(300, 450));
        wordList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                tables = new ArrayList<>();
                for (String value : wordList.getSelectedValuesList()) {
                    tables.add(value);
                }
            }
        });

        mainPanel.add(scrollPane);
        JBPanel actionPanel = new JBPanel<>(new GridLayout(0, 1, 5, 5));
        // 生成代码配置
        actionPanel.add(setGeneratorConfigUI());

        // 保存按钮
        JButton buttonConfirm = new JButton("生成代码");
        buttonConfirm.setPreferredSize(new Dimension(100, 26));
        buttonConfirm.addActionListener(actionEvent -> {
            boolean error = false;

            setCheckOption();

            for (String tableName : tables) {
                try {
                    generatorConfig.put("tableName", tableName);
                    CodeGenerator.generate(generatorConfig.getString("projectPath"), generatorConfig, project);
                } catch (Exception e) {
                    error = true;
                    Messages.showMessageDialog("表" + tableName + "代码生成异常" + e.getMessage(), "错误", Messages.getErrorIcon());
                }
            }

            if (!error) {
                Messages.showMessageDialog("代码生成完成", "提醒", Messages.getInformationIcon());
            }
        });
        JPanel buttonPanel = new JBPanel<>();
        buttonPanel.add(buttonConfirm);
        actionPanel.add(buttonPanel);

        mainPanel.add(actionPanel);

        setSize(new Dimension(PANEL_WIDTH, 500));
        // 居中显示
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

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

    private JPanel setGeneratorConfigUI() {
        JPanel panel = new JBPanel<>(new GridLayout(0, 1));
        ArrayList<RadioItem> radioItems = new ArrayList<>();

        List<String> lambox = new ArrayList<>();
        lambox.add("是");
        lambox.add("否");
        radioItems.add(new RadioItem("lombok",
                EnConfigField.LOMBOK.getCode(),
                lambox,
                "是"));

        List<String> facadeCheckBox = new ArrayList<>();
        facadeCheckBox.add("是");
        facadeCheckBox.add("否");
        radioItems.add(new RadioItem("生成Facade",
                EnConfigField.GEN_FACADE.getCode(),
                facadeCheckBox,
                "是"));

        List<String> tablePrefix = new ArrayList<>();
        tablePrefix.add("是");
        tablePrefix.add("否");
        radioItems.add(new RadioItem("表前缀插件",
                EnConfigField.TABLE_PREFIX.getCode(),
                tablePrefix,
                "是"));

        List<String> swagger = new ArrayList<>();
        swagger.add("是");
        swagger.add("否");
        radioItems.add(new RadioItem("Swagger2插件",
                EnConfigField.SWAGGER.getCode(),
                swagger,
                "是"));

        List<String> aliasDelete = new ArrayList<>();
        aliasDelete.add("是");
        aliasDelete.add("否");
        radioItems.add(new RadioItem("标记删除插件",
                EnConfigField.ALIAS_DELETE.getCode(),
                aliasDelete,
                "是"));

        List<String> root = new ArrayList<>();
        root.add("是");
        root.add("否");
        radioItems.add(new RadioItem("基础对象服务插件",
                EnConfigField.MODEL_ROOT.getCode(),
                root,
                "是"));

        List<String> fastJson = new ArrayList<>();
        fastJson.add("是");
        fastJson.add("否");
        radioItems.add(new RadioItem("FastJson插件",
                EnConfigField.FIELD_JSON.getCode(),
                fastJson,
                "是"));

        radioOptions.putAll(UiUtil.addRadioItems(panel,
                radioItems,
                50));
//        panel.setPreferredSize(new Dimension(radioItems.size() * 200, radioItems.size() * 50));
        panel.setSize(radioItems.size() * 200, radioItems.size() * 50);
        return panel;
    }
}
