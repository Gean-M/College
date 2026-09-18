package com.elasticgui.ui;

import com.elasticgui.model.SearchResultItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ResultsTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"Título", "Tempo de leitura", "Classificação", "Data", "Score"};

    private List<SearchResultItem> items = new ArrayList<>();

    public void setItems(List<SearchResultItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        fireTableDataChanged();
    }

    public SearchResultItem getItemAt(int row) {
        return items.get(row);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SearchResultItem item = items.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return item.getTitle();
            case 1:
                return item.getReadingTime() != null ? item.getReadingTime() + " min" : "-";
            case 2:
                return item.getLabel() != null ? item.getLabel() : "-";
            case 3:
                return item.getDtCreation() != null ? item.getDtCreation() : "-";
            case 4:
                return item.getScore() != null ? String.format("%.3f", item.getScore()) : "-";
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
