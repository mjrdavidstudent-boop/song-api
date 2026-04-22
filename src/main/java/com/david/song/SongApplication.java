package com.david.song;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;

@SpringBootApplication
public class SongApplication {

    public static void main(String[] args) {
        configureDatasourceUrl();
        SpringApplication.run(SongApplication.class, args);
    }

    private static void configureDatasourceUrl() {
        String rawUrl = firstNonBlank(System.getenv("DATABASE_URL"), System.getenv("DB_HOST"));

        if (rawUrl == null || rawUrl.isBlank()) {
            return;
        }

        if (rawUrl.startsWith("jdbc:")) {
            System.setProperty("spring.datasource.url", rawUrl);
            return;
        }

        if (rawUrl.startsWith("postgresql://") || rawUrl.startsWith("postgres://")) {
            configureFromDatabaseUri(rawUrl);
        }
    }

    private static void configureFromDatabaseUri(String rawUrl) {
        URI databaseUri = URI.create(rawUrl);
        String host = databaseUri.getHost();
        String path = databaseUri.getPath();

        if (host == null || path == null || path.isBlank()) {
            return;
        }

        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                .append(host);

        if (databaseUri.getPort() > 0) {
            jdbcUrl.append(":").append(databaseUri.getPort());
        }

        jdbcUrl.append(path);

        String query = databaseUri.getQuery();
        if (query != null && !query.isBlank()) {
            jdbcUrl.append("?").append(query);
        }

        System.setProperty("spring.datasource.url", jdbcUrl.toString());

        // Render connection strings can include credentials in the URI user-info section.
        String userInfo = databaseUri.getUserInfo();
        if (userInfo == null || userInfo.isBlank()) {
            return;
        }

        String[] credentials = userInfo.split(":", 2);
        if (!credentials[0].isBlank()) {
            System.setProperty("spring.datasource.username", credentials[0]);
        }

        if (credentials.length > 1 && !credentials[1].isBlank()) {
            System.setProperty("spring.datasource.password", credentials[1]);
        }
    }

    private static String firstNonBlank(String firstValue, String secondValue) {
        if (firstValue != null && !firstValue.isBlank()) {
            return firstValue;
        }

        if (secondValue != null && !secondValue.isBlank()) {
            return secondValue;
        }

        return null;
    }

}