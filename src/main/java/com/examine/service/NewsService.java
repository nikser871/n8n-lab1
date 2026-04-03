package com.examine.service;

import com.examine.dto.NewsDto;
import com.examine.dto.NewsForEnrichment;
import com.examine.dto.QwenResponse;
import com.examine.entity.News;
import com.examine.mapper.NewsMapper;
import com.examine.repository.NewsRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.*;

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

    @Transactional
    public void enrichNewsWithSummaries() {
        List<NewsForEnrichment> newsForEnrichments = repository.findNewsForAIEnrichment();
        List<QwenResponse> responses = qwenService.getSummaries(newsForEnrichments);

        if (Objects.isNull(responses)) return;

        List<String> articleIds = new ArrayList<>();
        List<String> summaries = new ArrayList<>();

        for (var response : responses) {
            articleIds.add(response.articleId());
            summaries.add(response.summary());
        }

//        repository.batchUpdateWithDifferentSummaries(articleIds, summaries);
    }

}
