package com.mvc.model.ia.compatibilidad;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompatibilidadResponseDTO {

    private String estado; // "compatible", "incompatible" o "faltante"
    private List<String> problemas; // Mensajes explicativos
    private List<Long> sugerencias; // IDs de productos sugeridos
}