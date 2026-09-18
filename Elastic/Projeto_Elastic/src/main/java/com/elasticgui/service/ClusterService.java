package com.elasticgui.service;

import com.elasticgui.client.ElasticsearchClient;
import com.elasticgui.client.EsException;
import com.elasticgui.json.Json;

import java.util.List;
import java.util.Map;

/**
 * Serviço com as consultas administrativas mostradas logo na primeira aula:
 *   GET /_cat/health?v
 *   GET /_cat/nodes?v
 *   GET /_cat/indices?v
 */
public class ClusterService {

    private final ElasticsearchClient client;

    public ClusterService(ElasticsearchClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> health() throws EsException {
        String raw = client.getRaw("/_cat/health?format=json");
        return (List<Map<String, Object>>) (List<?>) Json.asList(Json.parse(raw));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> nodes() throws EsException {
        String raw = client.getRaw("/_cat/nodes?format=json");
        return (List<Map<String, Object>>) (List<?>) Json.asList(Json.parse(raw));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> indices() throws EsException {
        String raw = client.getRaw("/_cat/indices?format=json");
        return (List<Map<String, Object>>) (List<?>) Json.asList(Json.parse(raw));
    }
}
