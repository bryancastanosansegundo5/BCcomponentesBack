package com.mvc.model.producto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mvc.model.carrito.CarritoVO;
import com.mvc.model.categoria.CategoriaVO;
import com.mvc.model.detalle.DetalleVO;
import com.mvc.model.proovedor.ProveedorVO;
import com.mvc.model.valoracion.ValoracionVO;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "producto")
public class ProductoVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @NotBlank
    private String imagen;

    private String socket;

    @Column(name = "tipo_ram")
    private String tipoRam;

    private String pcie;

    private Integer potenciaW;
    private Integer consumo; 

    private String chipset;
    private int vendidos;
    private int totalValoraciones;
    private double valoracionMedia;

    @Column(name = "factor_forma")
    private String factorForma; // ATX, Micro-ATX, Mini-ITX...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonIgnore
    @NotNull
    private CategoriaVO categoria;

    @JsonIgnore
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<DetalleVO> detalles;

    @JsonIgnore
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ValoracionVO> valoraciones;

    @JsonIgnore
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<CarritoVO> carritos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    @JsonIgnore
    private ProveedorVO proveedor;

}
