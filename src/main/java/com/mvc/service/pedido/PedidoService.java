package com.mvc.service.pedido;

import java.util.List;

import com.mvc.model.estadisticas.ResumenEstadisticasDTO;
import com.mvc.model.pedido.PedidoDTO;
import com.mvc.model.pedido.PedidoListadoDTO;
import com.mvc.model.pedido.PedidoVO;
import com.mvc.model.pedido.ResumenPedidoCompletoDTO;
import com.mvc.model.pedido.VentasMensualesDTO;

public interface PedidoService {

    List<PedidoVO> obtenerTodos();

    PedidoVO obtenerPorId(Long id);

    String insertar(PedidoDTO dto);

    PedidoVO buscarPorNumeroFactura(String numFactura);

    ResumenPedidoCompletoDTO obtenerResumenPorFactura(String numFactura);

    List<PedidoListadoDTO> obtenerPedidosPorUsuario(Long usuarioId);
    List<PedidoListadoDTO> obtenerTodosPedidos();

    public byte[] generarFacturaPdf(String numFactura);

    List<VentasMensualesDTO> obtenerVentasPorMes();

    ResumenEstadisticasDTO obtenerResumenGeneral();

}
