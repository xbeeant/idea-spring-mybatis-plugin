package org.xstudio.plugin.idea.setting;

import com.alibaba.fastjson.JSON;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xstudio.plugin.idea.model.Credential;
import org.xstudio.plugin.idea.model.PersistentConfig;
import org.xstudio.plugin.idea.model.TableConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaobiao
 * @version 2019/9/22
 */
@State(name = "MybatisSpringGeneratorConfiguration", storages = {@Storage("mybatis-spring-generator-project-config.xml")})
public class ProjectPersistentConfiguration implements PersistentStateComponent<ProjectPersistentConfiguration> {
    /**
     * 账号密码信息
     */
    private Map<String, Credential> credentials;

    private String databaseUrl;

    private Map<String, TableConfig> tableConfigs = new HashMap<>(1);

    private PersistentConfig persistentConfig = new PersistentConfig();

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

    public Map<String, TableConfig> getTableConfigs() {
        return tableConfigs;
    }

    public void setTableConfigs(Map<String, TableConfig> tableConfigs) {
        this.tableConfigs = tableConfigs;
    }

    public PersistentConfig getPersistentConfig() {
        return persistentConfig;
    }

    public void setPersistentConfig(PersistentConfig persistentConfig) {
        this.persistentConfig = persistentConfig;
    }

    @Nullable
    public static ProjectPersistentConfiguration getInstance(Project project) {
        return ServiceManager.getService(project, ProjectPersistentConfiguration.class);
    }

    public void setTableGenerateTarget(TableConfig tableConfig) {
        tableConfigs.put(tableConfig.getTableName(), tableConfig);
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

    public void addTableConfig(String databaseName, TableConfig tableConfig) {
        if (null == tableConfigs) {
            tableConfigs = new HashMap<>();
        }
        tableConfigs.put(tableConfig.getTableName(), tableConfig);
    }

    public TableConfig getTableConfig(String tableName, String databaseName) {
        DefaultPersistentConfiguration defaultPersistentConfiguration = ServiceManager.getService(DefaultPersistentConfiguration.class);
        PersistentConfig persistentConfig = defaultPersistentConfiguration.getPersistentConfig();
        TableConfig tableConfig = tableConfigs.get(tableName);
        if (null == tableConfig) {
            if (null != persistentConfig){
                tableConfig = JSON.parseObject(JSON.toJSONString(persistentConfig), TableConfig.class);
            } else {
                tableConfig = new TableConfig();
            }
            tableConfig.setDatabaseName(databaseName);
            tableConfig.setTableName(tableName);
            setTableGenerateTarget(tableConfig);
            return tableConfig;
        }
        return tableConfig;
    }

    public void saveProjectConfig(String tableName, TableConfig tableConfig) {
        tableConfigs.put(tableName, tableConfig);
    }
}