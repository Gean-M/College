package com.elasticgui.service;

import com.elasticgui.client.ElasticsearchClient;
import com.elasticgui.client.EsException;
import com.elasticgui.config.AppConfig;
import com.elasticgui.json.Json;
import com.elasticgui.model.SearchFilters;
import com.elasticgui.model.SearchResponse;
import com.elasticgui.model.SearchResultItem;
import com.elasticgui.model.StatsResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável por montar as consultas ao Elasticsearch a partir dos
 * filtros escolhidos na interface e por converter a resposta em objetos
 * simples que a interface Swing consegue exibir.
 *
 * Reproduz, com a API REST "crua", exatamente o que o SearchService do
 * projeto Spring Boot de referência faz: busca no campo "content", limpa o
 * conteúdo (equivalente ao campo "abs" do README) e pagina os resultados
 * usando from = (page - 1) * pageSize.
 */
public class SearchService {

    private final ElasticsearchClient client;
    private final AppConfig config;

    public SearchService(ElasticsearchClient client, AppConfig config) {
        this.client = client;
        this.config = config;
    }

    private static final List<String> SOURCE_FIELDS =
            List.of("title", "url", "content", "reading_time", "dt_creation", "label");

    // ---------------------------------------------------------------
    // Busca principal
    // ---------------------------------------------------------------

    public SearchResponse search(SearchFilters filters) throws EsException {
        int page = Math.max(1, filters.getPage());
        int pageSize = Math.max(1, filters.getPageSize());
        int from = (page - 1) * pageSize;

        Map<String, Object> body = buildQueryBody(filters, from, pageSize, true);
        Map<String, Object> response = client.post("/" + config.getIndexName() + "/_search", body);

        Map<String, Object> hits = Json.asMap(response.get("hits"));
        Map<String, Object> total = hits == null ? null : Json.asMap(hits.get("total"));
        long totalHits = total != null && total.get("value") != null ? Json.asLong(total.get("value")) : 0L;
        long took = response.get("took") != null ? Json.asLong(response.get("took")) : 0L;

        List<SearchResultItem> items = new ArrayList<>();
        List<Object> hitList = hits == null ? null : Json.asList(hits.get("hits"));
        if (hitList != null) {
            for (Object hitObj : hitList) {
                Map<String, Object> hit = Json.asMap(hitObj);
                if (hit == null) {
                    continue;
                }
                items.add(toResultItem(hit));
            }
        }

        int totalPages = totalHits == 0 ? 0 : (int) Math.ceil(totalHits / (double) pageSize);

        List<String> suggestions = null;
        if (totalHits == 0 && filters.getText() != null && !filters.getText().isBlank()) {
            suggestions = suggest(filters.getText());
        }

        String rawJson = Json.stringifyPretty(response);
        return new SearchResponse(items, totalHits, totalPages, page, pageSize, took, suggestions, rawJson);
    }

    private SearchResultItem toResultItem(Map<String, Object> hit) {
        Map<String, Object> source = Json.asMap(hit.get("_source"));
        if (source == null) {
            source = Map.of();
        }
        String title = Json.asString(source.get("title"));
        String url = Json.asString(source.get("url"));
        String content = Json.asString(source.get("content"));
        Integer readingTime = Json.asInteger(source.get("reading_time"));
        String dtCreation = Json.asString(source.get("dt_creation"));
        String label = Json.asString(source.get("label"));
        Double score = Json.asDouble(hit.get("_score"));

        String highlightHtml = null;
        Map<String, Object> highlight = Json.asMap(hit.get("highlight"));
        if (highlight != null) {
            List<Object> fragments = Json.asList(highlight.get("content"));
            if (fragments != null && !fragments.isEmpty()) {
                highlightHtml = Json.asString(fragments.get(0));
            }
        }

        String abstractText = cleanContent(content);
        return new SearchResultItem(title, url, abstractText, highlightHtml, readingTime, dtCreation, label, score);
    }

    /** Remove tags HTML e caracteres especiais, igual ao SearchService do projeto original. */
    public static String cleanContent(String content) {
        if (content == null) {
            return "";
        }
        String noTags = content.replaceAll("<[^>]+>", " ");
        String noSpecial = noTags.replaceAll("[^\\p{L}\\p{N}\\s.,;:!?'\"()\\-]", " ");
        String collapsed = noSpecial.replaceAll("\\s+", " ").trim();
        int maxLength = 350;
        if (collapsed.length() > maxLength) {
            return collapsed.substring(0, maxLength).trim() + "...";
        }
        return collapsed;
    }

    // ---------------------------------------------------------------
    // Construção da query
    // ---------------------------------------------------------------

