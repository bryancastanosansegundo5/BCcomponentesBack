package com.mvc.model.detalle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mvc.model.pedido.PedidoVO;
import com.mvc.model.producto.ProductoVO;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "detalle")
public class DetalleVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el pedido
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoVO pedido;

    // Relación con el producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoVO producto;

    // Número de unidades compradas
    private int unidades;

    // Precio unitario del producto en el momento de la compra
    @Column(name = "precio_unidad")
    private double precioUnidad;

    // Impuesto aplicado al producto
    private double impuesto;

    // Total de esta línea (precioUnidad * unidades + impuesto si aplica)
    private double total;
}
