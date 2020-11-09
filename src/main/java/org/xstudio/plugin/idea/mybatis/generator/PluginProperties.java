package org.xstudio.plugin.idea.mybatis.generator;

/**
 * @author huangxiaobiao
 */
public class PluginProperties {
    private boolean chkComment = true;
    private boolean chkToString = false;
    private boolean chkUseAlias = false;
    private boolean chkLombok = false;
    private boolean chkGenerateFacade = false;
    private boolean chkRootEntityObject = true;
    private boolean chekOverwrite = false;
    private boolean chkUseSchemaPrefix = true;
    private boolean chkMySQL8 = false;
    private boolean chkSwaggerModel = false;
    private boolean chkMarkDelete = true;
    private boolean chkFastJson = true;


    public boolean isChkComment() {
        return chkComment;
    }

    public void setChkComment(boolean chkComment) {
        this.chkComment = chkComment;
    }

    public boolean isChkToString() {
        return chkToString;
    }

    public void setChkToString(boolean chkToString) {
        this.chkToString = chkToString;
    }

    public boolean isChkUseAlias() {
        return chkUseAlias;
    }

    public void setChkUseAlias(boolean chkUseAlias) {
        this.chkUseAlias = chkUseAlias;
    }

    public boolean isChkLombok() {
        return chkLombok;
    }

    public void setChkLombok(boolean chkLombok) {
        this.chkLombok = chkLombok;
    }

    public boolean isChkGenerateFacade() {
        return chkGenerateFacade;
    }

    public void setChkGenerateFacade(boolean chkGenerateFacade) {
        this.chkGenerateFacade = chkGenerateFacade;
    }

    public boolean isChkRootEntityObject() {
        return chkRootEntityObject;
    }

    public void setChkRootEntityObject(boolean chkRootEntityObject) {
        this.chkRootEntityObject = chkRootEntityObject;
    }

    public boolean isChekOverwrite() {
        return chekOverwrite;
    }

    public void setChekOverwrite(boolean chekOverwrite) {
        this.chekOverwrite = chekOverwrite;
    }

    public boolean isChkUseSchemaPrefix() {
        return chkUseSchemaPrefix;
    }

    public void setChkUseSchemaPrefix(boolean chkUseSchemaPrefix) {
        this.chkUseSchemaPrefix = chkUseSchemaPrefix;
    }

    public boolean isChkMySQL8() {
        return chkMySQL8;
    }

    public void setChkMySQL8(boolean chkMySQL8) {
        this.chkMySQL8 = chkMySQL8;
    }

    public boolean isChkSwaggerModel() {
        return chkSwaggerModel;
    }

    public void setChkSwaggerModel(boolean chkSwaggerModel) {
        this.chkSwaggerModel = chkSwaggerModel;
    }

    public boolean isChkMarkDelete() {
        return chkMarkDelete;
    }

    public void setChkMarkDelete(boolean chkMarkDelete) {
        this.chkMarkDelete = chkMarkDelete;
    }

    public boolean isChkFastJson() {
        return chkFastJson;
    }

    public void setChkFastJson(boolean chkFastJson) {
        this.chkFastJson = chkFastJson;
    }
}
