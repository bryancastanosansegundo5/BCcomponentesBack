package com.mvc.model.proovedor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorDTO {
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private boolean baja;
}