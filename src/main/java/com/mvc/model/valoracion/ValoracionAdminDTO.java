package com.mvc.model.valoracion;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValoracionAdminDTO {
    private Long id; // id de la valoración
    private String producto;
    private String emailUsuario;
    private int puntuacion;
    private String comentario;
    private boolean visible;
    private String imagenProducto;
    private LocalDateTime fecha;


}
