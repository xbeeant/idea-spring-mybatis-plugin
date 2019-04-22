package org.xstudio.plugins.idea.ui;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import org.xstudio.plugins.idea.generator.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaobiao
 * @version 2019/3/19
 */
public class ProjectConfigUI extends JFrame {
    /**
     * 事件
     */
    private AnActionEvent actionEvent;

    /**
     * 当前项目
     */
    private Project project;

    private Map<String, JTextField> inputs = new HashMap<>();

    private JSONObject generatorConfig;

    private Integer PANEL_WIDTH = 900;

    /**
     * 配置面板
     * 创建面板，这个类似于 HTML 的 div 标签
     * 我们可以创建多个面板并在 JFrame 中指定位置
     * 面板中我们可以添加文本字段，按钮及其他组件。
     */
    private JPanel mainPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));


    public ProjectConfigUI(AnActionEvent event) {
        this.actionEvent = event;
        this.project = event.getProject();
        this.setUI();
    }

    public void setUI() {
        ProjectConfigUI projectConfigUI = this;

        String projectFolder = project.getBasePath();
        String configPath = projectFolder + File.separator;

        generatorConfig = FileUtil.getConfig(configPath);

        setTitle("SpringBoot Mybatis Code Generator");
        // 设置主界面
        Container contentPane = getContentPane();
        // 添加面板
        contentPane.add(mainPanel);

        mainPanel.add(projectConfigUI.setDatabaseUI());
        mainPanel.add(projectConfigUI.setConfigUI());

        // 保存按钮
        JButton buttonConfirm = new JButton("保存 & 关闭");
        buttonConfirm.setPreferredSize(new Dimension(100, 26));
        buttonConfirm.addActionListener(actionEvent -> {
            FileUtil.saveConfig(configPath, inputs);
            projectConfigUI.dispose();
            new GeneratorUI(this.actionEvent);
        });
        JPanel buttonPanel = new JBPanel<>();
        buttonPanel.add(buttonConfirm);
        mainPanel.add(buttonPanel);

        setSize(new Dimension(PANEL_WIDTH, 500));
        // 居中显示
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // show the frame
        setVisible(true);
        setResizable(false);
    }

    private JPanel setDatabaseUI() {
        JPanel panel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));
        panel.setPreferredSize(new Dimension(PANEL_WIDTH, 100));
        ArrayList<InputItem> labels = new ArrayList<>();
        labels.add(new InputItem("服务器：", "server"));
        labels.add(new InputItem("端口：", "port"));
        labels.add(new InputItem("账号：", "username"));
        labels.add(new InputItem("密码：", "password"));
        labels.add(new InputItem("数据库：", "table"));
        labels.add(new InputItem("SID：", "sid"));
        labels.add(new InputItem("表前缀：", "prefix"));
        labels.add(new InputItem("驱动：", "driverClass"));

        inputs.putAll(UiUtil.addLabelInput(panel, labels, 180, generatorConfig));

        return panel;
    }

    private JPanel setConfigUI() {
        JPanel panel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));
        panel.setPreferredSize(new Dimension(PANEL_WIDTH, 300));
        ArrayList<InputItem> labels = new ArrayList<>();
        labels.add(new InputItem("基础包：", "pakage"));
        labels.add(new InputItem("ID生成器：", "idGenerator"));
        labels.add(new InputItem("IService：", "serviceRootObject"));
        labels.add(new InputItem("ServiceImpl：", "serviceImplRootObject"));
        labels.add(new InputItem("IFacade：", "facadeServiceRootObject"));
        labels.add(new InputItem("FacadeImpl：", "facadeServiceImplRootObject"));
        labels.add(new InputItem("IDao：", "clientRootObject"));
        labels.add(new InputItem("基础对象：", "modelRootObject"));
        labels.add(new InputItem("忽略字段：", "ignoreColumns"));
        labels.add(new InputItem("不模糊搜索字段：", "nonFuzzyColumns"));

        inputs.putAll(UiUtil.addLabelInput(panel, labels, 700, generatorConfig));

        return panel;
    }
}
