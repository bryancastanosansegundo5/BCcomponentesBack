package com.mvc.model.pedido;

import com.mvc.model.enums.MetodoPago;
import com.mvc.model.enums.Transportista;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    @NotNull
    private Long usuarioId;

    @NotNull
    private DireccionEnvioDTO direccion;

    @NotEmpty
    private List<ProductoPedidoDTO> productos;

    @NotNull
    private MetodoPago metodoPago;

    @NotNull
    private Transportista transportista;

    @Positive
    private double total;

    @NotNull
    private LocalDate fechaEntregaUsuario;

    private DatosTarjetaDTO datosTarjeta;
}
