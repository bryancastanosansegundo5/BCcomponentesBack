package com.mvc.model.usuario;

import com.mvc.model.rol.RolDTO;

import lombok.Data;

@Data
public class UsuarioListadoDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String direccion;
    private String poblacion;
    private String provincia;
    private String pais;
    private String codigoPostal;
    private boolean baja;
    private int pedidos;
    private RolDTO rol;

}
