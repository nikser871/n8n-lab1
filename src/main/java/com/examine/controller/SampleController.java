package com.examine.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/sample")
public class SampleController {

    @Get(produces = MediaType.TEXT_PLAIN)
    public String getSampleText() {
        return "This is a sample text from the new controller!";
    }
}