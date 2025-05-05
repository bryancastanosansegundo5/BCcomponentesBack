package com.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;

@Configuration
public class CompatibilidadConfig {

    @Bean
    public ChatClient chatClient(OpenAiChatModel model) {
        // Crea y devuelve una instancia de ChatClient configurada con el modelo especificado
        return ChatClient.builder(model).build();
    }
}
