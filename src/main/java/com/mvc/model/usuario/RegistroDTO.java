package com.mvc.model.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistroDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String clave;

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmarClave;

    // Nuevos campos opcionales
    private String nombre;
    private String apellidos;
}
