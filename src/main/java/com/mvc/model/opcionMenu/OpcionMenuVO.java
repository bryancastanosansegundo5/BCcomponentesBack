package com.mvc.model.opcionMenu;

import com.mvc.model.rol.RolVO;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "opcion_menu")
public class OpcionMenuVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String ruta;

    @Column(nullable = false)
    private String ubicacion = "HEADER"; // HEADER o LATERAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolVO rol;

    public OpcionMenuVO() {}

    public OpcionMenuVO(String nombre, String ruta, RolVO rol, String ubicacion) {
        this.nombre = nombre;
        this.ruta = ruta;
        this.rol = rol;
        this.ubicacion = ubicacion;
    }
}
