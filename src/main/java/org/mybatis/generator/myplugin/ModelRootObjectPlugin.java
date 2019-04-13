package org.mybatis.generator.myplugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaobiao
 * @date 2017/2/21.
 */
public class ModelRootObjectPlugin extends PluginAdapter {

    private List<String> ignoreFields = new ArrayList<>();

    private FullyQualifiedJavaType baseObject;

    private Boolean generated = false;

    private Boolean enableGenerateGetSetKey = false;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        // 获取被忽略字段
        String excludeFields = properties.getProperty("excludeFields");
        if (null != excludeFields) {
            String[] fields = excludeFields.split(",");
            for (String field : fields) {
                ignoreFields.add(field.trim());
            }
        }
        generated = false;
        baseObject = new FullyQualifiedJavaType(properties.getProperty("rootObject"));
        String enableProperty = properties.getProperty("generateGetSetKey");
        if (Boolean.valueOf(enableProperty)) {
            enableGenerateGetSetKey = true;
        }

        super.initialized(introspectedTable);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 添加父类
        topLevelClass.setSuperClass(baseObject);
        topLevelClass.addImportedType(baseObject);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (ignoreFields.contains(introspectedColumn.getActualColumnName())) {
            return false;
        }

        if (!generated && enableGenerateGetSetKey) {
            List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
            // set Key
            Method setKey = new Method("setKey");
            Method getKey = new Method("getKey");
            if (!primaryKeyColumns.isEmpty()) {
                IntrospectedColumn keyColumn = primaryKeyColumns.get(0);
                // set key
                setKey.addBodyLine("if (null == key ||  \"null\".equals(key)) {");
                setKey.addBodyLine(JavaBeansUtil.getSetterMethodName(keyColumn.getJavaProperty()) + "(null);");
                setKey.addBodyLine("return;");
                setKey.addBodyLine("}");
                switch (keyColumn.getFullyQualifiedJavaType().getShortName()) {
                    case "Long":
                        setKey.addBodyLine(JavaBeansUtil.getSetterMethodName(keyColumn.getJavaProperty()) + "(Long.valueOf(String.valueOf(key)));");
                        break;
                    case "Integer":
                        setKey.addBodyLine(JavaBeansUtil.getSetterMethodName(keyColumn.getJavaProperty()) + "(Integer.valueOf(String.valueOf(key)));");
                        break;
                    case "Double":
                        setKey.addBodyLine(JavaBeansUtil.getSetterMethodName(keyColumn.getJavaProperty()) + "(Double.valueOf(String.valueOf(key)));");
                        break;
                    default:
                        setKey.addBodyLine(JavaBeansUtil.getSetterMethodName(keyColumn.getJavaProperty()) + "(String.valueOf(key));");
                }

                switch (keyColumn.getFullyQualifiedJavaType().getShortName()) {
                    case "String":
                        getKey.addBodyLine("return " + JavaBeansUtil.getGetterMethodName(keyColumn.getJavaProperty(), keyColumn.getFullyQualifiedJavaType()) + "();");
                        break;
                    default:
                        getKey.addBodyLine("return String.valueOf(" + JavaBeansUtil.getGetterMethodName(keyColumn.getJavaProperty(), keyColumn.getFullyQualifiedJavaType()) + "());");
                }
            } else {
                setKey.addBodyLine("");
                getKey.addBodyLine("return null;");
            }

            setKey.addAnnotation("@Override");
            setKey.setVisibility(JavaVisibility.PUBLIC);
            setKey.addParameter(new Parameter(new FullyQualifiedJavaType("java.lang.Object"), "key"));
            topLevelClass.addMethod(setKey);

            getKey.addAnnotation("@Override");
            getKey.setVisibility(JavaVisibility.PUBLIC);
            getKey.setReturnType(new FullyQualifiedJavaType("java.lang.String"));
            topLevelClass.addMethod(getKey);
            generated = true;
        }

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        // 如果字段在被忽略列表中，不生成该字段的set方法
        if (ignoreFields.contains(introspectedColumn.getActualColumnName())) {
            return false;
        }
        return super.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn
            introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        // 如果字段在被忽略列表中，不生成该字段的get方法
        if (ignoreFields.contains(introspectedColumn.getActualColumnName())) {
            return false;
        }
        return super.modelGetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

}
