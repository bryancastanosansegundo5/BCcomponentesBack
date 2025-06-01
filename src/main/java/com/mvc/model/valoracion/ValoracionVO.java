package com.mvc.model.valoracion;

import com.mvc.model.usuario.UsuarioVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mvc.model.detalle.DetalleVO;
import com.mvc.model.producto.ProductoVO;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
    name = "valoracion",
    uniqueConstraints = @UniqueConstraint(columnNames = {"detalle_id"})
)
public class ValoracionVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioVO usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoVO producto;

    private int puntuacion;

    @Column(length = 500)
    private String comentario;

    private boolean visible;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_id", nullable = false)
    private DetalleVO detalle;
}
