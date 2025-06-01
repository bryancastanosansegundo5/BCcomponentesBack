package com.mvc.model.pedido;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionEnvioDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El país es obligatorio")
    private String pais;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 9, max = 15)
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    private String adicionales;

    @NotBlank(message = "El código postal es obligatorio")
    private String codigoPostal;

    @NotBlank(message = "La población es obligatoria")
    private String poblacion;

    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;

    private Boolean guardarComoPredeterminada;
}
