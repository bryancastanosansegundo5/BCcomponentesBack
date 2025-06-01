package com.mvc.model.pedido;

import com.mvc.model.detalle.DetalleVO;
import com.mvc.model.enums.MetodoPago;
import com.mvc.model.enums.Transportista;
import com.mvc.model.usuario.UsuarioVO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "pedido")
public class PedidoVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioVO usuario;

    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;



    @Enumerated(EnumType.STRING)
    private Transportista transportista;

    @Column(name = "fecha_entrega_usuario")
    private LocalDate fechaEntregaUsuario;



    @Column(name = "num_factura", nullable = false, unique = true)
    private String numFactura;

    private double total;

    // ✅ Dirección de envío específica del pedido
    private String nombre;
    private String apellidos;
    private String direccion;
    private String adicionales; // opcional
    private String codigoPostal;
    private String poblacion;
    private String provincia;
    private String pais;
    private String telefono;
    
    @Column(nullable = false)
    private String estado;
    
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DetalleVO> detalles;
}
