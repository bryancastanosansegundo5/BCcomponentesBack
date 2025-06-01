package com.mvc.model.usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mvc.model.carrito.CarritoVO;
import com.mvc.model.pedido.PedidoVO;
import com.mvc.model.rol.RolVO;
import com.mvc.model.valoracion.ValoracionVO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "usuario")
public class UsuarioVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String clave;

    private String nombre;
    private String apellidos;
    private String direccion;
    private String pais;
    private String telefono;
    private String codigoPostal;
    private String poblacion;
    private String provincia;

    private String titularTarjeta;
    private String numeroTarjeta;
    private String fechaExpiracion;
    private String codigoSeguridad;

    private boolean baja;
    @Column(name = "num_logins")
    private Integer numLogins = 0;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolVO rol;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<PedidoVO> pedidos;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<ValoracionVO> valoraciones;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<CarritoVO> carritos;
}
