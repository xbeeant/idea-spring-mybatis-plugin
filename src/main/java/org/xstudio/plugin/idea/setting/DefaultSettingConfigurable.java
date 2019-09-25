package org.xstudio.plugin.idea.setting;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.ui.DefaultSettingUi;

import javax.swing.*;

/**
 * @author xiaobiao
 * @version 2019/9/23
 */
public class DefaultSettingConfigurable implements SearchableConfigurable {
    private DefaultSettingUi mainPanel;

    @Nullable
    @Override
    public String getHelpTopic() {
        return "gene.helpTopic";
    }

    @NotNull
    @Override
    public String getId() {
        return Constant.ID;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return Constant.TITLE;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mainPanel = new DefaultSettingUi();
        return mainPanel.getContentPanel();
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Override
    public boolean isModified() {
        return mainPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        mainPanel.apply();
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
    }
}
