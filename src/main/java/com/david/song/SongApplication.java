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
            URI databaseUri = URI.create(rawUrl);
            String host = databaseUri.getHost();
            String path = databaseUri.getPath();

            if (host != null && path != null && !path.isBlank()) {
                StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                        .append(host);

                if (databaseUri.getPort() > 0) {
                    jdbcUrl.append(":").append(databaseUri.getPort());
                }

                jdbcUrl.append(path);
                System.setProperty("spring.datasource.url", jdbcUrl.toString());
            }
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