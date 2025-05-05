package com.mvc.model.ia.compatibilidad;

import java.util.List;

import com.mvc.model.producto.ProductoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompatibilidadRequestDTO {

    private List<Long> productos;

   
}
