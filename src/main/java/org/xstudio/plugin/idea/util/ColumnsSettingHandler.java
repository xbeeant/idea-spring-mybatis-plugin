/*
 * Copyright (c) 2019 Tony Ho. Some rights reserved.
 */

package org.xstudio.plugin.idea.util;

import io.github.xbeeant.mybatis.po.ColumnProperty;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColumnsSettingHandler {
    private String[] COLUMN_NAMES = {"Order", "Column name", "Mybatis TypeHandler", "Fuzzy"};
    private String[] EDITABLE_FIELD_NAMES = {"order", "typeHandler", "fuzzySearch"};
    private Field[] FIELD_NAMES = {new Field("order", String.class),
            new Field("column", String.class),
            new Field("typeHandler", String.class),
            new Field("fuzzySearch", Boolean.class)
    };

    public void initTable(JTable table, List<ColumnProperty> data) {
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        table.setModel(createTableModel(data));

        // set combo box editor after binding
        TableColumnModel columnModel = table.getColumnModel();

        TableColumn column = columnModel.getColumn(0);
        JTableHeader tableHeader = new JTableHeader(columnModel);
        table.setTableHeader(tableHeader);
        column.setCellEditor(getActionCellEditor());
        column.setCellRenderer(getActionCellRenderer());

        columnModel.getColumn(1).setCellRenderer(getColumnCellRenderer());

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(data.size());
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        table.getColumn("Order").setMaxWidth(80);
        table.getColumn("Column name").setMaxWidth(300);
        table.getColumn("Fuzzy").setMaxWidth(100);

        //声明下拉框
        JComboBox tubePurposeBox = new JComboBox();
        tubePurposeBox.addItem("false");
        tubePurposeBox.addItem("true");
        //声明单元格编辑器，传入下拉框
        TableCellEditor fuzzyCellEditor = new DefaultCellEditor(tubePurposeBox);
        //将下拉框单元格编辑器放入表格第二列中，以为只初始化了一行内容
        table.getColumnModel().getColumn(table.getColumn("Fuzzy").getModelIndex()).setCellEditor(fuzzyCellEditor);
    }

    private ObjectTableModel<ColumnProperty> createTableModel(List<ColumnProperty> items) {
        ObjectTableModel<ColumnProperty> tableModel = new ObjectTableModel<ColumnProperty>(items, FIELD_NAMES, COLUMN_NAMES) {
        };

        tableModel.setEditableFieldNames(EDITABLE_FIELD_NAMES);
        return tableModel;
    }

    private TableCellEditor getActionCellEditor() {
        JTextField jTextField = new JTextField();
        // display label for action editor
        DefaultListCellRenderer cellRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        };
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        return new DefaultCellEditor(jTextField);
    }

    private TableCellRenderer getActionCellRenderer() {
        // display label for action
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        return cellRenderer;
    }

    private TableCellRenderer getColumnCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
    }

}