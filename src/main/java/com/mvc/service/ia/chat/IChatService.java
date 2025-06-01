package com.mvc.service.ia.chat;

import com.mvc.model.ia.chat.ChatRequestDTO;
import com.mvc.model.ia.chat.ChatResponseDTO;

public interface IChatService {
 ChatResponseDTO procesarPregunta(ChatRequestDTO request);
}
