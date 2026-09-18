package com.elasticgui.model;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representa todos os parâmetros que o usuário pode configurar na tela de
 * busca: o texto pesquisado (obrigatório, equivalente ao "query" do README),
 * a página (equivalente ao "page" do README) e os filtros/opções extras do
 * Elasticsearch vistos em aula (operador AND/OR, frase exata, fuzziness,
 * ordenação, faixa de tempo de leitura, faixa de data e classificação/label).
 */
public class SearchFilters {

    public enum Operator { OR, AND }

    public enum SortMode {
        RELEVANCIA("Relevância"),
        LEITURA_ASC("Tempo de leitura (menor primeiro)"),
        LEITURA_DESC("Tempo de leitura (maior primeiro)"),
        DATA_ASC("Data de criação (mais antiga primeiro)"),
        DATA_DESC("Data de criação (mais recente primeiro)");

        private final String label;

        SortMode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private String text = "";
    private int page = 1;
    private int pageSize = 10;

    private Operator operator = Operator.OR;
    private boolean phraseBoost = false;
    private String fuzziness = null; // null = desativado; "AUTO", "0", "1" ou "2"
    private boolean highlight = true;
    private SortMode sortMode = SortMode.RELEVANCIA;

    private Integer readingTimeMin = null;
    private Integer readingTimeMax = null;
    private String dateFrom = null; // yyyy-MM-dd
    private String dateTo = null;   // yyyy-MM-dd
    private final Set<String> labels = new LinkedHashSet<>(); // "rápido" | "médio" | "demorado"

    public SearchFilters copy() {
        SearchFilters c = new SearchFilters();
        c.text = this.text;
        c.page = this.page;
        c.pageSize = this.pageSize;
        c.operator = this.operator;
        c.phraseBoost = this.phraseBoost;
        c.fuzziness = this.fuzziness;
        c.highlight = this.highlight;
        c.sortMode = this.sortMode;
        c.readingTimeMin = this.readingTimeMin;
        c.readingTimeMax = this.readingTimeMax;
        c.dateFrom = this.dateFrom;
        c.dateTo = this.dateTo;
        c.labels.addAll(this.labels);
        return c;
    }

    public boolean hasActiveFilters() {
        return readingTimeMin != null || readingTimeMax != null
                || (dateFrom != null && !dateFrom.isBlank())
                || (dateTo != null && !dateTo.isBlank())
                || !labels.isEmpty();
    }

    // Getters / setters

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? "" : text;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(1, page);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public boolean isPhraseBoost() {
        return phraseBoost;
    }

    public void setPhraseBoost(boolean phraseBoost) {
        this.phraseBoost = phraseBoost;
    }

    public String getFuzziness() {
        return fuzziness;
    }

    public void setFuzziness(String fuzziness) {
        this.fuzziness = fuzziness;
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public SortMode getSortMode() {
        return sortMode;
    }

    public void setSortMode(SortMode sortMode) {
        this.sortMode = sortMode;
    }

    public Integer getReadingTimeMin() {
        return readingTimeMin;
    }

    public void setReadingTimeMin(Integer readingTimeMin) {
        this.readingTimeMin = readingTimeMin;
    }

    public Integer getReadingTimeMax() {
        return readingTimeMax;
    }

    public void setReadingTimeMax(Integer readingTimeMax) {
        this.readingTimeMax = readingTimeMax;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public Set<String> getLabels() {
        return labels;
    }
}
