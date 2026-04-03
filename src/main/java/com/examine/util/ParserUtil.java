package com.examine.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ParserUtil {

    public static String extractBodyText(String html) {
        // Извлекаем JSON из script
        Pattern pattern = Pattern.compile(
                "<script id=\"__NEXT_DATA__\" type=\"application/json\">(.*?)</script>",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            try {
                String jsonData = matcher.group(1);
                JsonNode root = new ObjectMapper().readTree(jsonData);

                // Путь к bodyText: props.pageProps.material.bodyText
                JsonNode bodyTextNode = root.at("/props/pageProps/material/bodyText");

                if (!bodyTextNode.isMissingNode()) {
                    return bodyTextNode.asText();
                }

                // Альтернативный путь: props.pageProps.material.bodyMd
                JsonNode bodyMdNode = root.at("/props/pageProps/material/bodyMd");
                if (!bodyMdNode.isMissingNode()) {
                    return bodyMdNode.asText();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
