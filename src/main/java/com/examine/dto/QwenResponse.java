package com.examine.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.serde.annotation.Serdeable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Serdeable
public record QwenResponse(@JsonAlias("id") String articleId,
                           String summary) {}
