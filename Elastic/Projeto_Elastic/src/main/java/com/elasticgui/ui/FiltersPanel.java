package com.elasticgui.ui;

import com.elasticgui.model.SearchFilters;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import java.awt.GridLayout;

/**
 * Painel lateral com os filtros e a ordenação do Elasticsearch ensinados em
 * aula: range de reading_time, range de dt_creation, terms de label e sort
 * por relevância / tempo de leitura / data.
 */
public class FiltersPanel extends JPanel {

    private final JTextField readingMinField = new JTextField(4);
    private final JTextField readingMaxField = new JTextField(4);
    private final JTextField dateFromField = new JTextField(9);
    private final JTextField dateToField = new JTextField(9);
    private final JCheckBox labelRapido = new JCheckBox("rápido (≤ 5 min)");
    private final JCheckBox labelMedio = new JCheckBox("médio (6-10 min)");
    private final JCheckBox labelDemorado = new JCheckBox("demorado (11-15 min)");
    private final JComboBox<SearchFilters.SortMode> sortCombo =
            new JComboBox<>(SearchFilters.SortMode.values());

    private final JButton applyButton = new JButton("Aplicar filtros");
    private final JButton clearButton = new JButton("Limpar filtros");

    public FiltersPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setPreferredSize(new Dimension(250, 0));

        add(titled("Tempo de leitura (min)", readingTimeRow()));
        add(Box.createVerticalStrut(10));
        add(titled("Data de criação (dt_creation)", dateRow()));
        add(Box.createVerticalStrut(10));
        add(titled("Classificação (label)", labelsColumn()));
        add(Box.createVerticalStrut(10));
        add(titled("Ordenar por", sortRow()));
        add(Box.createVerticalStrut(14));

        JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 6));
        buttons.add(applyButton);
        buttons.add(clearButton);
        buttons.setAlignmentX(LEFT_ALIGNMENT);
        add(buttons);
        add(Box.createVerticalGlue());

        clearButton.addActionListener(e -> reset());

        readingMinField.setToolTipText("Valor mínimo de reading_time (ex.: 3)");
        readingMaxField.setToolTipText("Valor máximo de reading_time (ex.: 10)");
        dateFromField.setToolTipText("Formato AAAA-MM-DD, ex.: 2018-01-01");
        dateToField.setToolTipText("Formato AAAA-MM-DD, ex.: 2020-12-31");
    }

    private JPanel titled(String title, JPanel content) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(LEFT_ALIGNMENT);
        wrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION));
        content.setAlignmentX(LEFT_ALIGNMENT);
        wrapper.add(content);
        return wrapper;
    }

    private JPanel readingTimeRow() {
        JPanel p = new JPanel();
        p.add(new JLabel("de"));
        p.add(readingMinField);
        p.add(new JLabel("até"));
        p.add(readingMaxField);
        return p;
    }

    private JPanel dateRow() {
        JPanel p = new JPanel();
        p.add(new JLabel("de"));
        p.add(dateFromField);
        p.add(new JLabel("até"));
        p.add(dateToField);
        return p;
    }

    private JPanel labelsColumn() {
        JPanel p = new JPanel(new GridLayout(3, 1));
        p.add(labelRapido);
        p.add(labelMedio);
        p.add(labelDemorado);
        return p;
    }

    private JPanel sortRow() {
        JPanel p = new JPanel();
        p.add(sortCombo);
        return p;
    }

    public void reset() {
        readingMinField.setText("");
        readingMaxField.setText("");
        dateFromField.setText("");
        dateToField.setText("");
        labelRapido.setSelected(false);
        labelMedio.setSelected(false);
        labelDemorado.setSelected(false);
        sortCombo.setSelectedItem(SearchFilters.SortMode.RELEVANCIA);
    }

    /** Preenche o objeto de filtros com o que está selecionado neste painel. Lança IllegalArgumentException em caso de valor inválido. */
    public void applyTo(SearchFilters filters) {
        filters.setReadingTimeMin(parseIntOrNull(readingMinField.getText(), "Tempo de leitura (de)"));
        filters.setReadingTimeMax(parseIntOrNull(readingMaxField.getText(), "Tempo de leitura (até)"));
        filters.setDateFrom(blankToNull(dateFromField.getText()));
        filters.setDateTo(blankToNull(dateToField.getText()));

        filters.getLabels().clear();
        if (labelRapido.isSelected()) {
            filters.getLabels().add("rápido");
        }
        if (labelMedio.isSelected()) {
            filters.getLabels().add("médio");
        }
        if (labelDemorado.isSelected()) {
            filters.getLabels().add("demorado");
        }

        filters.setSortMode((SearchFilters.SortMode) sortCombo.getSelectedItem());
    }

    public JButton getApplyButton() {
        return applyButton;
    }

    public JButton getClearButton() {
        return clearButton;
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static Integer parseIntOrNull(String s, String fieldLabel) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido em \"" + fieldLabel + "\": " + s);
        }
    }
}
