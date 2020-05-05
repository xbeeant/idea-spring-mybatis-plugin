package org.xstudio.plugin.idea.ui;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.model.Credential;
import org.xstudio.plugin.idea.setting.ProjectPersistentConfiguration;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 账号密码输入界面
 * Created by kangtian on 2018/8/3.
 */
public class DatabaseCredentialUI extends DialogWrapper {

    private ProjectPersistentConfiguration projectPersistentConfiguration;

    private Project project;
    private JPanel contentPanel = new JBPanel<>();

    private JTextField urlField = new JBTextField(30);
    private JTextField usernameField = new JBTextField(30);
    private JTextField passwordField = new JBPasswordField();
    private JLabel errorMessage = new JLabel("");


    public DatabaseCredentialUI(Project project, String url) throws HeadlessException {
        super(project);
        this.project = project;
        this.projectPersistentConfiguration = ProjectPersistentConfiguration.getInstance(project);
        setTitle("Connect to Database");
        pack();

        contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        Map<String, Credential> credentials = projectPersistentConfiguration.getCredentials();

        // ========== database url
        JPanel urlPanel = new JBPanel<>();
        urlPanel.setLayout(new BoxLayout(urlPanel, BoxLayout.X_AXIS));
        urlPanel.setBorder(JBUI.Borders.empty(1));
        JLabel urlLabel = new JLabel("URL:");
        urlLabel.setPreferredSize(new Dimension(80, 20));
        urlPanel.add(urlLabel);
        urlPanel.add(urlField);
        if (url != null) {
            urlField.setText(url);
        }
        contentPanel.add(urlPanel);
        // ========== username
        JPanel usernamePanel = new JBPanel<>();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
        usernamePanel.setBorder(JBUI.Borders.empty(1));
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setPreferredSize(new Dimension(80, 20));
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        if (credentials != null && credentials.containsKey(url)) {
            usernameField.setText(credentials.get(url).getUsername());
        }
        contentPanel.add(usernamePanel);
        // ========== password
        JPanel passwordPanel = new JBPanel<>();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
        passwordPanel.setBorder(JBUI.Borders.empty(1));
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setPreferredSize(new Dimension(80, 20));
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        contentPanel.add(passwordPanel);
        contentPanel.add(errorMessage);
        errorMessage.setForeground(JBColor.RED);
        this.init();
    }

    @Override
    protected void doOKAction() {

        if (StringUtils.isEmpty(usernameField.getText())) {
            errorMessage.setText("Username must not be null");
            return;
        }

        Map<String, Credential> credentials = projectPersistentConfiguration.getCredentials();
        if (credentials == null) {
            credentials = new HashMap<>();
        }
        String dbUrl = urlField.getText();
        credentials.put(dbUrl, new Credential(usernameField.getText()));

        CredentialAttributes attributes = new CredentialAttributes(Constant.PLUGIN_NAME + "-" + dbUrl, usernameField.getText(), this.getClass(), false);
        Credentials saveCredentials = new Credentials(attributes.getUserName(), passwordField.getText());
        PasswordSafe.getInstance().set(attributes, saveCredentials);
        projectPersistentConfiguration.setCredentials(credentials);
        projectPersistentConfiguration.setDatabaseUrl(dbUrl);
        Objects.requireNonNull(project.getProjectFile()).refresh(true, true);

        super.doOKAction();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return this.contentPanel;
    }
}
