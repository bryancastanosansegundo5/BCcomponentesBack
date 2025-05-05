package com.mvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Esta clase configura el canal de comunicación en tiempo real entre el servidor y el frontend. 
    // Sirve para que, por ejemplo, el servidor pueda enviar avisos de stock o 
    // notificaciones al instante sin que el usuario tenga que recargar la página.


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // STOMP: protcolo para mensajes
        // 🔧 Registra los puntos de entrada que usarán los clientes para conectarse

        registry.addEndpoint("/stock-alert") // 🔌 Define el endpoint de conexión WebSocket accesible en /stock-alert
                .setAllowedOrigins("http://localhost:5173")      // 🌐 Permite conexiones desde cualquier dominio (se puede restringir por seguridad)
                .withSockJS();               // 🔁 Habilita soporte para SockJS (compatibilidad con navegadores antiguos)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 📦 Configura el broker de mensajes (gestión de canales de comunicación)

        registry.enableSimpleBroker("/avisos"); 
        // 📤 Activa un broker simple en memoria que usará rutas que empiecen por /avisos para enviar mensajes a los clientes

        registry.setApplicationDestinationPrefixes("/app"); 
        // 📥 Define el prefijo que los clientes deben usar al enviar mensajes al servidor (entrada)
    }
}
