package com.examine.service;

import com.examine.util.ParserUtil;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;



/**
 * Service for parsing the body from new
 */
@Singleton
@RequiredArgsConstructor
public class NewsParser {

    private final HttpClient client;

    public String getTextFromNew(String url) {
        BlockingHttpClient blockingClient = client.toBlocking();

        // Добавляем заголовки браузера
        HttpRequest<?> request = HttpRequest.GET(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1");

        try {
            String response = blockingClient.retrieve(request);
            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response from " + url);
            }
            return ParserUtil.extractBodyText(response);
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке " + url + ": " + e.getMessage());
            throw e;
        }
    }
}
