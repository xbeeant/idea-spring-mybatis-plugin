package org.xstudio.plugins.idea.generator;

/**
 * @author xiaobiao
 * @version 2019/3/22
 */
public enum  EnConfigField {
    /**
     * lombok
     */
    LOMBOK("lombok"),
    /**
     * lombok
     */
    GEN_WEB("antd"),
    /**
     * 表前缀移除
     */
    TABLE_PREFIX("TablePrefixPlugin"),
    /**
     * swagger2
     */
    SWAGGER("ModelSwaggerPropertyAnnotationPlugin"),
    /**
     * 字段标记移除
     */
    ALIAS_DELETE("ModelMarkDeleteFieldPlugin"),
    /**
     * 基础对象 服务
     */
    MODEL_ROOT("ModelRootObjectPlugin"),
    /**
     * alibaba fast序列化插件
     */
    FIELD_JSON("ModelFieldJsonSerializePlugin"),
    /**
     * 是否生成facade
     */
    GEN_FACADE("FacadePlugin");

    private String code;

    EnConfigField(String code) {
        this.code = code;
    }

    /**
     * Getter for property 'code'.
     *
     * @return Value for property 'code'.
     */
    public String getCode() {
        return code;
    }
}
