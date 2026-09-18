package com.elasticgui.model;

/**
 * Um resultado de busca já tratado para exibição - equivalente ao objeto
 * "Result" (title, url, abs) devolvido pelo endpoint /search do projeto
 * original, acrescido dos campos extras do índice (reading_time, dt_creation,
 * label) e do trecho destacado (highlight) quando disponível.
 */
public class SearchResultItem {

    private final String title;
    private final String url;
    private final String abstractText;   // conteúdo limpo (sem tags HTML), como o campo "abs" do README
    private final String highlightHtml;  // trecho com <b>...</b> ao redor dos termos buscados (pode ser null)
    private final Integer readingTime;
    private final String dtCreation;
    private final String label;
    private final Double score;

    public SearchResultItem(String title, String url, String abstractText, String highlightHtml,
                             Integer readingTime, String dtCreation, String label, Double score) {
        this.title = title;
        this.url = url;
        this.abstractText = abstractText;
        this.highlightHtml = highlightHtml;
        this.readingTime = readingTime;
        this.dtCreation = dtCreation;
        this.label = label;
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public String getHighlightHtml() {
        return highlightHtml;
    }

    public Integer getReadingTime() {
        return readingTime;
    }

    public String getDtCreation() {
        return dtCreation;
    }

    public String getLabel() {
        return label;
    }

    public Double getScore() {
        return score;
    }
}
