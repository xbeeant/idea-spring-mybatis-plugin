package org.xstudio.plugin.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * @author xiaobiao
 * @version 2018/3/1
 */
public class ModelSwaggerPropertyAnnotationPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
        String remarks = introspectedColumn.getRemarks().replaceAll("\"", "'");
        remarks = remarks.replaceAll("\r|\n", "");
        remarks = remarks.replaceAll("fuzzy: false","");
        remarks = remarks.replaceAll("fuzzy:false","");

        field.addAnnotation("@ApiModelProperty(\"" + remarks + "\")");
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
        topLevelClass.addAnnotation("@ApiModel(value = \"" + topLevelClass.getType().getShortName() + "\", description = \"" + topLevelClass.getType().getShortName() + "对象\")");
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }
}
