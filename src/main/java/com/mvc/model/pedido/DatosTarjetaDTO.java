package com.mvc.model.pedido;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosTarjetaDTO {

    @NotBlank
    private String titular;

    @NotBlank
    @Pattern(regexp = "\\d{13,19}")
    private String numero;

    @NotBlank
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/\\d{2}$", message = "Formato inválido (MM/AA)")
    private String fechaExpiracion;

    @NotBlank
    @Size(min = 3, max = 4)
    private String codigoSeguridad;

    private boolean guardar;
}
