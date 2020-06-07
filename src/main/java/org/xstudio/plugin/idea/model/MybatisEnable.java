package org.xstudio.plugin.idea.model;

import java.io.Serializable;

/**
 * @author Beeant
 * @version 2020/6/7
 */
public class MybatisEnable implements Serializable {
    /**
     * 是否覆盖
     */
    private boolean override = false;
    /**
     * 是否使用Example
     */
    private boolean useExample = false;
    /**
     * 是否使用Schema前缀
     */
    private boolean useSchemaPrefix;
    /**
     * 是否使用实际的列名
     */
    private boolean useActualColumnNames;
    /**
     * 是否启用as别名查询
     */
    private boolean useTableNameAlias;
    /**
     * 是否生成实体注释（来自表）
     */
    private boolean comment = true;
    /**
     * 是否生成JPA注解
     */
    private boolean annotation;
    /**
     * 是否是mysql8数据库
     */
    private boolean mysql8;

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public boolean isUseExample() {
        return useExample;
    }

    public void setUseExample(boolean useExample) {
        this.useExample = useExample;
    }

    public boolean isUseSchemaPrefix() {
        return useSchemaPrefix;
    }

    public void setUseSchemaPrefix(boolean useSchemaPrefix) {
        this.useSchemaPrefix = useSchemaPrefix;
    }

    public boolean isUseActualColumnNames() {
        return useActualColumnNames;
    }

    public void setUseActualColumnNames(boolean useActualColumnNames) {
        this.useActualColumnNames = useActualColumnNames;
    }

    public boolean isUseTableNameAlias() {
        return useTableNameAlias;
    }

    public void setUseTableNameAlias(boolean useTableNameAlias) {
        this.useTableNameAlias = useTableNameAlias;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isAnnotation() {
        return annotation;
    }

    public void setAnnotation(boolean annotation) {
        this.annotation = annotation;
    }

    public boolean isMysql8() {
        return mysql8;
    }

    public void setMysql8(boolean mysql8) {
        this.mysql8 = mysql8;
    }
}
