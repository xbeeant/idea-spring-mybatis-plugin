package org.mybatis.generator.myplugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.Iterator;
import java.util.List;

/**
 * @author xiaobiao
 * @version 2017/2/21.
 */
public class ModelMarkDeleteFieldPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        Iterator<IntrospectedColumn> allColumns = introspectedTable.getAllColumns().iterator();
        removeDeletedField(allColumns);

        Iterator<IntrospectedColumn> baseColumns = introspectedTable.getBaseColumns().iterator();
        removeDeletedField(baseColumns);
        Iterator<IntrospectedColumn> blobColumns = introspectedTable.getBLOBColumns().iterator();
        removeDeletedField(blobColumns);
        super.initialized(introspectedTable);
    }

    private void removeDeletedField(Iterator<IntrospectedColumn> columns) {
        IntrospectedColumn column;
        String remarks;
        boolean containsDeleteText;
        while (columns.hasNext()) {
            column = columns.next();
            remarks = column.getRemarks();
            containsDeleteText = null != remarks && !"".equalsIgnoreCase(remarks.trim()) &&
                    (remarks.contains("#delete#") || remarks.contains("#deleted#"));
            if (containsDeleteText) {
                columns.remove();
            }
        }
    }
}
