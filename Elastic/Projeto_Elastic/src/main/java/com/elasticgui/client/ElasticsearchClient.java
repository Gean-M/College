package com.elasticgui.client;

import com.elasticgui.config.AppConfig;
import com.elasticgui.json.Json;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

/**
 * Cliente HTTP simples para conversar diretamente com a API REST do
 * Elasticsearch (mesmos endpoints usados em aula: /_search, /_cat/health,
 * /_cat/nodes, /_cat/indices, etc.), usando apenas classes do JDK
 * (java.net.http.HttpClient). Assim a aplicação não depende de nenhuma
 * biblioteca externa nem precisa de acesso a repositórios Maven/Docker
 * para compilar ou rodar.
 *
 * A confiança em certificados autoassinados (trustAllCerts) reproduz o
 * comportamento do README do projeto original ("SSL configurado para
 * ignorar certificados não confiáveis - apenas para ambiente de dev")
 * e do curl --insecure usado no material de aula.
 */
public class ElasticsearchClient {

    private final AppConfig config;
    private final HttpClient httpClient;

    public ElasticsearchClient(AppConfig config) {
        this.config = config;
        this.httpClient = buildHttpClient(config);
    }

    private static HttpClient buildHttpClient(AppConfig config) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .version(HttpClient.Version.HTTP_1_1);
        if ("https".equalsIgnoreCase(config.getScheme()) && config.isTrustAllCerts()) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
                builder.sslContext(sslContext);
                SSLParameters sslParameters = new SSLParameters();
                sslParameters.setEndpointIdentificationAlgorithm(""); // desativa checagem de hostname
                builder.sslParameters(sslParameters);
            } catch (GeneralSecurityException e) {
                throw new IllegalStateException("Não foi possível preparar o SSLContext", e);
            }
        }
        return builder.build();
    }

    /** TrustManager que aceita qualquer certificado - somente para ambiente de desenvolvimento. */
    private static final class TrustAllManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private String authHeader() {
        String raw = config.getUsername() + ":" + config.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private HttpRequest.Builder baseRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(config.baseUrl() + path))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", authHeader())
                .header("Accept", "application/json");
    }

    /** Executa a requisição e devolve o corpo já convertido em Map/List (via Json.parse). */
    private Map<String, Object> execAsMap(HttpRequest request) throws EsException {
        String body = execAsString(request);
        Object parsed = body.isBlank() ? Map.of() : Json.parse(body);
        return Json.asMap(parsed) != null ? Json.asMap(parsed) : Map.of("value", parsed);
    }

    /** Executa a requisição e devolve o corpo bruto (usado para respostas em formato de lista, ex.: _cat). */
    public String execAsString(HttpRequest request) throws EsException {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                throw new EsException(status, response.body(), extractErrorMessage(status, response.body()));
            }
            return response.body();
        } catch (IOException e) {
            throw new EsException("Falha de conexão com " + config.baseUrl() +
                    " (verifique se o Elasticsearch está no ar e se host/porta/usuário/senha estão corretos): "
                    + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EsException("Requisição interrompida", e);
        }
    }

    private String extractErrorMessage(int status, String body) {
        try {
            Map<String, Object> parsed = Json.asMap(Json.parse(body));
            if (parsed != null) {
                Map<String, Object> error = Json.asMap(parsed.get("error"));
                if (error != null && error.get("reason") != null) {
                    return "HTTP " + status + ": " + error.get("reason");
                }
            }
        } catch (RuntimeException ignored) {
            // corpo não é JSON válido; cai no fallback abaixo
        }
        return "HTTP " + status + ": " + body;
    }

    // ---------------------------------------------------------------
    // Verbos HTTP
    // ---------------------------------------------------------------

    public Map<String, Object> get(String path) throws EsException {
        return execAsMap(baseRequest(path).GET().build());
    }

    public String getRaw(String path) throws EsException {
        return execAsString(baseRequest(path).GET().build());
    }

    public Map<String, Object> post(String path, Object bodyObj) throws EsException {
        String jsonBody = Json.stringify(bodyObj);
        HttpRequest request = baseRequest(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();
        return execAsMap(request);
    }

    public Map<String, Object> put(String path, Object bodyObj) throws EsException {
        String jsonBody = Json.stringify(bodyObj);
        HttpRequest request = baseRequest(path)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();
        return execAsMap(request);
    }

    public Map<String, Object> delete(String path) throws EsException {
        HttpRequest request = baseRequest(path).DELETE().build();
        return execAsMap(request);
    }

    /** GET /  - usado para "Testar conexão" (retorna nome do cluster, versão, etc.). */
    public Map<String, Object> ping() throws EsException {
        return get("/");
    }
}
