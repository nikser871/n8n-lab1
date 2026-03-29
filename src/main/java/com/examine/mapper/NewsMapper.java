package com.examine.mapper;

import com.examine.dto.NewsDto;
import com.examine.entity.News;
import jakarta.inject.Singleton;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jsr330")
@Singleton
public interface NewsMapper {


    @Mapping(source = "id", target = "articleId")
    NewsDto toDto(News entity);

    @Mapping(source = "articleId", target = "id")
    News toEntity(NewsDto entity);

}
