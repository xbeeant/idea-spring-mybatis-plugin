package org.mybatis.generator.myplugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaobiao on 2017/2/22.
 */
public class TablePrefixPlugin extends PluginAdapter {
    List<String> prefixCamels = new ArrayList<>();
    private String prefix = "";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private List<String> getPrefixCamel() {
        if (!"".equals(prefix) && null != prefix) {
            if (prefix.contains(",")) {
                String[] split = prefix.split(",");
                for (int i = 0; i < split.length; i++) {
                    prefixCamels.add(JavaBeansUtil.getCamelCaseString(split[i].trim(), true));
                }
            } else {
                prefixCamels.add(JavaBeansUtil.getCamelCaseString(prefix, true));
            }
        }
        return prefixCamels;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        prefix = properties.getProperty("prefix");
        prefixCamels = getPrefixCamel();

        for (String prefixCamel : prefixCamels) {
            // 替换 对象的前缀
            introspectedTable.setBaseRecordType(introspectedTable.getBaseRecordType().replace(prefixCamel, ""));
            // 替换 mapper 文件的前缀
            introspectedTable.setMyBatis3XmlMapperFileName(introspectedTable.getMyBatis3XmlMapperFileName().replace(prefixCamel, ""));
            // 替换 dao 接口的前缀
            introspectedTable.setMyBatis3XmlMapperPackage(introspectedTable.getMyBatis3XmlMapperPackage().replaceAll(prefixCamel, ""));
            // 替换 dao 接口的前缀
            introspectedTable.setMyBatis3JavaMapperType(introspectedTable.getMyBatis3JavaMapperType().replace(prefixCamel, ""));
        }

        super.initialized(introspectedTable);
    }
}
