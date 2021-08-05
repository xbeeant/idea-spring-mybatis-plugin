package org.xstudio.plugin.idea.util;

import com.intellij.openapi.util.text.StringUtil;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xiaobiao
 * @date 2021/5/17
 */
public class ObjectTableModel<T> extends AbstractTableModel {
    private String[] columnNames;
    private Set<String> editableFieldNames = new HashSet<>();
    private Field[] fieldNames;
    private List<T> items;

    public ObjectTableModel(List<T> items, Field[] fieldNames, String[] columnNames) {
        this.items = items;
        this.fieldNames = fieldNames;
        this.columnNames = columnNames;
    }

    @Override
    public String getColumnName(int i) {
        if (columnNames == null) {
            return fieldNames[i].getName();
        } else {
            return columnNames[i];
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        String fieldName = fieldNames[columnIndex].getName();
        return editableFieldNames.contains(fieldName);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Object item = items.get(rowIndex);
        String fieldName = fieldNames[columnIndex].getName();
        if (!StringUtil.isEmpty(fieldName)) {
            setProperty(item, fieldNames[columnIndex], aValue);
        }
    }

    private void setProperty(Object bean, Field field, Object val) {
        try {
            String fieldName = field.getName();
            String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = bean.getClass().getDeclaredMethod(methodName, field.getType());
            if (val.getClass().isAssignableFrom(field.getType())) {
                method.invoke(bean, val);
            } else {
                if (Boolean.class.equals(field.getType())) {
                    method.invoke(bean, Boolean.parseBoolean(String.valueOf(val)));
                } else if (Integer.class.equals(field.getType())) {
                    method.invoke(bean, Integer.parseInt(String.valueOf(val)));
                } else {
                    method.invoke(bean, val);
                }
            }

        } catch (Exception e) {
            // NULL
        }
    }

    public List<T> getItems() {
        return items;
    }

    public T getRow(int i) {
        return items.get(i);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return fieldNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object item = items.get(rowIndex);
        String fieldName = fieldNames[columnIndex].getName();
        if (!StringUtil.isEmpty(fieldName)) {
            return getProperty(item, fieldName);
        } else {
            return null;
        }
    }

    private Object getProperty(Object bean, String fieldName) {
        try {
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = bean.getClass().getDeclaredMethod(methodName);
            return method.invoke(bean);
        } catch (Exception e) {
            return "--";
        }
    }

    public void setEditableFieldNames(String[] editableFieldNames) {
        this.editableFieldNames.clear();
        this.editableFieldNames.addAll(Arrays.asList(editableFieldNames));
    }
}
