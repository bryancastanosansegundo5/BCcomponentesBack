package com.mvc.model.valoracion;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisibilidadValoracionDTO {
     private boolean visible;
}
