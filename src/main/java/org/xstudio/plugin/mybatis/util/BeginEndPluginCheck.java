package org.xstudio.plugin.mybatis.util;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.PluginAggregator;
import org.xstudio.plugin.mybatis.ModelBeginEndFieldPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author xiaobiao
 * @version 2019/3/20
 */
public class BeginEndPluginCheck {
    private BeginEndPluginCheck() {
    }

    public static boolean exist(Context context) {
        PluginAggregator plugins = (PluginAggregator) context.getPlugins();
        try {
            Field field = plugins.getClass().getDeclaredField("plugins");
            field.setAccessible(true);
            ArrayList<PluginAdapter> pluginAdapters = (ArrayList<PluginAdapter>) field.get(plugins);
            for (PluginAdapter pluginAdapter : pluginAdapters) {
                if (pluginAdapter instanceof ModelBeginEndFieldPlugin) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
