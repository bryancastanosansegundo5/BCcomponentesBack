package com.mvc.model.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CambioClaveDTO {
    private Long idUsuario;
    @NotBlank(message = "La contraseña es obligatoria") 
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String claveActual;
    @NotBlank(message = "La contraseña es obligatoria") 
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String nuevaClave;
    @NotBlank(message = "La contraseña es obligatoria") 
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String repetirNuevaClave;
}
