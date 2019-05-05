package org.mybatis.generator.myplugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.myplugin.util.BeginEndPluginCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaobiao on 2017/2/22.
 */
public class MapperFuzzySearchPlugin extends PluginAdapter {
    private Boolean addBeginEnd = false;

    private List<IntrospectedColumn> keyColumns = new ArrayList<IntrospectedColumn>();

    private List<String> digit = new ArrayList<>();

    private List<String> time = new ArrayList<>();

    private String nonFuzzyColumn = "";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        digit.add("INTEGER");
        digit.add("FLOAT");
        digit.add("BIGINT");
        digit.add("TINYINT");
        digit.add("BYTE");
        digit.add("BIT");
        digit.add("BOOLEAN");
        digit.add("DOUBLE");


        time.add("DATE");
        time.add("TIMESTAMP");
        time.add("TIME");

        addBeginEnd = BeginEndPluginCheck.exist(this.getContext());
        if (null != properties.getProperty("nonFuzzyColumn")) {
            nonFuzzyColumn = properties.getProperty("nonFuzzyColumn");
        }
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            keyColumns.add(column);
        }
        super.initialized(introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        generateFuzzyElement(introspectedTable, document.getRootElement());
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private void generateFuzzyElement(IntrospectedTable introspectedTable, XmlElement rootElement) {
        fuzzySearch(introspectedTable, rootElement, false);
        fuzzySearchMap(introspectedTable, rootElement, false);
        fuzzySearch(introspectedTable, rootElement, true);
        fuzzySearchMap(introspectedTable, rootElement, true);
    }

    private void fuzzySearchMap(IntrospectedTable introspectedTable, XmlElement mapper, boolean pager) {
        XmlElement rootXmlElement;
        Attribute attribute;
        XmlElement ifElement;
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            keyColumns.add(column);
        }

        rootXmlElement = new XmlElement("sql");
        context.getCommentGenerator().addComment(rootXmlElement);
        if (pager) {
            attribute = new Attribute("id", "Prefixed_Fuzzy_Search_Where_Clause");
        } else {
            attribute = new Attribute("id", "Fuzzy_Search_Where_Clause");
        }
        rootXmlElement.addAttribute(attribute);

        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            ifElement = new XmlElement("if");
            String testClause = getTestClause(column, pager);
            ifElement.addAttribute(new Attribute("test", testClause));
            String sqlClause = getSqlClause(column, pager);
            ifElement.addElement(new TextElement(sqlClause));
            rootXmlElement.addElement(ifElement);

            if (addBeginEnd && "TIMESTAMP".equals(column.getJdbcTypeName())) {
                // ***Begin
                ifElement = new XmlElement("if");
                testClause = getTestClause(column, pager);
                testClause = testClause.replaceAll(column.getJavaProperty(), column.getJavaProperty() + "Begin");
                ifElement.addAttribute(new Attribute("test", testClause));
                sqlClause = getSqlClause(column, pager);
                sqlClause = sqlClause.replaceAll(column.getJavaProperty(), column.getJavaProperty() + "Begin");
                sqlClause = sqlClause.replaceAll(" = #", " &gt;= #");
                ifElement.addElement(new TextElement(sqlClause));
                rootXmlElement.addElement(ifElement);

                // ***End
                ifElement = new XmlElement("if");
                testClause = getTestClause(column, pager);
                testClause = testClause.replaceAll(column.getJavaProperty(), column.getJavaProperty() + "End");
                ifElement.addAttribute(new Attribute("test", testClause));
                sqlClause = getSqlClause(column, pager);
                sqlClause = sqlClause.replaceAll(column.getJavaProperty(), column.getJavaProperty() + "End");
                sqlClause = sqlClause.replaceAll(" = #", " &lt;= #");
                ifElement.addElement(new TextElement(sqlClause));
                rootXmlElement.addElement(ifElement);
            }
        }

        mapper.addElement(rootXmlElement);
    }

    private String getSqlClause(IntrospectedColumn column, boolean pager) {
        String name = column.getJavaProperty();
        String columName = column.getActualColumnName();
        String prefix = "";
        if (pager) {
            prefix = "example.";
        }
        if (digit.contains(column.getJdbcTypeName().toUpperCase())
                || time.contains(column.getJdbcTypeName().toUpperCase())
                || isKeyColumn(column)
                || column.getRemarks().contains("fuzzy: false") || column.getRemarks().contains("fuzzy:false")
                || nonFuzzyColumn.contains(column.getActualColumnName())
                || column.isBLOBColumn()
        ) {
            return "AND " + columName + " = #{" + prefix + name + ",jdbcType=" + column.getJdbcTypeName() + "}";
        }
        if (column.getRemarks().contains("fuzzy: startWidth") || column.getRemarks().contains("fuzzy:startWidth")) {
            return "AND " + columName + " LIKE CONCAT(#{" + prefix + name + ",jdbcType=" + column.getJdbcTypeName() + "}, '%')";
        }
        if (column.getRemarks().contains("fuzzy: endWidth") || column.getRemarks().contains("fuzzy:endWidth")) {
            return "AND " + columName + " LIKE CONCAT('%', #{" + prefix + name + ",jdbcType=" + column.getJdbcTypeName() + "})";
        }

        return "AND " + columName + " LIKE CONCAT('%', #{" + prefix + name + ",jdbcType=" + column.getJdbcTypeName() + "}, '%')";
    }

    private String getTestClause(IntrospectedColumn column, boolean pager) {
        String name = column.getJavaProperty();
        String prefix = "";
        if (pager) {
            prefix = "example.";
        }
        if (digit.contains(column.getJdbcTypeName())
                || time.contains(column.getJdbcTypeName())) {
            String suffix = "";
            if (digit.contains(column.getJdbcTypeName())) {
                suffix = " or " + prefix + name + " == 0";
            }
            return prefix + name + " != null" + suffix;
        }
        return prefix + name + " != null and " + prefix + name + " != '' ";
    }

    private void fuzzySearch(IntrospectedTable introspectedTable, XmlElement mapper, boolean pager) {
        XmlElement rootXmlElement;
        Attribute attribute;

        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            keyColumns.add(column);
        }

        rootXmlElement = new XmlElement("select");
        context.getCommentGenerator().addComment(rootXmlElement);
        if (pager) {
            attribute = new Attribute("id", "fuzzySearchByPager");
        } else {
            attribute = new Attribute("id", "fuzzySearch");
        }
        rootXmlElement.addAttribute(attribute);
        attribute = new Attribute("resultMap", "BaseResultMap");
        rootXmlElement.addAttribute(attribute);
        XmlElement whereElement = new XmlElement("where");
        fromCondition(introspectedTable, rootXmlElement);

        if (pager) {
            addInclude(whereElement, "Prefixed_Fuzzy_Search_Where_Clause");
        } else {
            addInclude(whereElement, "Fuzzy_Search_Where_Clause");
        }
        rootXmlElement.addElement(whereElement);
        mapper.addElement(rootXmlElement);
    }

    private void addInclude(XmlElement xmlElement, String include) {
        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", include));
        xmlElement.addElement(includeElement);
    }

    private boolean isKeyColumn(IntrospectedColumn column) {
        for (IntrospectedColumn key : keyColumns) {
            if (key.getActualColumnName().equals(column.getActualColumnName())) {
                return true;
            }
        }

        return false;
    }

    private void fromCondition(IntrospectedTable introspectedTable, XmlElement rootXmlElement) {
        StringBuilder sb;
        TextElement textElement;

        textElement = new TextElement("SELECT");
        rootXmlElement.addElement(textElement);

        if (introspectedTable.hasBLOBColumns()) {
            rootXmlElement.addElement(new TextElement("<include refid=\"Base_Column_List\" />,"));
            rootXmlElement.addElement(new TextElement("<include refid=\"Blob_Column_List\" />"));
        } else {
            rootXmlElement.addElement(new TextElement("<include refid=\"Base_Column_List\" />"));
        }

        sb = new StringBuilder();
        sb.append("FROM ");
        sb.append(introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
        textElement = new TextElement(sb.toString());
        rootXmlElement.addElement(textElement);
    }
}
