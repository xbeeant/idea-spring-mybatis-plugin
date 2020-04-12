package org.xstudio.plugin.mybatis.util;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

/**
 * @author xiaobiao
 * @version 2019/5/1
 */
public class PrimaryKeyUtil {
    public static FullyQualifiedJavaType getFqjt(IntrospectedTable introspectedTable){
        String primaryKeyType = introspectedTable.getPrimaryKeyType();
        FullyQualifiedJavaType primaryKeyTypeFqjt = new FullyQualifiedJavaType(primaryKeyType);
        if(introspectedTable.getPrimaryKeyColumns().isEmpty()){
            // default key
            primaryKeyTypeFqjt = new FullyQualifiedJavaType("java.lang.Long");
        }
        if(null != introspectedTable.getPrimaryKeyColumns() && introspectedTable.getPrimaryKeyColumns().size() == 1){
            primaryKeyTypeFqjt = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType();
        }

        return primaryKeyTypeFqjt;
    }
}
