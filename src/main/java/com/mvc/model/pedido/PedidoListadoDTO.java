package com.mvc.model.pedido;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoListadoDTO {
    private String numFactura;
    private LocalDateTime  fecha;
    private String estado;
    private double total;
        private LocalDate fechaEntregaUsuario;


    List<String> imagenesProductos;
}
