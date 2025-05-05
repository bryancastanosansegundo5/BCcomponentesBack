package com.mvc.model.usuario;

import lombok.Data;

@Data
public class CambioClaveDTO {
    private Long idUsuario;
    private String claveActual;
    private String nuevaClave;
    private String repetirNuevaClave;
}
