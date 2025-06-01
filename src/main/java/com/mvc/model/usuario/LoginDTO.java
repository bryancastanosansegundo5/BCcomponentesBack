package com.mvc.model.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "El email es obligatorio") // Asegura que el email no esté vacío
    @Email(message = "El email debe tener un formato válido") // Verifica que el email tenga formato correcto
    private String email;

    @NotBlank(message = "La contraseña es obligatoria") // Asegura que la contraseña no esté vacía
    private String clave;
}
