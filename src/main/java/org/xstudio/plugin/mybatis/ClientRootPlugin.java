package org.xstudio.plugin.mybatis;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.*;
import org.xstudio.plugin.mybatis.util.PrimaryKeyUtil;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author xiaobiao
 * @version 2018/1/26
 */
public class ClientRootPlugin extends PluginAdapter {

    private String rootClient;

    private FullyQualifiedJavaType rootClientFqjt;

    private String excludeMethods;
    private String excludeMapper;

    private boolean beginEndPlugin = false;

    private XmlElement updateByPrimaryKeySelectiveElement;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        rootClient = properties.getProperty("rootClient");
        excludeMethods = properties.getProperty("excludeMethods");
        excludeMapper = properties.getProperty("excludeMapper");
        rootClientFqjt = new FullyQualifiedJavaType(rootClient);
        beginEndPlugin = Boolean.valueOf(properties.getProperty("beginEndPluginEnable"));
        super.initialized(introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        batchInsertSelective(document, introspectedTable);
        batchDeleteByPrimaryKey(document, introspectedTable);
        batchUpdateByPrimaryKeySelective(document, introspectedTable);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        /*
         * 添加 继承
         */
        if (null != rootClient) {
            FullyQualifiedJavaType rootClientFqjtArgument = new FullyQualifiedJavaType(rootClient);
            FullyQualifiedJavaType tableFqjt = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
            rootClientFqjtArgument.addTypeArgument(tableFqjt);

            FullyQualifiedJavaType primaryKeyTypeFqjt = PrimaryKeyUtil.getFqjt(introspectedTable);
            rootClientFqjtArgument.addTypeArgument(primaryKeyTypeFqjt);
            interfaze.addImportedType(primaryKeyTypeFqjt);
            interfaze.addSuperInterface(rootClientFqjtArgument);

            interfaze.addImportedType(rootClientFqjt);
            interfaze.addImportedType(tableFqjt);
        }
        interfaze.getMethods().removeIf(method -> excludeMethods.contains(method.getName()));

        // 移除 *Example 依赖包
        FullyQualifiedTable fullyQualifiedTable = introspectedTable.getFullyQualifiedTable();
        String example = fullyQualifiedTable.getDomainObjectName().concat("Example");
        Set<FullyQualifiedJavaType> importedTypes = interfaze.getImportedTypes();
        importedTypes.removeIf(next -> next.getShortName().equals(example));
        importedTypes.removeIf(next -> next.getShortName().equals("Param"));
        boolean ignoreListRemove = false;
        for (Method method : interfaze.getMethods()) {
            Optional<FullyQualifiedJavaType> returnType = method.getReturnType();
            if (null != returnType && returnType.get().getShortName().equals("List")) {
                ignoreListRemove = true;
                break;
            }
        }
        if (!ignoreListRemove) {
            importedTypes.removeIf(next -> next.getShortName().equals("List"));
        }

        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        return true;
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        return super.sqlMapResultMapWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.getElements().remove(element.getElements().get(5));
        XmlElement whereElement = new XmlElement("where");
        addInclude(whereElement, "Prefixed_Example_Where_Clause");
        element.getElements().add(whereElement);

        element.getAttributes().removeIf(attribute -> "parameterType".equals(attribute.getName()));
        return super.sqlMapCountByExampleElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        return super.sqlMapDeleteByExampleElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        // key 查询条件添加key. 的前缀 满足多个key的情况
        setPrimaryKeyCondition(element, true, introspectedTable.getPrimaryKeyColumns());
        element.getAttributes().remove(element.getAttributes().get(1));
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.getElements().removeAll(element.getElements());
        context.getCommentGenerator().addComment(element);
        String prefix = "";
        if (!element.getAttributes().get(0).getValue().equalsIgnoreCase("Example_Where_Clause")) {
            prefix = "example.";
            element.getAttributes().removeIf(attribute -> "id".equals(attribute.getName()));
            element.addAttribute(new Attribute("id", "Prefixed_Example_Where_Clause"));
        }
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            addIfElement(element, column, prefix);
        }
        return super.sqlMapExampleWhereClauseElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        element.getAttributes().removeIf(attribute -> "parameterType".equals(attribute.getName()));
//        element.getElements().remove(5);
//        element.getElements().remove(9);
//        element.getElements().remove(9);
        XmlElement whereElement = new XmlElement("where");
        addInclude(whereElement, "Prefixed_Example_Where_Clause");
        element.getElements().add(whereElement);
        return super.sqlMapResultMapWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        element.getElements().remove(element.getElements().get(5));
        element.getElements().remove(element.getElements().get(7));
        element.getElements().remove(element.getElements().get(7));
        XmlElement whereElement = new XmlElement("where");
        addInclude(whereElement, "Prefixed_Example_Where_Clause");
        element.getElements().add(whereElement);
        element.getAttributes().removeIf(attribute -> "parameterType".equals(attribute.getName()));
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        element.getAttributes().removeIf(attribute -> "parameterType".equals(attribute.getName()));
        element.getElements().remove(5);
        element.getElements().remove(9);
        element.getElements().remove(9);
        XmlElement whereElement = new XmlElement("where");
        addInclude(whereElement, "Prefixed_Example_Where_Clause");
        element.getElements().add(whereElement);
        return super.sqlMapSelectByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        element.getAttributes().remove(element.getAttributes().get(2));
        // key 查询条件添加key. 的前缀 满足多个key的情况
        setPrimaryKeyCondition(element, true, introspectedTable.getPrimaryKeyColumns());

        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        element.getElements().remove(element.getElements().get(6));
        XmlElement whereElement = new XmlElement("where");
        addInclude(whereElement, "Prefixed_Example_Where_Clause");
        element.getElements().add(whereElement);
        element.getAttributes().remove(element.getAttributes().get(1));
        return super.sqlMapUpdateByExampleSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        element.getAttributes().remove(1);

