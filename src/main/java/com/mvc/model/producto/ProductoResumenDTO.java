package com.mvc.model.producto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResumenDTO {
    private Long id;
    private Long detalleId;
    private int cantidad;
    private boolean yaValorado;
}
