package com.mvc.service.carrito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvc.model.carrito.CarritoDTO;
import com.mvc.model.carrito.CarritoVO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.repository.CarritoRepository;
import com.mvc.repository.ProductoRepository;
import com.mvc.repository.UsuarioRepository;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void insertar(CarritoDTO dto) {
        CarritoVO carrito = new CarritoVO();
        carrito.setCantidad(dto.getCantidad());

        ProductoVO producto = productoRepository.findById(dto.getProductoId()).orElse(null);
        UsuarioVO usuario = usuarioRepository.findById(dto.getUsuarioId()).orElse(null);

        carrito.setProducto(producto);
        carrito.setUsuario(usuario);

        carritoRepository.save(carrito);
    }

    @Override
    @Transactional
    public void quitarUnidad(Long usuarioId, Long productoId) {
        Optional<CarritoVO> carrito = carritoRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);

        carrito.ifPresent(c -> {
            System.out.println("➡️ Carrito encontrado: " + c.getCantidad());
        
            if (c.getCantidad() > 1) {
                c.setCantidad(c.getCantidad() - 1);
                carritoRepository.save(c);
                System.out.println("🔻 Unidad restada. Nueva cantidad: " + c.getCantidad());
            } else {
                carritoRepository.delete(c);
                System.out.println("🗑 Producto eliminado del carrito");
            }
        });
    }

    @Override
    @Transactional
    public void eliminarPorUsuarioYProducto(Long usuarioId, Long productoId) {
        carritoRepository.deleteByUsuarioIdAndProductoId(usuarioId, productoId);
    }

    @Override
    @Transactional
    public List<CarritoVO> getByUsuarioId(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public List<CarritoVO> getByProductoId(Long productoId) {
        return carritoRepository.findByProductoId(productoId);
    }

    @Override
    @Transactional
    public int contarTotalPorProducto(Long productoId) {
        Integer suma = carritoRepository.sumCantidadByProductoId(productoId);
        return (suma != null) ? suma : 0;
    }

    @Override
    @Transactional
    public void vaciarPorUsuario(Long usuarioId) {
        carritoRepository.deleteByUsuarioId(usuarioId);
    }

    @Override
    public int contarEnCarritosPorProducto(Long productoId) {
        return carritoRepository.contarPorProductoId(productoId);
    }

    @Override
    @Transactional
    public List<Long> obtenerProductoIdsEnCarritos() {
        return carritoRepository.findDistinctProductoIds();
    }

    @Override
    @Transactional
    public void eliminarCarritosInactivos(int horas) {
        // Calculamos el límite de tiempo a partir del cual consideramos el carrito como
        // inactivo
        LocalDateTime limite = LocalDateTime.now().minusHours(horas);

        // Borramos todos los productos en carritos cuya fecha de creación sea anterior
        // al límite
        carritoRepository.eliminarCarritosAntiguos(limite);
    }

    @Override
    @Transactional
    public void insertarOSumar(Long usuarioId, Long productoId, int cantidad) {
        // Buscamos si ya existe un registro para ese usuario y ese producto
        Optional<CarritoVO> existente = carritoRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);

        if (existente.isPresent()) {
            // Si ya existe, sumamos la cantidad
            CarritoVO carrito = existente.get();
            carrito.setCantidad(carrito.getCantidad() + cantidad);
            carritoRepository.save(carrito);
        } else {
            // Si no existe, creamos uno nuevo usando el DTO y la lógica ya definida
            CarritoDTO dto = new CarritoDTO(usuarioId, productoId, cantidad);
            insertar(dto);
        }
    }

}
