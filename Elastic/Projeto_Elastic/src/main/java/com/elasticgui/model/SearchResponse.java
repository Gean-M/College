package com.elasticgui.model;

import java.util.List;

/**
 * Resposta completa de uma busca: itens da página atual, informações de
 * paginação (calculadas com a mesma fórmula ensinada em aula:
 * total_paginas = teto(total_resultados / tamanho_pagina)) e, quando não há
 * nenhum resultado, sugestões de correção (mecanismo de "suggest" do ES).
 */
public class SearchResponse {

    private final List<SearchResultItem> items;
    private final long totalHits;
    private final int totalPages;
    private final int currentPage;
    private final int pageSize;
    private final long tookMillis;
    private final List<String> suggestions;
    private final String rawJson;

    public SearchResponse(List<SearchResultItem> items, long totalHits, int totalPages, int currentPage,
                           int pageSize, long tookMillis, List<String> suggestions, String rawJson) {
        this.items = items;
        this.totalHits = totalHits;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.tookMillis = tookMillis;
        this.suggestions = suggestions;
        this.rawJson = rawJson;
    }

    public List<SearchResultItem> getItems() {
        return items;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTookMillis() {
        return tookMillis;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public String getRawJson() {
        return rawJson;
    }
}
