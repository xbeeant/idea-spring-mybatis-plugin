package org.xstudio.plugins.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.xstudio.plugins.idea.ui.ProjectConfigUI;

/**
 * 主Action
 *
 * @author beeant
 */
public class MainAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        new ProjectConfigUI(event);
    }
}
