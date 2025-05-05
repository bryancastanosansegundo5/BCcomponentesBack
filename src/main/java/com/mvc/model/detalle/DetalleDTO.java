package com.mvc.model.detalle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleDTO {
    private Long pedidoId;
    private Long productoId;
    private int unidades;
    private double preciounidad;
    private double impuesto;
    private double total;
}
