package com.elasticgui.client;

/**
 * Exceção lançada quando o Elasticsearch responde com um status de erro
 * (ex.: index_not_found_exception, security_exception por credenciais
 * inválidas, mapper_parsing_exception, etc.).
 */
public class EsException extends Exception {

    private final int statusCode;
    private final String rawBody;

    public EsException(int statusCode, String rawBody, String message) {
        super(message);
        this.statusCode = statusCode;
        this.rawBody = rawBody;
    }

    public EsException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.rawBody = null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getRawBody() {
        return rawBody;
    }
}
