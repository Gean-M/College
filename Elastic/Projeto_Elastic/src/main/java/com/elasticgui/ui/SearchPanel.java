package com.elasticgui.ui;

import com.elasticgui.model.SearchFilters;
import com.elasticgui.model.SearchResponse;
import com.elasticgui.model.StatsResult;
import com.elasticgui.service.SearchService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.Supplier;

/**
 * Painel principal da aplicação. Cumpre o endpoint GET /search do README
 * (query + page) usando uma interface gráfica em vez de uma chamada HTTP, e
 * acrescenta os recursos de filtro/ordenação/paginação do Elasticsearch
 * vistos ao longo do curso.
 */
public class SearchPanel extends JPanel {

    private final Supplier<SearchService> searchServiceSupplier;

    private final JTextField queryField = new JTextField(35);
    private final JButton searchButton = new JButton("Buscar");

    private final JComboBox<String> operatorCombo =
            new JComboBox<>(new String[]{"OU entre os termos (padrão)", "E entre todos os termos"});
    private final JComboBox<String> fuzzinessCombo =
            new JComboBox<>(new String[]{"Fuzziness desativado", "AUTO", "0", "1", "2"});
    private final JCheckBox phraseCheckbox = new JCheckBox("Priorizar frase exata");
    private final JCheckBox highlightCheckbox = new JCheckBox("Destacar termos", true);

    private final FiltersPanel filtersPanel = new FiltersPanel();
    private final ResultsPanel resultsPanel = new ResultsPanel();

    private final JLabel suggestionLabel = new JLabel(" ");
    private final JButton useSuggestionButton = new JButton("Buscar sugestão");

    private final JLabel statusLabel = new JLabel(" ");
    private final JButton prevButton = new JButton("\u25C0 Anterior");
    private final JButton nextButton = new JButton("Próxima \u25B6");
    private final JLabel pageInfoLabel = new JLabel("Página 0 de 0", SwingConstants.CENTER);
    private final JTextField goToPageField = new JTextField(3);
    private final JButton goToPageButton = new JButton("Ir");
    private final JComboBox<Integer> pageSizeCombo = new JComboBox<>(new Integer[]{10, 20, 50});
    private final JButton statsButton = new JButton("Ver estatísticas do conjunto");

    private final SearchFilters currentFilters = new SearchFilters();
    private SearchResponse lastResponse;

