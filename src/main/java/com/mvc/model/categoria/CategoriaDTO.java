package com.mvc.model.categoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDTO {
    private Long id;
    private String nombre;
    private boolean baja;
}
