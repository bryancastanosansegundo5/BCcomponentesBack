package com.mvc.model.pedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentasMensualesDTO {
    private String mes;
    private double total;
}
