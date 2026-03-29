package com.examine.controller;

import com.examine.dto.NewsDto;
import com.examine.service.NewsService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService service;

    @Get
    public HttpResponse<List<NewsDto>> getAll() {
        var result = service.findAll();
        return HttpResponse.ok(result);
    }
}
