package com.mvc.model.descuento;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "descuento")
public class DescuentoVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    private double porcentaje;

    private boolean activo;
}
