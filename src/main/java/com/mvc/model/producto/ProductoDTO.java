package com.mvc.model.producto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    Long id;
    @NotBlank
    private String nombre;

    private String descripcion;

    private String caracteristicas;

    @Positive
    private double precio;

    @Min(0)
    private int stock;

    private double impuesto;

    private boolean baja;

    private String socket;

    private String tipoRam;

    private String pcie;

    private Integer potenciaW;
    private Integer consumo;

    private String chipset;
    private int vendidos;
    private double valoracionMedia;
    private int totalValoraciones;
    
    private String factorForma; // Nuevo campo para placas base

    @NotNull
    private Long categoriaId;

    @NotBlank
    private String imagen;

    @NotNull
    private Long proveedorId; // ✅ NUEVO: para vincular proveedor
}
