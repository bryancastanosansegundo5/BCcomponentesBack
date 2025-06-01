package com.mvc.model.descuento;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DescuentoDTO {
    private String codigo;
    private double porcentaje;
    private boolean activo;
}