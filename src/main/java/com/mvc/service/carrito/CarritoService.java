package com.mvc.service.carrito;

import java.util.List;
import java.util.Optional;

import com.mvc.model.carrito.CarritoDTO;
import com.mvc.model.carrito.CarritoVO;

public interface CarritoService {

    void insertar(CarritoDTO dto);

    void eliminarPorUsuarioYProducto(Long usuarioId, Long productoId);

    void quitarUnidad(Long usuarioId, Long productoId);

    List<CarritoVO> getByUsuarioId(Long usuarioId);

    List<CarritoVO> getByProductoId(Long productoId);

    int contarTotalPorProducto(Long productoId);

    void vaciarPorUsuario(Long usuarioId);

    int contarEnCarritosPorProducto(Long productoId);

    List<Long> obtenerProductoIdsEnCarritos();

    void eliminarCarritosInactivos(int horas);

    void insertarOSumar(Long usuarioId, Long productoId, int cantidad);




}
