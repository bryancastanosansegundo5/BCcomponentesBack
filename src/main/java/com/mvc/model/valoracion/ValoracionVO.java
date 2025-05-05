package com.mvc.model.valoracion;

import com.mvc.model.usuario.UsuarioVO;
import com.mvc.model.producto.ProductoVO;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "valoracion")
public class ValoracionVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioVO usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoVO producto;

    private int puntuacion;

    private String comentario;

    private boolean visible;
}
