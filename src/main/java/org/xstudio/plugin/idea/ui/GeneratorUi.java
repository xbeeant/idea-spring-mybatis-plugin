package org.xstudio.plugin.idea.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author xiaobiao
 * @version 2019/9/20
 */
public class GeneratorUi extends DialogWrapper {
    /**
     * idea 事件对象
     */
    private AnActionEvent event;
    /**
     * IDEA当前工程对象
     */
    private Project project;
    /**
     * 主面板
     */
    private JPanel contentPane = new JBPanel<>();

    public GeneratorUi(AnActionEvent e) {
        super(e.getData(PlatformDataKeys.PROJECT));

        this.project = e.getData(PlatformDataKeys.PROJECT);

        this.event = e;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