        List<VisitableElement> elements = element.getElements();
        element.getElements().remove(elements.get(elements.size() - 1));
        XmlElement whereElement = new XmlElement("where");
        addInclude(whereElement, "Prefixed_Example_Where_Clause");
        elements.add(whereElement);
        return super.sqlMapUpdateByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        updateByPrimaryKeySelectiveElement = element;
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        setPrimaryKeyCondition(element, true, introspectedTable.getPrimaryKeyColumns());
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    private void setPrimaryKeyCondition(XmlElement element, boolean addPrefix, List<IntrospectedColumn> keyColumns) {
        // key 查询条件添加key. 的前缀 满足多个key的情况
        List<VisitableElement> elements = element.getElements();
        for (VisitableElement elementElement : elements) {
            if (elementElement instanceof TextElement) {
                TextElement textElement = (TextElement) elementElement;
                String content = textElement.getContent();
                if (keyColumns.size() > 1) {
                    if (addPrefix && content.contains("#{")) {
                        textElement.setContent(content.replace("#{", "#{key."));
                    }
                } else {
                    if (addPrefix && content.contains("#{")) {
                        textElement.setContent(content.replace("#{" + keyColumns.get(0).getJavaProperty(), "#{key"));
                    }
                }

            }
        }
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        setPrimaryKeyCondition(element, true, introspectedTable.getPrimaryKeyColumns());
        return super.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        setPrimaryKeyCondition(element, true, introspectedTable.getPrimaryKeyColumns());
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapBaseColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        return super.sqlMapBaseColumnListElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapBlobColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        return super.sqlMapBlobColumnListElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (excludeMapper.contains(element.getAttributes().get(0).getValue())) {
            return false;
        }
        return super.sqlMapSelectAllElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return super.clientSelectByExampleWithBLOBsMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return super.clientSelectByExampleWithoutBLOBsMethodGenerated(method, interfaze, introspectedTable);
    }

    private void addIfElement(XmlElement whereElement, IntrospectedColumn column, String prefix) {
        XmlElement ifElement = new XmlElement("if");
        StringBuilder sb = new StringBuilder();
        if (null == prefix) {
            prefix = "";
        }
        sb.append(prefix);
        sb.append(column.getJavaProperty());
        sb.append(" != null");
        Attribute attribute = new Attribute("test", sb.toString());

        sb = new StringBuilder("and ");
        sb.append(column.getActualColumnName());
        sb.append(" = #{");
        sb.append(prefix);
        sb.append(column.getJavaProperty());
        sb.append(",jdbcType=");
        sb.append(column.getJdbcTypeName());
        sb.append("}");
        ifElement.addElement(new TextElement(sb.toString()));
        ifElement.addAttribute(attribute);

        whereElement.addElement(ifElement);

        if (beginEndPlugin && column.getFullyQualifiedJavaType().equals(FullyQualifiedJavaType.getDateInstance())) {
            // **Begin
            ifElement = new XmlElement("if");
            sb = new StringBuilder(prefix);
            sb.append(column.getJavaProperty());
            sb.append("Begin != null");
            attribute = new Attribute("test", sb.toString());

            sb = new StringBuilder("and ");
            sb.append(column.getActualColumnName());
            sb.append(" &gt;= #{");
            sb.append(prefix);
            sb.append(column.getJavaProperty());
            sb.append("Begin,jdbcType=");
            sb.append(column.getJdbcTypeName());
            sb.append("}");
            ifElement.addElement(new TextElement(sb.toString()));
            ifElement.addAttribute(attribute);
            whereElement.addElement(ifElement);

            // **End
            ifElement = new XmlElement("if");
            sb = new StringBuilder(prefix);
            sb.append(column.getJavaProperty());
            sb.append("End != null");
            attribute = new Attribute("test", sb.toString());

            sb = new StringBuilder("and ");
            sb.append(column.getActualColumnName());
            sb.append(" &lt;= #{");
            sb.append(prefix);
            sb.append(column.getJavaProperty());
            sb.append("End,jdbcType=");
            sb.append(column.getJdbcTypeName());
            sb.append("}");
            ifElement.addElement(new TextElement(sb.toString()));
            ifElement.addAttribute(attribute);
            whereElement.addElement(ifElement);
        }
    }

