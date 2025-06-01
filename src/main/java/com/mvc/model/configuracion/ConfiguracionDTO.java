package com.mvc.model.configuracion;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracionDTO {
    private Long id;
    @NotBlank
    private String clave;

    @NotBlank
    private String valor;
}
