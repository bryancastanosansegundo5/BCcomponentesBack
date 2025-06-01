package com.mvc.model.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "El email es obligatorio") // Asegura que el campo email no esté vacío
    @Email(message = "El email debe tener un formato válido") // Verifica que el email esté en formato correcto
    private String email;

    @NotBlank(message = "La contraseña es obligatoria") // Asegura que el campo clave no esté vacío
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") // La contraseña debe tener mínimo 8
                                                                               // caracteres
    private String clave;

    private String nombre;
    private String apellidos;
    private String direccion;
    private int rolId; // ID del rol asignado al usuario
    private String titularTarjeta;
    private String numeroTarjeta;
    private String fechaExpiracion;
    private String codigoSeguridad;
    private String poblacion;
    private String provincia;
    private String pais;
    private String telefono;
    private String codigoPostal;

}
