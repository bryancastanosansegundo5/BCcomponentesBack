package com.mvc.model.rol;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolDTO {
    @NotNull(message = "El id del rol es obligatorio")
    private int id;
    private String nombre;
}

