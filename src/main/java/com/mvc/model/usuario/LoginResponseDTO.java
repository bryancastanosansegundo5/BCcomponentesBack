package com.mvc.model.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    //no requiere validaciones adicionales, ya que los datos han pasado por validación 
    private Long id;           // ID del usuario
    private String nombre;     // Nombre del usuario
    private String apellidos;  // Apellidos del usuario
    private String email;      // Email del usuario
    private int rolId;         // ID del rol del usuario (por ejemplo: cliente, administrador)
    private String token; 
    private String errorMessage;
}
