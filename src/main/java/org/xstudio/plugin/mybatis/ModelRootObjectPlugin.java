package org.xstudio.plugin.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.xstudio.plugin.mybatis.util.PrimaryKeyUtil;

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
        if (!topLevelClass.getSuperClass().isPresent()) {
            FullyQualifiedJavaType primaryKeyTypeFqjt = PrimaryKeyUtil.getFqjt(introspectedTable);
            topLevelClass.addImportedType(baseObject);
            FullyQualifiedJavaType superClass = new FullyQualifiedJavaType(baseObject.getFullyQualifiedName());
            superClass.addTypeArgument(primaryKeyTypeFqjt);
            topLevelClass.addImportedType(primaryKeyTypeFqjt);

            // 添加父类
            topLevelClass.setSuperClass(superClass);
        }
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType primaryKeyTypeFqjt = PrimaryKeyUtil.getFqjt(introspectedTable);
        topLevelClass.addImportedType(baseObject);
        FullyQualifiedJavaType superClass = new FullyQualifiedJavaType(baseObject.getFullyQualifiedName());
        superClass.addTypeArgument(primaryKeyTypeFqjt);
        topLevelClass.addImportedType(primaryKeyTypeFqjt);

        // 添加父类
        topLevelClass.setSuperClass(superClass);
        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (ignoreFields.contains(introspectedColumn.getActualColumnName())) {
            return false;
        }
        if (!topLevelClass.getSuperClass().isPresent()) {
            if (!generated && enableGenerateGetSetKey) {
                List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
                // set Key
                Method setKey = new Method("setKey");
                Method getKey = new Method("getKey");
                FullyQualifiedJavaType primaryKeyTypeFqjt = PrimaryKeyUtil.getFqjt(introspectedTable);
                if (!primaryKeyColumns.isEmpty()) {
                    if (primaryKeyColumns.size() == 1) {
                        String keyProperty = primaryKeyColumns.get(0).getJavaProperty();
                        setKey.addBodyLine("this." + keyProperty + " = key;");
                        getKey.addBodyLine("return " + keyProperty + ";");
                    } else {
                        getKey.addBodyLine(primaryKeyTypeFqjt.getShortNameWithoutTypeArguments() + " key = " +
                                "new " + primaryKeyTypeFqjt.getShortNameWithoutTypeArguments() + "();");
                        for (IntrospectedColumn primaryKeyColumn : primaryKeyColumns) {
                            String keyProperty = primaryKeyColumn.getJavaProperty();
                            String propertyMethod = JavaBeansUtil.getCamelCaseString(keyProperty, true);
                            setKey.addBodyLine("this." + keyProperty + " = key.get" + propertyMethod + "();");
                            getKey.addBodyLine("key.set" + propertyMethod + "(this." + keyProperty + ");");
                        }
                        getKey.addBodyLine("return key;");
                    }
                } else {
                    setKey.addBodyLine("");
                    getKey.addBodyLine("return null;");
                }

                setKey.addAnnotation("@Override");
                setKey.setVisibility(JavaVisibility.PUBLIC);
                setKey.addParameter(new Parameter(primaryKeyTypeFqjt, "key"));
                topLevelClass.addMethod(setKey);

                getKey.addAnnotation("@Override");
                getKey.setVisibility(JavaVisibility.PUBLIC);
                getKey.setReturnType(primaryKeyTypeFqjt);
                topLevelClass.addMethod(getKey);
                generated = true;
            }
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
