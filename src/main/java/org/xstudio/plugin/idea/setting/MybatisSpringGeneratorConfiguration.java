package org.xstudio.plugin.idea.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xstudio.plugin.idea.model.TableConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaobiao
 * @version 2019/9/22
 */
@State(name = "MybatisSpringGeneratorConfiguration", storages = {@Storage("mybatis-spring-generator-config.xml")})
public class MybatisSpringGeneratorConfiguration implements PersistentStateComponent<MybatisSpringGeneratorConfiguration> {

    private Map<String, TableConfig> tableConfigs = new HashMap<>(1);

    @Nullable
    public static MybatisSpringGeneratorConfiguration getInstance(Project project) {
        return ServiceManager.getService(project, MybatisSpringGeneratorConfiguration.class);
    }

    @Nullable
    @Override
    public MybatisSpringGeneratorConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MybatisSpringGeneratorConfiguration mybatisSpringGeneratorConfiguration) {
        XmlSerializerUtil.copyBean(mybatisSpringGeneratorConfiguration, this);
    }

    public void addTableConfig(String databaseName, TableConfig tableConfig) {
        if (null == tableConfigs) {
            tableConfigs = new HashMap<>();
        }
        tableConfigs.put(tableConfig.getTableName(), tableConfig);
    }

    public TableConfig getTableConfig(String tableName) {
        TableConfig tableConfig = tableConfigs.get(tableName);
        if (null == tableConfig) {
            return new TableConfig();
        }
        return tableConfig;
    }
}
