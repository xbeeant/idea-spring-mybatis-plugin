package org.xstudio.plugin.mybatis;

import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * date timestamp类型字段 添加 Begin End字段
 * <p>
 *
 * @author xiaobiao
 * @date 2017/2/21.
 */
public class ModelBeginEndFieldPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if ("Date".equals(field.getType().getShortName())) {
            Field fieldBegin = new Field(field.getName().concat("Begin"), field.getType());
            context.getCommentGenerator().addFieldComment(fieldBegin, introspectedTable);
            fieldBegin.setVisibility(JavaVisibility.PRIVATE);
            topLevelClass.addField(fieldBegin);

            Field fieldEnd = new Field(field.getName().concat("End"), field.getType());
            context.getCommentGenerator().addFieldComment(fieldEnd, introspectedTable);
            fieldEnd.setVisibility(JavaVisibility.PRIVATE);
            topLevelClass.addField(fieldEnd);
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if ("Date".equals(introspectedColumn.getFullyQualifiedJavaType().getShortName())) {
            Method methodBegin = getMethod(method, introspectedColumn, introspectedTable, "Begin");
            topLevelClass.addMethod(methodBegin);
            Method methodEnd = getMethod(method, introspectedColumn, introspectedTable, "End");
            topLevelClass.addMethod(methodEnd);
        }
        return super.modelGetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @NotNull
    private Method getMethod(Method method, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, String suffix) {
        Method newMethod = new Method(method.getName().concat(suffix));
        context.getCommentGenerator().addGeneralMethodComment(newMethod, introspectedTable);
        newMethod.addBodyLine("return " + introspectedColumn.getJavaProperty() + suffix + ";");
        newMethod.setVisibility(method.getVisibility());
        newMethod.setReturnType(method.getReturnType().get());
        return newMethod;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if ("Date".equals(introspectedColumn.getFullyQualifiedJavaType().getShortName())) {
            Method methodBegin = new Method(method.getName().concat("Begin"));
            context.getCommentGenerator().addGeneralMethodComment(methodBegin, introspectedTable);
            methodBegin.addBodyLine("this." + introspectedColumn.getJavaProperty() + "Begin = " + introspectedColumn.getJavaProperty() + "Begin;");
            methodBegin.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), introspectedColumn.getJavaProperty() + "Begin"));
            methodBegin.setVisibility(method.getVisibility());
            topLevelClass.addMethod(methodBegin);

            Method methodEnd = new Method(method.getName().concat("End"));
            context.getCommentGenerator().addGeneralMethodComment(methodEnd, introspectedTable);
            methodEnd.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), introspectedColumn.getJavaProperty() + "End"));
            methodEnd.addBodyLine("this." + introspectedColumn.getJavaProperty() + "End = " + introspectedColumn.getJavaProperty() + "End;");
            methodEnd.setVisibility(method.getVisibility());
            topLevelClass.addMethod(methodEnd);

        }
        return super.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }
}
