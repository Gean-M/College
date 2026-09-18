package com.elasticgui.ui;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Map;

/**
 * Modelo de tabela genérico que exibe uma lista de Map&lt;String,Object&gt;
 * (formato devolvido pelos endpoints _cat/...?format=json) de acordo com uma
 * lista de colunas (chaves) escolhida.
 */
public class MapTableModel extends AbstractTableModel {

    private final List<String> columns;
    private final List<String> headerLabels;
    private List<Map<String, Object>> rows;

    public MapTableModel(List<String> columns, List<String> headerLabels) {
        this.columns = columns;
        this.headerLabels = headerLabels;
        this.rows = List.of();
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows != null ? rows : List.of();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getColumnName(int column) {
        return headerLabels.get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = rows.get(rowIndex).get(columns.get(columnIndex));
        return value != null ? value.toString() : "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
