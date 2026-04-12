package com.examine.service;

import com.examine.dto.NewsForEnrichment;
import com.examine.dto.QwenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.micronaut.context.annotation.Value;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;
import static java.util.Objects.isNull;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class QwenService {

    private final NewsParser parser;

    @Value("${qwen.header}")
    private String HEADER_MESSAGE;

    @Value("${qwen.request}")
    private String REQUEST_MESSAGE;

    @Value("${open-router.api-key:''}")
    private String API_KEY;

    @Value("${open-router.model:''}")
    private String MODEL;

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Method for using AI to retrieve short digest of new
     * @param news news for ai enrichment (first attempt)
     * @return response from qwen model
     */
    public List<QwenResponse> getSummaries(List<NewsForEnrichment> news) {
//        List<String> texts = getAllArticlesTexts(news);

        log.info("model {}", MODEL);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<QwenResponse>> futures = news.stream()
                    .map(item -> executor.submit(() -> callQwenApi(item)))
                    .toList();

            return futures.parallelStream()
                    .map(future -> {
                        try {
                            return future.get(2, TimeUnit.MINUTES); // Здесь поток блокируется, но это "дешево" для Virtual Thread
                        } catch (Exception e) {
                            future.cancel(true);
                            e.printStackTrace();
                        }

                        return null;
                    })
                    .toList();
        }
//        StringBuilder sb = new StringBuilder(HEADER_MESSAGE);
//        for (String text : texts)
//            sb.append(text);
//
//        try {
//            return callQwenApi(sb.toString());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
    }

    private QwenResponse callQwenApi(NewsForEnrichment item) throws JsonProcessingException {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(API_KEY)
                .baseUrl("https://openrouter.ai/api/v1")
                .modelName(MODEL)
                .timeout(ofSeconds(120))
                .logResponses(true)
                .responseFormat("json_object")
                .build();
        String textFromParser = parser.getTextFromNew(item.link());

        if (isNull(textFromParser))
            return new QwenResponse(item.id(), null);

        String prompt = String.format(REQUEST_MESSAGE, item.id(), textFromParser);
        ChatRequest request = ChatRequest.builder()
                .messages(UserMessage.from(prompt))
                .build();

        ChatResponse response = model.chat(request);

        log.info("resonse: {}", response);

        String json = response.aiMessage().text();

        return mapper.readValue(json, QwenResponse.class);
    }

//    private List<String> getAllArticlesTexts(List<NewsForEnrichment> news) {
//        List<String> texts = new ArrayList<>();
//        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
//            List<Future<String>> futures = news.stream()
//                    .map(item -> executor.submit(() -> String.format(REQUEST_MESSAGE, item.id(), parser.getTextFromNew(item.link()))))
//                    .toList();
//
//            futures.forEach(ft -> {
//                try {
//                    texts.add(ft.get());
//                } catch (InterruptedException | ExecutionException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        }
//        return texts;
//    }

}
