package com.mvc.service.ia.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.model.ia.chat.ChatRequestDTO;
import com.mvc.model.ia.chat.ChatResponseDTO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository productoRepository;

    @Value("classpath:templates/ChatPrompt.st")
    private Resource chatPromptTemplate;

    @Override
    public ChatResponseDTO procesarPregunta(ChatRequestDTO request) {
        try {
            System.out.println("🛠 Procesando pregunta del cliente: " + request.getMensaje());

            // 🔹 1. Obtener productos activos de la tienda
            List<ProductoVO> productosActivos = productoRepository.findByBajaFalse();

            // 🔹 2. Mapear a DTOs simples (solo lo que necesitemos enviar)
            List<Map<String, Object>> productosDisponibles = productosActivos.stream()
                    .map(producto -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", producto.getId());
                        map.put("nombre", producto.getNombre());
                        return map;
                    })
                    .toList();

            // 🔹 3. Serializara JSON disponibles y recomendados
            String disponiblesJson = objectMapper.writeValueAsString(productosDisponibles);
            String recomendadosJson = objectMapper.writeValueAsString(request.getProductosRecomendados());

            // 🔹 4. Leer y preparar el prompt
            String templateRaw = new String(chatPromptTemplate.getInputStream().readAllBytes());
            PromptTemplate promptTemplate = new PromptTemplate(templateRaw);

            Map<String, Object> variables = new HashMap<>();
            variables.put("productos_disponibles", disponiblesJson);
            variables.put("productos_recomendados", recomendadosJson);
            variables.put("mensaje_usuario", request.getMensaje());

            var prompt = promptTemplate.create(variables);

            // 🔹 5. Llamar a la IA
            String respuestaIA = chatClient.prompt(prompt).call().content();

            System.out.println("✅ Respuesta IA recibida:");
            System.out.println(respuestaIA);

            // 🔹 6. Devolver la respuesta como DTO
            return objectMapper.readValue(respuestaIA, ChatResponseDTO.class);

        } catch (Exception e) {
            System.out.println("Error procesando pregunta de compatibilidad: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error procesando pregunta de compatibilidad", e);
        }
    }
}
