package org.xstudio.plugin.idea.setting;

import com.alibaba.fastjson.JSON;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.BeanMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xstudio.plugin.idea.model.PersistentConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaobiao
 * @version 2019/9/22
 */
@State(name = "MybatisSpringGeneratorPersistentConfiguration", storages = {@Storage("mybatis-spring-generator-config.xml")})
public class DefaultPersistentConfiguration implements PersistentStateComponent<DefaultPersistentConfiguration> {

    @Getter
    private PersistentConfig persistentConfig = new PersistentConfig();

    @Getter
    @Setter
    private Map<String, PersistentConfig> configs = new HashMap<>();


    @Nullable
    public static DefaultPersistentConfiguration getInstance() {
        return ServiceManager.getService(DefaultPersistentConfiguration.class);
    }

    public void setPersistentConfig(PersistentConfig persistentConfig) {
        PersistentConfig newConfig = JSON.parseObject(JSON.toJSONString(persistentConfig), PersistentConfig.class);
        this.persistentConfig = persistentConfig;
        String name = "default";
        if (null != persistentConfig.getName() && !"".equals(persistentConfig.getName())) {
            name = persistentConfig.getName();
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