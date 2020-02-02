package org.xstudio.plugin.mybatis;

import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * org.xstudio.plugin.mybatis
 *
 * @author xiaobiao
 * @version 2019/12/30
 */
public class MapperTypeHandlerPlugin extends PluginAdapter {
    private String TYPE_HANDLER_EXPRESSION = "(#\\s*(typehandler\\s*:\\s*)([\\w\\d\\.\\=\\s]*)\\s*#)";

    Map<String, String> typeHandlersMap = new HashMap<>();

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();

        for (IntrospectedColumn column : columns) {
            String remarks = column.getRemarks();
            if (remarks.contains("typehandler")) {
                Pattern pattern = Pattern.compile(TYPE_HANDLER_EXPRESSION);
                Matcher matcher = pattern.matcher(remarks);
                if (matcher.find()) {
                    String handler = matcher.group(3);
                    typeHandlersMap.put(column.getActualColumnName(), handler.trim());
                }
            }
        }
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addTypeHandlerAttribute(element);
        return super.sqlMapResultMapWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    private void addTypeHandlerAttribute(XmlElement element) {
        List<VisitableElement> elements = element.getElements();
        Iterator<VisitableElement> iterator = elements.iterator();
        while (iterator.hasNext()) {
            VisitableElement next = iterator.next();
            if (next instanceof XmlElement) {
                XmlElement xmlElement = (XmlElement) next;
                String handler = typeHandlersMap.get(xmlElement.getAttributes().get(0).getValue());
                if (!StringUtils.isEmpty(handler)) {
                    xmlElement.addAttribute(new Attribute("typeHandler", handler));
                }
            }
        }
    }

    private void replaceByTypeHandler(XmlElement element) {
        List<VisitableElement> elements = element.getElements();
        Iterator<VisitableElement> iterator = elements.iterator();
        while (iterator.hasNext()) {
            VisitableElement next = iterator.next();
            if (next instanceof XmlElement) {
                XmlElement xmlElement = (XmlElement) next;
                TextElement columnElement = (TextElement) xmlElement.getElements().get(0);
                String content = columnElement.getContent();
                String handler = typeHandlersMap.get(content.substring(content.indexOf("and ") + 3, content.indexOf("=")).trim());
                if (!StringUtils.isEmpty(handler)) {
                    content = content.replaceAll("}", ",typeHandler=" + handler + "}");
                    columnElement.setContent(content);
                }
            }
        }
    }


    @Override
    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addTypeHandlerAttribute(element);
        return super.sqlMapResultMapWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addTypeHandlerAttribute(element);
        return super.sqlMapSelectAllElementGenerated(element, introspectedTable);
    }


    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addTypeHandlerAttribute(element);
        return super.sqlMapDeleteByExampleElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        replaceByTypeHandler(element);
        return super.sqlMapExampleWhereClauseElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addTypeHandlerAttribute(element);
        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }


    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<VisitableElement> elements = element.getElements();
        XmlElement xmlElement = (XmlElement) elements.get(elements.size() - 2);
        for (VisitableElement visitableElement : xmlElement.getElements()) {
            TextElement textElement = (TextElement) ((XmlElement) visitableElement).getElements().get(0);
            String content = textElement.getContent();
            String handler = typeHandlersMap.get(content.substring(content.indexOf("#{") + 2, content.indexOf(",")).trim());
            if (!StringUtils.isEmpty(handler)) {
                content = content.replaceAll("}", ",typeHandler=" + handler + "}");
                textElement.setContent(content);
            }
        }

        // element.get(element.size - 2) . get elements each . get elements . replace
        return super.sqlMapUpdateByExampleSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addTypeHandlerAttribute(element);
        return super.sqlMapUpdateByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addTypeHandlerAttribute(element);
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<VisitableElement> elements = element.getElements();
        XmlElement xmlElement = (XmlElement) elements.get(elements.size() - 2);
        for (VisitableElement visitableElement : xmlElement.getElements()) {
            TextElement textElement = (TextElement) ((XmlElement) visitableElement).getElements().get(0);
            String content = textElement.getContent();
            String handler = typeHandlersMap.get(content.substring(content.indexOf("#{") + 2, content.indexOf(",")).trim());
            if (!StringUtils.isEmpty(handler)) {
                content = content.replaceAll("}", ",typeHandler=" + handler + "}");
                textElement.setContent(content);
            }
        }
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addTypeHandlerAttribute(element);
        return super.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addTypeHandlerAttribute(element);
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement xmlElement = (XmlElement) element.getElements().get(element.getElements().size() - 1);
        for (VisitableElement visitableElement : xmlElement.getElements()) {
            TextElement textElement = (TextElement) ((XmlElement) visitableElement).getElements().get(0);
            String content = textElement.getContent();
            String handler = typeHandlersMap.get(content.substring(content.indexOf("#{") + 2, content.indexOf(",")).trim());
            if (!StringUtils.isEmpty(handler)) {
                content = content.replaceAll("}", ",typeHandler=" + handler + "}");
                textElement.setContent(content);
            }
        }
        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapBlobColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addTypeHandlerAttribute(element);
        return super.sqlMapBlobColumnListElementGenerated(element, introspectedTable);
    }
}
