package com.mvc.model.pedido;

import com.mvc.model.enums.MetodoPago;
import com.mvc.model.enums.Transportista;
import com.mvc.model.producto.ProductoResumenDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPedidoCompletoDTO {
    private String fecha;
    private MetodoPago metodoPago;
    private Transportista transportista;
    private String numFactura;
    private double subtotal;
    private double total;
    private String estado;
   private LocalDate fechaEntregaUsuario;

    // Datos de dirección de envío
    private String nombre;
    private String apellidos;
    private String direccion;
    private String adicionales;
    private String codigoPostal;
    private String poblacion;
    private String provincia;
    private String pais;
    private String telefono;

    private List<ProductoResumenDTO> productos;
}
