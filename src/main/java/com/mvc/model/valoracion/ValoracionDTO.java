package com.mvc.model.valoracion;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValoracionDTO {
    private Long productoId;
    private int puntuacion;
    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    private String comentario;
    private Long usuarioId;
    private boolean visible;
    private Long detalleId;

}
