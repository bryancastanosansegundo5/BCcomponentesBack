package com.mvc.model.usuario;

import com.mvc.model.rol.RolDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioEditarDTO {

    private Long id;

    private String nombre;

    private String apellidos;

    @Email(message = "Formato de email inválido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    private String telefono;

    private String direccion;
    private String poblacion;
    private String provincia;
    private String pais;
    private String codigoPostal;

    private boolean baja;

    private RolDTO rol;

    private String nuevaClave;
    private String repetirClave;
}
