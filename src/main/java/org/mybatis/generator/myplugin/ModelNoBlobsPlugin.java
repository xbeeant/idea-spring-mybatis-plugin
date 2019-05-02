package org.mybatis.generator.myplugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

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
        for (IntrospectedColumn blobColumn : blobColumns) {
            Field field = new Field(blobColumn.getJavaProperty(), blobColumn.getFullyQualifiedJavaType());
            field.setVisibility(JavaVisibility.PUBLIC);
            context.getCommentGenerator().addFieldComment(field,
                    introspectedTable, blobColumn);
            topLevelClass.addField(field);
        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }
}