    private Map<String, Object> buildQueryBody(SearchFilters filters, int from, int size, boolean includeHighlight) {
        Map<String, Object> matchParams = new LinkedHashMap<>();
        matchParams.put("query", filters.getText());
        if (filters.getOperator() == SearchFilters.Operator.AND) {
            matchParams.put("operator", "and");
        }
        if (filters.getFuzziness() != null && !filters.getFuzziness().isBlank()) {
            matchParams.put("fuzziness", filters.getFuzziness());
        }

        List<Object> must = Json.arr(Json.obj("match", Json.obj("content", matchParams)));

        List<Object> should = new ArrayList<>();
        if (filters.isPhraseBoost()) {
            should.add(Json.obj("match_phrase", Json.obj("content", filters.getText())));
        }

        List<Object> filter = buildFilterClauses(filters);

        Map<String, Object> bool = new LinkedHashMap<>();
        bool.put("must", must);
        if (!should.isEmpty()) {
            bool.put("should", should);
        }
        if (!filter.isEmpty()) {
            bool.put("filter", filter);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("from", from);
        body.put("size", size);
        body.put("_source", SOURCE_FIELDS);
        body.put("query", Json.obj("bool", bool));

        List<Object> sort = buildSort(filters);
        if (sort != null) {
            body.put("sort", sort);
        }

        if (includeHighlight && filters.isHighlight()) {
            body.put("highlight", Json.obj(
                    "pre_tags", Json.arr("<b>"),
                    "post_tags", Json.arr("</b>"),
                    "number_of_fragments", 1,
                    "fragment_size", 300,
                    "fields", Json.obj("content", Json.obj())
            ));
        }

        return body;
    }

    private List<Object> buildFilterClauses(SearchFilters filters) {
        List<Object> filter = new ArrayList<>();

        if (filters.getReadingTimeMin() != null || filters.getReadingTimeMax() != null) {
            Map<String, Object> range = new LinkedHashMap<>();
            if (filters.getReadingTimeMin() != null) {
                range.put("gte", filters.getReadingTimeMin());
            }
            if (filters.getReadingTimeMax() != null) {
                range.put("lte", filters.getReadingTimeMax());
            }
            filter.add(Json.obj("range", Json.obj("reading_time", range)));
        }

        boolean hasDateFrom = filters.getDateFrom() != null && !filters.getDateFrom().isBlank();
        boolean hasDateTo = filters.getDateTo() != null && !filters.getDateTo().isBlank();
        if (hasDateFrom || hasDateTo) {
            Map<String, Object> range = new LinkedHashMap<>();
            if (hasDateFrom) {
                range.put("gte", filters.getDateFrom());
            }
            if (hasDateTo) {
                range.put("lte", filters.getDateTo());
            }
            filter.add(Json.obj("range", Json.obj("dt_creation", range)));
        }

        if (!filters.getLabels().isEmpty()) {
            filter.add(Json.obj("terms", Json.obj("label", new ArrayList<>(filters.getLabels()))));
        }

        return filter;
    }

    private List<Object> buildSort(SearchFilters filters) {
        switch (filters.getSortMode()) {
            case LEITURA_ASC:
                return Json.arr(Json.obj("reading_time", Json.obj("order", "asc")),
                        Json.obj("_score", Json.obj("order", "desc")));
            case LEITURA_DESC:
                return Json.arr(Json.obj("reading_time", Json.obj("order", "desc")),
                        Json.obj("_score", Json.obj("order", "desc")));
            case DATA_ASC:
                return Json.arr(Json.obj("dt_creation", Json.obj("order", "asc")),
                        Json.obj("_score", Json.obj("order", "desc")));
            case DATA_DESC:
                return Json.arr(Json.obj("dt_creation", Json.obj("order", "desc")),
                        Json.obj("_score", Json.obj("order", "desc")));
            case RELEVANCIA:
            default:
                return null; // usa o BM25 padrão, sem cláusula "sort" explícita
        }
    }

    // ---------------------------------------------------------------
    // Suggest ("você quis dizer?")
    // ---------------------------------------------------------------

    public List<String> suggest(String text) throws EsException {
        Map<String, Object> body = Json.obj(
                "suggest", Json.obj(
                        "correcao", Json.obj(
                                "text", text,
                                "term", Json.obj("field", "content", "size", 1)
                        )
                )
        );
        Map<String, Object> response = client.post("/" + config.getIndexName() + "/_search", body);
        Map<String, Object> suggest = Json.asMap(response.get("suggest"));
        if (suggest == null) {
            return List.of();
        }
        List<Object> entries = Json.asList(suggest.get("correcao"));
        if (entries == null) {
            return List.of();
        }

        StringBuilder corrected = new StringBuilder();
        List<String> alternativeWords = new ArrayList<>();
        for (Object entryObj : entries) {
            Map<String, Object> entry = Json.asMap(entryObj);
            if (entry == null) {
                continue;
            }
            String original = Json.asString(entry.get("text"));
            List<Object> options = Json.asList(entry.get("options"));
            String replacement = original;
            if (options != null && !options.isEmpty()) {
                Map<String, Object> best = Json.asMap(options.get(0));
                if (best != null && best.get("text") != null) {
                    replacement = Json.asString(best.get("text"));
                    alternativeWords.add(replacement);
                }
            }
            if (corrected.length() > 0) {
                corrected.append(' ');
            }
            corrected.append(replacement);
        }

        if (alternativeWords.isEmpty()) {
            return List.of();
        }
        return List.of(corrected.toString());
    }

    // ---------------------------------------------------------------
    // Estatísticas (aggregations) sobre o conjunto filtrado
    // ---------------------------------------------------------------

    public StatsResult stats(SearchFilters filters) throws EsException {
        Map<String, Object> body = buildQueryBody(filters, 0, 0, false);
        body.put("aggs", Json.obj("stats_reading_time", Json.obj("stats", Json.obj("field", "reading_time"))));

        Map<String, Object> response = client.post("/" + config.getIndexName() + "/_search", body);
        Map<String, Object> aggs = Json.asMap(response.get("aggregations"));
        if (aggs == null) {
            return new StatsResult(0, null, null, null, null);
        }
        Map<String, Object> stats = Json.asMap(aggs.get("stats_reading_time"));
        if (stats == null) {
            return new StatsResult(0, null, null, null, null);
        }
        long count = stats.get("count") != null ? Json.asLong(stats.get("count")) : 0L;
        return new StatsResult(count, Json.asDouble(stats.get("min")), Json.asDouble(stats.get("max")),
                Json.asDouble(stats.get("avg")), Json.asDouble(stats.get("sum")));
    }
}