    public SearchPanel(Supplier<SearchService> searchServiceSupplier) {
        this.searchServiceSupplier = searchServiceSupplier;
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(filtersPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(buildSuggestionBar(), BorderLayout.NORTH);
        centerPanel.add(resultsPanel, BorderLayout.CENTER);
        centerPanel.add(buildPaginationBar(), BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        wireActions();
        suggestionLabel.setVisible(false);
        useSuggestionButton.setVisible(false);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JLabel label = new JLabel("Buscar em \"content\":");
        searchRow.add(label);
        searchRow.add(queryField);
        searchRow.add(searchButton);
        queryField.addActionListener(e -> doSearch(1));

        JPanel optionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        optionsRow.add(new JLabel("Operador:"));
        optionsRow.add(operatorCombo);
        optionsRow.add(new JLabel("Fuzziness:"));
        optionsRow.add(fuzzinessCombo);
        optionsRow.add(phraseCheckbox);
        optionsRow.add(highlightCheckbox);

        top.add(searchRow, BorderLayout.NORTH);
        top.add(optionsRow, BorderLayout.SOUTH);
        return top;
    }

    private JPanel buildSuggestionBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        suggestionLabel.setFont(suggestionLabel.getFont().deriveFont(Font.ITALIC));
        p.add(suggestionLabel);
        p.add(useSuggestionButton);
        return p;
    }

    private JPanel buildPaginationBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        left.add(statusLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        right.add(prevButton);
        right.add(pageInfoLabel);
        right.add(nextButton);
        right.add(new JLabel("Ir para página:"));
        right.add(goToPageField);
        right.add(goToPageButton);
        right.add(new JLabel("Resultados por página:"));
        pageSizeCombo.setSelectedItem(10);
        right.add(pageSizeCombo);
        right.add(statsButton);

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private void wireActions() {
        searchButton.addActionListener(e -> doSearch(1));
        filtersPanel.getApplyButton().addActionListener(e -> doSearch(1));
        filtersPanel.getClearButton().addActionListener(e -> {
            if (lastResponse != null) {
                doSearch(1);
            }
        });

        prevButton.addActionListener(e -> {
            if (lastResponse != null && lastResponse.getCurrentPage() > 1) {
                doSearch(lastResponse.getCurrentPage() - 1);
            }
        });
        nextButton.addActionListener(e -> {
            if (lastResponse != null && lastResponse.getCurrentPage() < lastResponse.getTotalPages()) {
                doSearch(lastResponse.getCurrentPage() + 1);
            }
        });
        goToPageButton.addActionListener(e -> {
            try {
                int page = Integer.parseInt(goToPageField.getText().trim());
                doSearch(page);
            } catch (NumberFormatException ex) {
                UiUtils.showError(this, "Página inválida", ex);
            }
        });
        pageSizeCombo.addActionListener(e -> doSearch(1));

        useSuggestionButton.addActionListener(e -> {
            String suggestion = suggestionLabel.getClientProperty("suggestionText") == null
                    ? null : suggestionLabel.getClientProperty("suggestionText").toString();
            if (suggestion != null) {
                queryField.setText(suggestion);
                doSearch(1);
            }
        });

        resultsPanel.getOpenUrlButton().addActionListener(e -> {
            var item = resultsPanel.getSelectedItem();
            if (item != null) {
                UiUtils.openInBrowser(this, item.getUrl());
            }
        });

        statsButton.addActionListener(e -> showStats());
    }

    private void collectFiltersFromUi() {
        currentFilters.setText(queryField.getText());
        currentFilters.setOperator(operatorCombo.getSelectedIndex() == 1
                ? SearchFilters.Operator.AND : SearchFilters.Operator.OR);
        String fuzziness = (String) fuzzinessCombo.getSelectedItem();
        currentFilters.setFuzziness("Fuzziness desativado".equals(fuzziness) ? null : fuzziness);
        currentFilters.setPhraseBoost(phraseCheckbox.isSelected());
        currentFilters.setHighlight(highlightCheckbox.isSelected());
        currentFilters.setPageSize((Integer) pageSizeCombo.getSelectedItem());
        filtersPanel.applyTo(currentFilters);
    }

    private void doSearch(int page) {
        String text = queryField.getText();
        if (text == null || text.isBlank()) {
            UiUtils.showInfo(this, "Campo obrigatório", "Digite um termo de busca (equivalente ao parâmetro \"query\").");
            return;
        }
        try {
            collectFiltersFromUi();
        } catch (IllegalArgumentException ex) {
            UiUtils.showError(this, "Filtro inválido", ex);
            return;
        }
        currentFilters.setPage(page);

        setControlsEnabled(false);
        statusLabel.setText("Buscando...");
        SearchFilters snapshot = currentFilters.copy();

        UiUtils.runAsync(
                () -> searchServiceSupplier.get().search(snapshot),
                response -> {
                    setControlsEnabled(true);
                    onSearchSuccess(response);
                },
                error -> {
                    setControlsEnabled(true);
                    statusLabel.setText("Falha na busca.");
                    UiUtils.showError(this, "Erro ao buscar no Elasticsearch", error);
                }
        );
    }

    private void onSearchSuccess(SearchResponse response) {
        this.lastResponse = response;
        resultsPanel.setItems(response.getItems());

        statusLabel.setText(String.format("%d resultado(s) encontrados em %d ms.",
                response.getTotalHits(), response.getTookMillis()));
        pageInfoLabel.setText(String.format("Página %d de %d",
                response.getTotalPages() == 0 ? 0 : response.getCurrentPage(), response.getTotalPages()));
        prevButton.setEnabled(response.getCurrentPage() > 1);
        nextButton.setEnabled(response.getCurrentPage() < response.getTotalPages());

        if (response.getTotalHits() == 0 && response.getSuggestions() != null && !response.getSuggestions().isEmpty()) {
            String suggestion = response.getSuggestions().get(0);
            suggestionLabel.setText("Nenhum resultado. Você quis dizer: \"" + suggestion + "\"?");
            suggestionLabel.putClientProperty("suggestionText", suggestion);
            suggestionLabel.setVisible(true);
            useSuggestionButton.setVisible(true);
        } else {
            suggestionLabel.setVisible(false);
            useSuggestionButton.setVisible(false);
        }
    }

    private void showStats() {
        if (queryField.getText() == null || queryField.getText().isBlank()) {
            UiUtils.showInfo(this, "Campo obrigatório", "Digite um termo de busca antes de ver as estatísticas.");
            return;
        }
        try {
            collectFiltersFromUi();
        } catch (IllegalArgumentException ex) {
            UiUtils.showError(this, "Filtro inválido", ex);
            return;
        }
        SearchFilters snapshot = currentFilters.copy();
        statsButton.setEnabled(false);
        UiUtils.runAsync(
                () -> searchServiceSupplier.get().stats(snapshot),
                stats -> {
                    statsButton.setEnabled(true);
                    showStatsDialog(stats);
                },
                error -> {
                    statsButton.setEnabled(true);
                    UiUtils.showError(this, "Erro ao calcular estatísticas", error);
                }
        );
    }

    private void showStatsDialog(StatsResult stats) {
        String message = String.format(
                "Estatísticas de \"reading_time\" para o conjunto filtrado:\n\n" +
                        "Documentos:      %d\n" +
                        "Mínimo:          %s\n" +
                        "Máximo:          %s\n" +
                        "Média:           %s\n" +
                        "Soma:            %s",
                stats.getCount(),
                formatOrDash(stats.getMin()),
                formatOrDash(stats.getMax()),
                formatOrDash(stats.getAvg()),
                formatOrDash(stats.getSum()));
        UiUtils.showInfo(this, "Estatísticas (aggregation \"stats\")", message);
    }

    private static String formatOrDash(Double d) {
        return d == null ? "-" : String.format("%.2f", d);
    }

    private void setControlsEnabled(boolean enabled) {
        searchButton.setEnabled(enabled);
        filtersPanel.getApplyButton().setEnabled(enabled);
        prevButton.setEnabled(enabled && lastResponse != null && lastResponse.getCurrentPage() > 1);
        nextButton.setEnabled(enabled && lastResponse != null && lastResponse.getCurrentPage() < lastResponse.getTotalPages());
        goToPageButton.setEnabled(enabled);
        statsButton.setEnabled(enabled);
    }
}
