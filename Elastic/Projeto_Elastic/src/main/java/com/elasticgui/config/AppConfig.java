package com.elasticgui.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Configuração de conexão com o cluster Elasticsearch.
 *
 * Os valores padrão reproduzem exatamente o ambiente usado em aula
 * (docker.zip / README do repositório de exemplo):
 *   host: localhost   porta: 9200   esquema: https
 *   usuário: elastic  senha: user123
 *   índice: wikipedia
 *   certificado autoassinado (ignorar validação em ambiente de dev)
 */
public class AppConfig {

    private String scheme = "https";
    private String host = "localhost";
    private int port = 9200;
    private String username = "elastic";
    private String password = "user123";
    private String indexName = "wikipedia";
    private boolean trustAllCerts = true;
    private int pageSize = 10;

    private static final Path CONFIG_DIR =
            Path.of(System.getProperty("user.home"), ".elastic-swing-gui");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.properties");

    public static AppConfig loadOrDefault() {
        AppConfig config = new AppConfig();
        if (Files.exists(CONFIG_FILE)) {
            try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
                Properties p = new Properties();
                p.load(in);
                config.scheme = p.getProperty("scheme", config.scheme);
                config.host = p.getProperty("host", config.host);
                config.port = Integer.parseInt(p.getProperty("port", String.valueOf(config.port)));
                config.username = p.getProperty("username", config.username);
                config.password = p.getProperty("password", config.password);
                config.indexName = p.getProperty("indexName", config.indexName);
                config.trustAllCerts = Boolean.parseBoolean(
                        p.getProperty("trustAllCerts", String.valueOf(config.trustAllCerts)));
                config.pageSize = Integer.parseInt(p.getProperty("pageSize", String.valueOf(config.pageSize)));
            } catch (IOException | NumberFormatException ex) {
                // Se o arquivo estiver corrompido, seguimos com os padrões.
            }
        }
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            Properties p = new Properties();
            p.setProperty("scheme", scheme);
            p.setProperty("host", host);
            p.setProperty("port", String.valueOf(port));
            p.setProperty("username", username);
            p.setProperty("password", password);
            p.setProperty("indexName", indexName);
            p.setProperty("trustAllCerts", String.valueOf(trustAllCerts));
            p.setProperty("pageSize", String.valueOf(pageSize));
            try (OutputStream out = Files.newOutputStream(CONFIG_FILE)) {
                p.store(out, "Configuração - Elasticsearch Swing GUI");
            }
        } catch (IOException ex) {
            // Falha ao salvar não deve travar a aplicação; o usuário pode tentar de novo.
        }
    }

    public String baseUrl() {
        return scheme + "://" + host + ":" + port;
    }

    // Getters / setters

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public boolean isTrustAllCerts() {
        return trustAllCerts;
    }

    public void setTrustAllCerts(boolean trustAllCerts) {
        this.trustAllCerts = trustAllCerts;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
