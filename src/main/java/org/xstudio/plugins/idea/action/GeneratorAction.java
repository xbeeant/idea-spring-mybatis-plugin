package org.xstudio.plugins.idea.action;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.xstudio.plugins.idea.generator.FileUtil;
import org.xstudio.plugins.idea.ui.GeneratorUI;
import org.xstudio.plugins.idea.ui.ProjectConfigUI;

import java.io.File;

/**
 * @author xiaobiao
 * @version 2019/3/19
 */
public class GeneratorAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        String projectFolder = project.getBasePath();
        String configPath = projectFolder + File.separator;
        JSONObject generatorConfig = FileUtil.getConfig(configPath);
        if (null == generatorConfig || generatorConfig.size() == 0) {
            Messages.showMessageDialog("请先配置生成器", "提醒", Messages.getErrorIcon());
            new ProjectConfigUI(event);
        } else {
            new GeneratorUI(event);
        }

    }
}
