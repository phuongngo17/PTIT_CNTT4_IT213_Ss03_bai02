package com.crm.bai02.controller;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class AiStreamController {

    private final ChatModel chatModel;

    public AiStreamController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping(
            value = "/api/v1/ai/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<String> getStreamResponse(
            @RequestParam String message) {

        return chatModel.stream(new Prompt(message))
                .map(ChatResponse::getResult)
                .map(result -> result.getOutput().getText())
                .filter(text -> text != null && !text.isBlank());
    }
}
