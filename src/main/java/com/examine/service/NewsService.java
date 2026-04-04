package com.examine.service;

import com.examine.dto.NewsDto;
import com.examine.dto.NewsForEnrichment;
import com.examine.dto.QwenResponse;
import com.examine.mapper.NewsMapper;
import com.examine.repository.NewsRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static java.util.Objects.isNull;

/**
 * Сервис для работы с новостями
 */
@Singleton
@RequiredArgsConstructor
public class NewsService {

    private final NewsMapper mapper;
    private final NewsRepository repository;
    private final QwenService qwenService;

    @Transactional(readOnly = true)
    public List<NewsDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public void enrichNewsWithSummaries() {
        List<NewsForEnrichment> newsForEnrichments = repository.findNewsForAIEnrichment();
        List<QwenResponse> responses = qwenService.getSummaries(newsForEnrichments);

        if (isNull(responses)) return;

        saveAiResults(responses);
    }

    @Transactional
    protected void saveAiResults(List<QwenResponse> responses) {
        for (var response : responses) {
            if (response != null && response.summary() != null) {
                repository.updateWithSummary(response.articleId(), response.summary());
            }
        }
    }

}