    private void addInclude(XmlElement xmlElement, String include) {
        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", include));
        xmlElement.addElement(includeElement);
    }

    private void batchUpdateByPrimaryKeySelective(Document document, IntrospectedTable introspectedTable) {
        XmlElement element = new XmlElement("update");
        element.addAttribute(new Attribute("id", "batchUpdateByPrimaryKeySelective"));
        element.addAttribute(new Attribute("parameterType", "java.util.List"));
        if (introspectedTable.getPrimaryKeyColumns() != null && !introspectedTable.getPrimaryKeyColumns().isEmpty()) {

            XmlElement foreachElement = new XmlElement("foreach");
            foreachElement.addAttribute(new Attribute("collection", "list"));
            foreachElement.addAttribute(new Attribute("index", "index"));
            foreachElement.addAttribute(new Attribute("item", "item"));
            foreachElement.addAttribute(new Attribute("separator", ";"));
            context.getCommentGenerator().addComment(element);
            TextElement whereEl = new TextElement("");
            foreachElement.addElement(new TextElement("update " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
            XmlElement set = new XmlElement("set");
            for (VisitableElement el : updateByPrimaryKeySelectiveElement.getElements()) {
                if (el instanceof XmlElement) {
                    boolean isSetEl = ((XmlElement) el).getName().equalsIgnoreCase("set");
                    if (isSetEl) {
                        set = (XmlElement) el;
                    }
                } else {
                    if (((TextElement) el).getContent().contains("where")) {
                        whereEl = (TextElement) el;
                    }
                }
            }

            XmlElement setEl = new XmlElement("set");

            List<VisitableElement> ifEls = set.getElements();
            for (VisitableElement ifEl : ifEls) {
                XmlElement ifxEl = (XmlElement) ifEl;
                String value = ifxEl.getAttributes().get(0).getValue();
                String[] split = value.split(" ");
                XmlElement ifel0 = new XmlElement("if");
                String s = value.replaceAll(split[0], "item." + split[0]);
                ifel0.addAttribute(new Attribute("test", s));
                TextElement tx = (TextElement) ((XmlElement) ifEl).getElements().get(0);
                String s1 = tx.getContent().replaceAll("#\\{" + split[0], "#{item." + split[0]);
                ifel0.addElement(new TextElement(s1));
                setEl.addElement(ifel0);
            }

            foreachElement.addElement(setEl);

            String s = whereEl.getContent().replaceAll("#\\{", "#\\{item.");
            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
                s = s.replaceAll("item.key.", "item.");
            }
            whereEl = new TextElement(s);
            foreachElement.addElement(whereEl);

            element.addElement(foreachElement);
        }
        // todo 多主键的更新语句
        document.getRootElement().addElement(element);
    }

    private void batchInsertSelective(Document document, IntrospectedTable introspectedTable) {
        XmlElement element = new XmlElement("insert");
        element.addAttribute(new Attribute("id", "batchInsertSelective"));
        element.addAttribute(new Attribute("parameterType", "java.util.List"));
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("index", "index"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("separator", ";"));
        context.getCommentGenerator().addComment(element);

        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        XmlElement set = new XmlElement("set");
        XmlElement ifXml;
        TextElement text;
        foreachElement.addElement(new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        for (IntrospectedColumn column : columns) {
            ifXml = new XmlElement("if");
            ifXml.addAttribute(new Attribute("test", "item." + column.getJavaProperty() + " != null "));
            text = new TextElement(column.getActualColumnName() + " = #{item." + column.getJavaProperty() + ",jdbcType=" + column.getJdbcTypeName() + "},");
            ifXml.addElement(text);
            set.addElement(ifXml);
        }
        foreachElement.addElement(set);
        element.addElement(foreachElement);

        document.getRootElement().addElement(element);
    }

    private void batchDeleteByPrimaryKey(Document document, IntrospectedTable introspectedTable) {
        XmlElement element = new XmlElement("delete");
        element.addAttribute(new Attribute("id", "batchDeleteByPrimaryKey"));
        element.addAttribute(new Attribute("parameterType", "java.util.List"));
        if (introspectedTable.getPrimaryKeyColumns() != null && introspectedTable.getPrimaryKeyColumns().size() > 0) {

            XmlElement foreachElement = new XmlElement("foreach");
            foreachElement.addAttribute(new Attribute("collection", "items"));
            foreachElement.addAttribute(new Attribute("index", "index"));
            foreachElement.addAttribute(new Attribute("item", "item"));
            foreachElement.addAttribute(new Attribute("separator", ","));
            foreachElement.addAttribute(new Attribute("open", "("));
            foreachElement.addAttribute(new Attribute("close", ")"));
            context.getCommentGenerator().addComment(element);

            StringBuilder sb = new StringBuilder("delete from ");
            sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
            sb.append(" where ");
            sb.append(introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName());
            sb.append(" in");
            element.addElement(new TextElement(sb.toString()));

            foreachElement.addElement(new TextElement("#{item}"));

            element.addElement(foreachElement);
        }
        // todo 多个主键的删除逻辑不一样
        document.getRootElement().addElement(element);
    }
}
