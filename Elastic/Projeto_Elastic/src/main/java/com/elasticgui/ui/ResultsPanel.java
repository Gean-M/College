package com.elasticgui.ui;

import com.elasticgui.model.SearchResultItem;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Font;

/**
 * Tabela de resultados + painel de detalhe do item selecionado, exibindo o
 * trecho destacado (highlight) em HTML quando disponível.
 */
public class ResultsPanel extends JPanel {

    private final ResultsTableModel tableModel = new ResultsTableModel();
    private final JTable table = new JTable(tableModel);

    private final JLabel detailTitle = new JLabel(" ");
    private final JLabel detailUrl = new JLabel(" ");
    private final JEditorPane detailBody = new JEditorPane("text/html", "");
    private final JButton openUrlButton = new JButton("Abrir URL no navegador");

    public ResultsPanel() {
        setLayout(new BorderLayout());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.setAutoCreateRowSorter(false);
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(360);
        cm.getColumn(1).setPreferredWidth(110);
        cm.getColumn(2).setPreferredWidth(100);
        cm.getColumn(3).setPreferredWidth(100);
        cm.getColumn(4).setPreferredWidth(70);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedDetail();
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);

        detailTitle.setFont(detailTitle.getFont().deriveFont(Font.BOLD, 14f));
        detailBody.setEditable(false);
        detailBody.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        openUrlButton.setEnabled(false);

        JPanel detailHeader = new JPanel(new BorderLayout());
        JPanel detailHeaderText = new JPanel();
        detailHeaderText.setLayout(new javax.swing.BoxLayout(detailHeaderText, javax.swing.BoxLayout.Y_AXIS));
        detailHeaderText.add(detailTitle);
        detailHeaderText.add(detailUrl);
        detailHeader.add(detailHeaderText, BorderLayout.CENTER);
        detailHeader.add(openUrlButton, BorderLayout.EAST);
        detailHeader.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.add(detailHeader, BorderLayout.NORTH);
        detailPanel.add(new JScrollPane(detailBody), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, detailPanel);
        split.setResizeWeight(0.55);
        add(split, BorderLayout.CENTER);
    }

    private void showSelectedDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            clearDetail();
            return;
        }
        SearchResultItem item = tableModel.getItemAt(row);
        detailTitle.setText(item.getTitle() != null ? item.getTitle() : "(sem título)");
        detailUrl.setText(item.getUrl() != null ? item.getUrl() : "");
        String snippet = item.getHighlightHtml() != null ? item.getHighlightHtml() : escapeHtml(item.getAbstractText());
        detailBody.setText("<html><body style='font-family:sans-serif;font-size:10pt'>" + snippet + "</body></html>");
        detailBody.setCaretPosition(0);
        openUrlButton.setEnabled(item.getUrl() != null && !item.getUrl().isBlank());
    }

    private void clearDetail() {
        detailTitle.setText(" ");
        detailUrl.setText(" ");
        detailBody.setText("");
        openUrlButton.setEnabled(false);
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public void setItems(java.util.List<SearchResultItem> items) {
        tableModel.setItems(items);
        clearDetail();
        if (!items.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    public JButton getOpenUrlButton() {
        return openUrlButton;
    }

    public SearchResultItem getSelectedItem() {
        int row = table.getSelectedRow();
        return row >= 0 ? tableModel.getItemAt(row) : null;
    }
}
