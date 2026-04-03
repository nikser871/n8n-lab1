package com.examine.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record QwenResponse(@JsonAlias("id") String articleId,
                           String summary) {}
