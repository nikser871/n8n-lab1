package com.examine.service;

import com.examine.dto.NewsDto;
import com.examine.dto.NewsForEnrichment;
import com.examine.dto.QwenResponse;
import com.examine.entity.enums.EnrichmentStatus;
import com.examine.entity.enums.NewsType;
import com.examine.mapper.NewsMapper;
import com.examine.repository.NewsRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static com.examine.entity.enums.NewsType.PRICED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Сервис для работы с новостями
 */
@Singleton
@RequiredArgsConstructor
public class NewsService {

    private final NewsMapper mapper;
    private final NewsRepository repository;
    private final QwenService qwenService;
    private static final String PRICED_DOMEN = "pro.rbc.ru";

    @Transactional(readOnly = true)
    public List<NewsDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public void enrichNewsWithSummaries() {
        updateNewsType();
        List<NewsForEnrichment> newsToEnrich = lockNewsForProcessing();

        if (newsToEnrich.isEmpty()) return;

        List<QwenResponse> responses = qwenService.getSummaries(newsToEnrich);

        if (isNull(responses)) return;

        saveAiResults(responses);
    }

    @Transactional
    protected void saveAiResults(List<QwenResponse> responses) {
        for (var response : responses) {
            if (response != null && response.summary() != null) {
                repository.updateWithSummary(response.articleId(), response.summary());
            } else if (response != null && response.articleId() != null){
                repository.updateStatus(List.of(response.articleId()), EnrichmentStatus.COMPLETED);
            }
        }
        repository.updateOrphanNews();
    }

    @Transactional
    public List<NewsForEnrichment> lockNewsForProcessing() {
        List<NewsForEnrichment> toProcess = repository.findNewsForAIEnrichment();

        if (toProcess.isEmpty()) return Collections.emptyList();

        List<String> ids = toProcess.stream().map(NewsForEnrichment::id).toList();
        repository.updateStatus(ids, EnrichmentStatus.IN_PROGRESS);

        return toProcess;
    }

    @Transactional
    public void updateNewsType() {
        var news = repository.findByStatus(EnrichmentStatus.NEW);
        var pricedNews = news.stream()
                .filter(article -> nonNull(article.getLink()) && article.getLink().contains(PRICED_DOMEN))
                .toList();
        pricedNews.forEach(vip -> vip.setType(PRICED));
        repository.saveAll(pricedNews);
    }

}
