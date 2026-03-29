package com.examine.dto;

import com.examine.dto.enums.Source;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;

/**
 * Метаданные о новости
 */
@Serdeable
public record NewsDto(String articleId,
                      LocalDateTime extractedAt,
                      String link,
                      Source source,
                      String title) {
}
