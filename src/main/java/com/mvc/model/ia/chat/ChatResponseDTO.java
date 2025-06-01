package com.mvc.model.ia.chat;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
    private String respuesta;
    private List<Long> productos_agregar; 
    private List<Long> productos_quitar; 
}
