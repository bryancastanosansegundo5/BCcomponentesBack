package com.mvc.model.carrito;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoDTO {
    private Long usuarioId;
    private Long productoId;
    private int cantidad;
}
