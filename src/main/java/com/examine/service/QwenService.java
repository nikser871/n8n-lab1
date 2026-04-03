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
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Singleton
@RequiredArgsConstructor
public class QwenService {

    private final NewsParser parser;

    @Value("${qwen.request}")
    private String REQUEST_MESSAGE;

    @Value("${open-router.api-key}")
    private String API_KEY;

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Method for using AI to retrieve short digest of new
     * @param news news for ai enrichment (first attempt)
     * @return response from qwen model
     */
    public List<QwenResponse> getSummaries(List<NewsForEnrichment> news) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<QwenResponse>> futures = news.stream()
                    .map(item -> executor.submit(() -> callQwenApi(item)))
                    .toList();

            return futures.stream()
                    .map(future -> {
                        try {
                            return future.get(); // Здесь поток блокируется, но это "дешево" для Virtual Thread
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    })
                    .toList();
        }
    }

    private QwenResponse callQwenApi(NewsForEnrichment item) throws JsonProcessingException {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(API_KEY)
                .baseUrl("https://openrouter.ai/api/v1")
                .modelName("qwen/qwen3.6-plus:free")
                .responseFormat("json_object")
                .build();

        String prompt = String.format(REQUEST_MESSAGE, item.id(), parser.getTextFromNew(item.link()));
        ChatRequest request = ChatRequest.builder()
                .messages(UserMessage.from(prompt))
                .build();

        ChatResponse response = model.chat(request);

        String json = response.aiMessage().text();

        return mapper.readValue(json, QwenResponse.class);
    }

}
