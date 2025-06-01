package com.mvc.model.producto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoListadoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String imagen;
    private Long categoriaId;
    private String categoriaNombre;
    private boolean activo; // Para bajas lógicas
    private double valoracionMedia; // Media de valoraciones
    private int vendidos; // Total de ventas del producto
    private Long proveedorId;
}
