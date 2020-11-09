package org.xstudio.plugin.idea.setting;

import com.alibaba.fastjson.JSON;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.xstudio.mybatis.po.TableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xstudio.plugin.idea.model.Credential;
import org.xstudio.plugin.idea.mybatis.generator.PersistentProperties;
import org.xstudio.plugin.idea.mybatis.generator.ProjectPersistentProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaobiao
 * @version 2019/9/22
 */
@State(name = "XstudioGenerator", storages = {@Storage("xstudio-generator.xml")})
public class ProjectPersistentConfiguration implements PersistentStateComponent<ProjectPersistentConfiguration> {
    /**
     * 账号密码信息
     */
    private Map<String, Credential> credentials;

    private String databaseUrl;
    private ProjectPersistentProperties persistentConfig = new ProjectPersistentProperties();
    private Map<String, ProjectPersistentProperties> tableConfigs = new HashMap<>(1);

    @Nullable
    public static ProjectPersistentConfiguration getInstance(Project project) {
        return ServiceManager.getService(project, ProjectPersistentConfiguration.class);
    }

    public void addTableConfig(String databaseName, String schema, ProjectPersistentProperties tableConfig) {
        if (null == tableConfigs) {
            tableConfigs = new HashMap<>();
        }
        tableConfigs.put(schema, tableConfig);
    }

    public Map<String, Credential> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, Credential> credentials) {
        this.credentials = credentials;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public ProjectPersistentProperties getPersistentConfig() {
        return persistentConfig;
    }

    public void setPersistentConfig(ProjectPersistentProperties persistentConfig) {
        this.persistentConfig = persistentConfig;
    }

    @Nullable
    @Override
    public ProjectPersistentConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProjectPersistentConfiguration projectPersistentConfiguration) {
        XmlSerializerUtil.copyBean(projectPersistentConfiguration, this);
    }

    public ProjectPersistentProperties getTableConfig(String databaseName, String schema) {
        DefaultPersistentConfiguration defaultPersistentConfiguration = ServiceManager.getService(DefaultPersistentConfiguration.class);
        PersistentProperties persistentConfig = defaultPersistentConfiguration.getPersistentConfig();
        ProjectPersistentProperties tableConfig = tableConfigs.get(databaseName);
        if (null == tableConfig) {
            if (null != persistentConfig) {
                tableConfig = JSON.parseObject(JSON.toJSONString(persistentConfig), ProjectPersistentProperties.class);
            } else {
                tableConfig = new ProjectPersistentProperties();
            }
            tableConfig.setSchema(schema);
            tableConfig.setDatabase(databaseName);
            setTableGenerateTarget(tableConfig);
            return tableConfig;
        }
        return tableConfig;
    }

    public void setTableGenerateTarget(ProjectPersistentProperties tableConfig) {
        tableConfigs.put(tableConfig.getDatabase(), tableConfig);
    }

    public Map<String, ProjectPersistentProperties> getTableConfigs() {
        return tableConfigs;
    }

    public void setTableConfigs(Map<String, ProjectPersistentProperties> tableConfigs) {
        this.tableConfigs = tableConfigs;
    }

    public void saveProjectConfig(String tableName, ProjectPersistentProperties tableConfig) {
        tableConfigs.put(tableName, tableConfig);
    }
}