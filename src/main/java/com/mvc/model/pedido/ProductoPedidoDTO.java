package com.mvc.model.pedido;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPedidoDTO {

    @NotNull
    private Long idProducto;

    @NotBlank
    private String nombre;

    @Positive
    private double precio;

    @Min(1)
    private int cantidad;

    @Positive
    private double subtotal;

    @NotBlank
    private String imagen;
}
