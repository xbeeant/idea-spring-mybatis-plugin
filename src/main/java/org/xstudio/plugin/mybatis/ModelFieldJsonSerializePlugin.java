package org.xstudio.plugin.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaobiao
 */
public class ModelFieldJsonSerializePlugin extends PluginAdapter {

    private static FullyQualifiedJavaType JSON_PROPERTY = new FullyQualifiedJavaType("com.alibaba.fastjson.annotation.JSONField");
    private String FIELD_EXPRESSION = "(#\\s*(json\\s*:\\s*)([\\w\\d\\-\\\"\\:\\.\\=\\s]*)\\s*#)";
    private String ALIAS_EXPRESSION = "(#\\s*(alias\\s*:\\s*)([\\w\\d\\.\\=\\s]*)\\s*#)";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String expression = properties.getProperty("expression");
        if (null != expression) {
            FIELD_EXPRESSION = expression;
        }
        super.initialized(introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String remarks = introspectedColumn.getRemarks();
        if (null != remarks || "".equals(remarks.trim())) {
            if ("Date".equals(field.getType().getShortName())) {
                List<Field> fields = topLevelClass.getFields();
                for (Field classFields : fields) {
                    if(classFields.getName().equals(field.getName().concat("Begin"))
                            || classFields.getName().equals(field.getName().concat("End")) ) {
                        classFields.addAnnotation("@JSONField(format = \"yyyy-MM-dd HH:mm:ss\")");
                    }
                }
                field.addAnnotation("@JSONField(format = \"yyyy-MM-dd HH:mm:ss\")");
            }
            return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
        }

        dateField(field, topLevelClass, remarks);

        aliasField(field, topLevelClass, remarks);
        topLevelClass.addImportedType("com.alibaba.fastjson.annotation.JSONField");
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    private void aliasField(Field field, TopLevelClass topLevelClass, String remarks) {
        if (remarks.contains("alias")) {
            Pattern pattern = Pattern.compile(ALIAS_EXPRESSION);
            Matcher matcher = pattern.matcher(remarks);
            if (matcher.find()) {
                String alias = matcher.group(3);
                field.addAnnotation("@JSONField(name = \"" + alias + "\")");
                topLevelClass.addImportedType(JSON_PROPERTY);
            }
        }
    }

    private void dateField(Field field, TopLevelClass topLevelClass, String remarks) {
        if (remarks.contains("json")) {
            Pattern pattern = Pattern.compile(FIELD_EXPRESSION);
            boolean skipGeneralDateFormat = false;
            Matcher matcher = pattern.matcher(remarks);
            if (matcher.find()) {
                String jsonField = matcher.group(3);
                if (jsonField.contains("serializeUsing")) {
                    String[] split = jsonField.split("=");
                    FullyQualifiedJavaType fullyQualifiedJavaType = new FullyQualifiedJavaType(split[1].trim());
                    field.addAnnotation("@JSONField(" + split[0].trim() + " = " + fullyQualifiedJavaType.getShortName() + ".class)");
                    topLevelClass.addImportedType(fullyQualifiedJavaType);
                } else if (jsonField.contains("format")) {
                    field.addAnnotation("@JSONField(" + jsonField + ")");
                } else {
                    field.addAnnotation("@JSONField(name = \"" + jsonField + "\")");
                }
                skipGeneralDateFormat = true;
                topLevelClass.addImportedType(JSON_PROPERTY);
            }

            if (!skipGeneralDateFormat) {
                String dataString = "java.util.Date";
                if (field.getType().getFullyQualifiedName().equals(dataString)) {
                    field.addAnnotation("@JSONField(format = \"yyyy-MM-dd HH:mm:ss\")");
                    topLevelClass.addImportedType(JSON_PROPERTY);
                }
            }
        }
    }
}
