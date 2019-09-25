package org.xstudio.plugin.idea.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xstudio.plugin.idea.model.PersistentConfig;

/**
 * @author xiaobiao
 * @version 2019/9/22
 */
@State(name = "MybatisSpringGeneratorPersistentConfiguration", storages = {@Storage("mybatis-spring-generator-config.xml")})
public class DefaultPersistentConfiguration implements PersistentStateComponent<DefaultPersistentConfiguration> {
    @Getter
    @Setter
    private PersistentConfig persistentConfig = new PersistentConfig();

    @Nullable
    public static DefaultPersistentConfiguration getInstance() {
        return ServiceManager.getService(DefaultPersistentConfiguration.class);
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