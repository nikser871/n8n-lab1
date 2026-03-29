package com.examine.service;

import com.examine.dto.NewsDto;
import com.examine.mapper.NewsMapper;
import com.examine.repository.NewsRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Сервис для работы с новостями
 */
@Singleton
@RequiredArgsConstructor
public class NewsService {

    private final NewsMapper mapper;
    private final NewsRepository repository;

    @Transactional(readOnly = true)
    public List<NewsDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }
}
