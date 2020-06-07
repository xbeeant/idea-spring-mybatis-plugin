package org.xstudio.plugin.idea.model;

/**
 * @author xiaobiao
 * @version 2019/9/23
 */
public class MybatisPluginConfig extends MybatisEnable {

    private static final long serialVersionUID = -4974530965013552854L;

    private boolean lombokPlugin = true;

    private boolean facadePlugin = false;

    private boolean prefixPlugin = true;

    private boolean swagger2Plugin = false;

    private boolean markDeletePlugin = true;

    private boolean baseServicePlugin = true;

    private boolean rootObjectPlugin = true;

    private boolean fastjsonPlugin = true;

    private boolean toStringHashcodeEquals = false;

    public boolean isLombokPlugin() {
        return lombokPlugin;
    }

    public void setLombokPlugin(boolean lombokPlugin) {
        this.lombokPlugin = lombokPlugin;
    }

    public boolean isFacadePlugin() {
        return facadePlugin;
    }

    public void setFacadePlugin(boolean facadePlugin) {
        this.facadePlugin = facadePlugin;
    }

    public boolean isPrefixPlugin() {
        return prefixPlugin;
    }

    public void setPrefixPlugin(boolean prefixPlugin) {
        this.prefixPlugin = prefixPlugin;
    }

    public boolean isSwagger2Plugin() {
        return swagger2Plugin;
    }

    public void setSwagger2Plugin(boolean swagger2Plugin) {
        this.swagger2Plugin = swagger2Plugin;
    }

    public boolean isMarkDeletePlugin() {
        return markDeletePlugin;
    }

    public void setMarkDeletePlugin(boolean markDeletePlugin) {
        this.markDeletePlugin = markDeletePlugin;
    }

    public boolean isBaseServicePlugin() {
        return baseServicePlugin;
    }

    public void setBaseServicePlugin(boolean baseServicePlugin) {
        this.baseServicePlugin = baseServicePlugin;
    }

    public boolean isRootObjectPlugin() {
        return rootObjectPlugin;
    }

    public void setRootObjectPlugin(boolean rootObjectPlugin) {
        this.rootObjectPlugin = rootObjectPlugin;
    }

    public boolean isFastjsonPlugin() {
        return fastjsonPlugin;
    }

    public void setFastjsonPlugin(boolean fastjsonPlugin) {
        this.fastjsonPlugin = fastjsonPlugin;
    }

    public boolean isToStringHashcodeEquals() {
        return toStringHashcodeEquals;
    }

    public void setToStringHashcodeEquals(boolean toStringHashcodeEquals) {
        this.toStringHashcodeEquals = toStringHashcodeEquals;
    }
}
