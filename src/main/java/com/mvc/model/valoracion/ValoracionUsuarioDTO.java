package com.mvc.model.valoracion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValoracionUsuarioDTO {
    private Long productoId;
    private String imagenProducto;
    private String comentario;
    private String emailUsuario;
    private int puntuacion;
}
