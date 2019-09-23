package org.xstudio.plugin.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * @author xiaobiao
 * @date 2019/4/14.
 */
public class ModelLombokPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addLombokAnnotation(topLevelClass);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addLombokAnnotation(topLevelClass);
        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
    }

    private void addLombokAnnotation(TopLevelClass topLevelClass) {
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = false)");
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("lombok.EqualsAndHashCode");
        boolean imported = false;
        for (Field field : topLevelClass.getFields()) {
            for (String annotation : field.getAnnotations()) {
                if (annotation.contains("@JSONField")) {
                    topLevelClass.addImportedType("com.alibaba.fastjson.annotation.JSONField");
                    imported = true;
                    break;
                }
            }
            if (imported) {
                break;
            }
        }
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn
            introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

}
