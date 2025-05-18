package com.mvc.model.rol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mvc.model.opcionMenu.OpcionMenuVO;
import com.mvc.model.usuario.UsuarioVO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "rol")
public class RolVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    
    @JsonIgnore
    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private List<UsuarioVO> usuarios;

    @JsonIgnore
    @OneToMany(mappedBy = "rol", fetch = FetchType.EAGER)
    private List<OpcionMenuVO> opcionesMenu;
}
