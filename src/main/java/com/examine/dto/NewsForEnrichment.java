package com.examine.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record NewsForEnrichment(String id,
                                String link) {
}
