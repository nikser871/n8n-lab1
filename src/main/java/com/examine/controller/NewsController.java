package com.examine.controller;

import com.examine.dto.NewsDto;
import com.examine.service.NewsParser;
import com.examine.service.NewsService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService service;
    private final NewsParser parser;

    @Get
    public HttpResponse<List<NewsDto>> getAll() {
        var result = service.findAll();
        return HttpResponse.ok(result);
    }

    @Post("/ai/enrich")
    public HttpResponse<Void> enrichDataByAI() {
        service.enrichNewsWithSummaries();
        return HttpResponse.ok();
    }

    @Post("parser/by-url")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<String> getTextByParser(String link) {
        return HttpResponse.ok(parser.getTextFromNew(link));
    }
}
