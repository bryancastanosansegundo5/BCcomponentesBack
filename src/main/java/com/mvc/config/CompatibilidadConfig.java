package com.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;

@Configuration
public class CompatibilidadConfig {

    @Bean
    public ChatClient chatClient(OpenAiChatModel model) {
        return ChatClient.builder(model).build();
    }
}
