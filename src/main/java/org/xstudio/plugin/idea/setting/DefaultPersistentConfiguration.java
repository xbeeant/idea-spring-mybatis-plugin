package org.xstudio.plugin.idea.setting;

import com.alibaba.fastjson.JSON;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xstudio.plugin.idea.mybatis.generator.PersistentProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaobiao
 * @version 2019/9/22
 */
@State(name = "XstudioGenerator", storages = {@Storage("xstudio-generator.xml")})
public class DefaultPersistentConfiguration implements PersistentStateComponent<DefaultPersistentConfiguration> {

    private PersistentProperties persistentConfig = new PersistentProperties();

    public PersistentProperties getPersistentConfig() {
        return persistentConfig;
    }

    private Map<String, PersistentProperties> configs = new HashMap<>();

    public Map<String, PersistentProperties> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, PersistentProperties> configs) {
        this.configs = configs;
    }

    @Nullable
    public static DefaultPersistentConfiguration getInstance() {
        return ServiceManager.getService(DefaultPersistentConfiguration.class);
    }

    public void setPersistentConfig(PersistentProperties persistentConfig) {
        PersistentProperties newConfig = JSON.parseObject(JSON.toJSONString(persistentConfig), PersistentProperties.class);
        this.persistentConfig = persistentConfig;
        String name = "default";
        if (null != persistentConfig.getCfgName() && !"".equals(persistentConfig.getCfgName())) {
            name = persistentConfig.getCfgName();
        }
        configs.put(name, newConfig);
    }

    @Nullable
    @Override
    public DefaultPersistentConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DefaultPersistentConfiguration mybatisSpringGeneratorConfiguration) {
        XmlSerializerUtil.copyBean(mybatisSpringGeneratorConfiguration, this);
    }
}