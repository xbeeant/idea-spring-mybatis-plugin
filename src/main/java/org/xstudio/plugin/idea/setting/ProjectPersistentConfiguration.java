package org.xstudio.plugin.idea.setting;

import com.alibaba.fastjson.JSON;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import lombok.Getter;
import lombok.Setter;
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
    @Getter
    @Setter
    private Map<String, Credential> credentials;

    @Getter
    @Setter
    private Map<String, TableConfig> tableConfigs = new HashMap<>(1);

    @Getter
    @Setter
    private PersistentConfig persistentConfig = new PersistentConfig();

    @Nullable
    public static ProjectPersistentConfiguration getInstance(Project project) {
        return ServiceManager.getService(project, ProjectPersistentConfiguration.class);
    }

    public void setTableGenerateTarget(TableConfig tableConfig, String entityName) {
        String databaseName = tableConfig.getDatabaseName();
        String databaseNamePackage = tableConfig.getDatabaseNamePackage();
        String basePackage = tableConfig.getBasePackage() + "." + databaseNamePackage;
        String resourcePath = tableConfig.getResourcePath();

        tableConfig.setModelClass(basePackage + ".model." + entityName);
        tableConfig.setServiceInterfaceClass(basePackage + ".service.I" + entityName + "Service");
        tableConfig.setServiceImplClass(basePackage + ".service.impl." + entityName + "ServiceImpl");
        tableConfig.setFacadeInterfaceClass(basePackage + ".facade.I" + entityName + "Facade");
        tableConfig.setFacadeImplClass(basePackage + ".facade.impl." + entityName + "FacadeImpl");
        tableConfig.setMapperClass(basePackage + ".mapper.I" + entityName + "Mapper");
        tableConfig.setMapperImplClass(resourcePath + "/mybatis/" + databaseName + "/" + entityName + "Mapper.xml");

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
        DefaultPersistentConfiguration persistentConfiguration = ServiceManager.getService(DefaultPersistentConfiguration.class);
        PersistentConfig persistentConfig = persistentConfiguration.getPersistentConfig();
        TableConfig tableConfig = tableConfigs.get(tableName);
        if (null == tableConfig) {
            if (null != persistentConfig){
                tableConfig = JSON.parseObject(JSON.toJSONString(persistentConfig), TableConfig.class);
            } else {
                tableConfig = new TableConfig();
            }
            tableConfig.setDatabaseName(databaseName);
            tableConfig.setTableName(tableName);
            setTableGenerateTarget(tableConfig, tableConfig.getEntityName());
            return tableConfig;
        }
        return tableConfig;
    }

    public void saveProjectConfig(String tableName, TableConfig tableConfig) {
        tableConfigs.put(tableName, tableConfig);
    }
}