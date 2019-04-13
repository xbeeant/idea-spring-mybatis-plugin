package org.mybatis.generator.myplugin.antd;

public class Column {
    private String title;

    private String dataIndex;

    private Boolean sorter = false;

    private String key;

    private String width;

    private String name;

    private int type;

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public Column() {
    }

    public Column(String remarks, String shortName) {
        this.title = remarks;
        this.dataIndex = shortName;
        this.width = "100px";
    }

    public Column(int type, String typeName, String name) {
        this.name = name;
        this.typeName = typeName;
        this.type = type;
        this.width = "100px";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
    }

    public Boolean getSorter() {
        return sorter;
    }

    public void setSorter(Boolean sorter) {
        this.sorter = sorter;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
