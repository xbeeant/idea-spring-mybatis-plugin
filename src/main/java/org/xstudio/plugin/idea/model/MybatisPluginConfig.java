package org.xstudio.plugin.idea.model;

import lombok.Data;

/**
 * @author xiaobiao
 * @version 2019/9/23
 */
@Data
public class MybatisPluginConfig {
    private boolean lombokPlugin = true;

    private boolean facadePlugin = false;

    private boolean prefixPlugin = true;

    private boolean swagger2Plugin = false;

    private boolean markDeletePlugin = true;

    private boolean baseServicePlugin = true;

    private boolean rootObjectPlugin = true;

    private boolean fastjsonPlugin = true;

    private boolean toStringHashcodeEquals = false;
}
