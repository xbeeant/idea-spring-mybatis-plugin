package org.mybatis.generator.myplugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author xiaobiao
 * @version 2019/5/1
 */
public class ModelNoBlobsPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> blobColumns = introspectedTable.getBLOBColumns();
        List<Field> fields = topLevelClass.getFields();
        List<String> existField = new ArrayList<>();
        for (Field field : fields) {
            existField.add(field.getName());
        }

        for (IntrospectedColumn blobColumn : blobColumns) {
            if (existField.contains(blobColumn.getJavaProperty())) {
                continue;
            }
            Field field = new Field(blobColumn.getJavaProperty(), blobColumn.getFullyQualifiedJavaType());
            field.setVisibility(JavaVisibility.PUBLIC);
            context.getCommentGenerator().addFieldComment(field,
                    introspectedTable, blobColumn);
            topLevelClass.addField(field);
        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        removeBlobs(element);
        return super.sqlMapResultMapWithBLOBsElementGenerated(element, introspectedTable);
    }

    private void removeBlobs(XmlElement element) {
        List<Attribute> attributes = element.getAttributes();
        Iterator<Attribute> iterator = attributes.iterator();
        while (iterator.hasNext()) {
            Attribute next = iterator.next();
            if (next.getValue().contains("WithBLOBs") && ("type".equals(next.getName()) || "parameterType".equals(next.getName()))) {
                next.setValue(next.getValue().replace("WithBLOBs", ""));
            }
        }
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        removeBlobs(element);
        return super.sqlMapSelectByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        removeBlobs(element);
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        removeBlobs(element);
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        removeBlobs(element);
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        removeBlobs(element);
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        Set<FullyQualifiedJavaType> importedTypes = topLevelClass.getImportedTypes();
        Iterator<FullyQualifiedJavaType> iterator = importedTypes.iterator();
        while (iterator.hasNext()) {
            FullyQualifiedJavaType next = iterator.next();
            if (next.getFullyQualifiedName().endsWith("WithBLOBs")) {
                iterator.remove();
            }
        }

        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }
}
