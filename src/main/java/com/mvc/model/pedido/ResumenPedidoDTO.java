package com.mvc.model.pedido;

import java.util.List;

import com.mvc.model.enums.MetodoPago;
import com.mvc.model.enums.Transportista;
import com.mvc.model.producto.ProductoResumenDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPedidoDTO {

    private String fecha;

    private MetodoPago metodoPago;

    private Transportista transportista;

    private String numFactura;

    private double subtotal;

    private double total;

    private String estado; // Puedes ajustar dinámicamente si lo necesitas

    private List<ProductoResumenDTO> productos;
}
