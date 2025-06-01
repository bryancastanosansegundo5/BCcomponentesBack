package com.mvc.model.ia.chat;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {
    private String mensaje;
    private List<Long> productosRecomendados;
}
