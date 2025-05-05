package com.mvc.model.valoracion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValoracionDTO {
    private Long productoId;
    private int puntuacion;
    private String comentario;
    private Long usuarioId;
    private boolean visible;
}
