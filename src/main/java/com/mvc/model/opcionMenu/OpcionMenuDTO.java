package com.mvc.model.opcionMenu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpcionMenuDTO {
    private String nombre;
    private String ruta;
    private int rolId;
    private String ubicacion;
}
