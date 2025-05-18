package com.mvc.service.valoracion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mvc.components.ModeradorComentariosService;
import com.mvc.model.detalle.DetalleVO;
import com.mvc.model.pedido.PedidoVO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.model.valoracion.ValoracionAdminDTO;
import com.mvc.model.valoracion.ValoracionDTO;
import com.mvc.model.valoracion.ValoracionUsuarioDTO;
import com.mvc.model.valoracion.ValoracionVO;
import com.mvc.repository.DetalleRepository;
import com.mvc.repository.ProductoRepository;
import com.mvc.repository.UsuarioRepository;
import com.mvc.repository.ValoracionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ValoracionServiceImpl implements ValoracionService {

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private DetalleRepository detallePedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModeradorComentariosService moderadorComentariosService;

    @Override
    public ValoracionVO guardarValoracion(ValoracionDTO dto) {
        DetalleVO detalle = detallePedidoRepository.findById(dto.getDetalleId())
                .orElseThrow(() -> new EntityNotFoundException("Detalle de pedido no encontrado"));

        PedidoVO pedido = detalle.getPedido();
        UsuarioVO usuario = pedido.getUsuario();

        // Validar que el detalle pertenece al usuario que envía la valoración
        if (!usuario.getId().equals(dto.getUsuarioId())) {
            throw new IllegalArgumentException("No puedes valorar productos que no has comprado tú.");
        }

        // Validar que el pedido esté enviado
        if (!"Pago aceptado".equals(pedido.getEstado())) {
            throw new IllegalArgumentException("Solo puedes valorar productos ya pagados.");
        }

        // Validar que no exista ya una valoración para este detalle
        if (valoracionRepository.existsByDetalle(detalle)) {
            throw new IllegalArgumentException("Ya has valorado esta compra de este producto.");
        }

        // Validar toxicidad
        if (moderadorComentariosService.esToxico(dto.getComentario())) {
            throw new IllegalArgumentException("El comentario contiene lenguaje inapropiado.");
        }
        ProductoVO producto = detalle.getProducto();

        ValoracionVO valoracion = new ValoracionVO();
        valoracion.setDetalle(detalle);
        valoracion.setComentario(dto.getComentario());
        valoracion.setPuntuacion(dto.getPuntuacion());
        valoracion.setVisible(dto.isVisible());
        valoracion.setProducto(producto); // ✅ evitar null en producto_id
        valoracion.setUsuario(usuario); // ✅ evitar null en usuario_id

        // Actualizar estadísticas del producto
        producto.setTotalValoraciones(producto.getTotalValoraciones() + 1);
        double sumaActual = producto.getValoracionMedia() * (producto.getTotalValoraciones() - 1);
        double nuevaMedia = (sumaActual + dto.getPuntuacion()) / producto.getTotalValoraciones();
        producto.setValoracionMedia(nuevaMedia);
        productoRepository.save(producto);

        return valoracionRepository.save(valoracion);
    }

    @Override
    public List<ValoracionUsuarioDTO> obtenerValoracionesProducto(Long productoId) {
        ProductoVO producto = productoRepository.findById(productoId).orElseThrow();

        return valoracionRepository.findByProductoAndVisibleTrue(producto).stream().map(valoracion -> {
            ValoracionUsuarioDTO dto = new ValoracionUsuarioDTO();
            dto.setComentario(valoracion.getComentario());
            dto.setPuntuacion(valoracion.getPuntuacion());
            dto.setEmailUsuario(valoracion.getUsuario().getEmail());
            dto.setProductoId(productoId);
            dto.setImagenProducto(producto.getImagen());
            return dto;
        }).toList();
    }

    @Override
    public List<ValoracionUsuarioDTO> obtenerValoracionesPorUsuario(Long usuarioId) {
        UsuarioVO usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return valoracionRepository.findByUsuario(usuario).stream().map(valoracion -> {
            ValoracionUsuarioDTO dto = new ValoracionUsuarioDTO();
            dto.setProductoId(valoracion.getProducto().getId());
            dto.setImagenProducto(valoracion.getProducto().getImagen());
            dto.setComentario(valoracion.getComentario());
            dto.setPuntuacion(valoracion.getPuntuacion());
            dto.setEmailUsuario(valoracion.getUsuario().getEmail());
            return dto;
        }).toList();
    }

    @Override
    public Page<ValoracionAdminDTO> obtenerTodasLasValoraciones(Pageable pageable) {
        return valoracionRepository.findAll(pageable).map(valoracion -> {
            ValoracionAdminDTO dto = new ValoracionAdminDTO();
            dto.setId(valoracion.getId());
            dto.setProducto(valoracion.getProducto().getNombre());
            dto.setImagenProducto(valoracion.getProducto().getImagen()); // ✅ imagen
            dto.setEmailUsuario(valoracion.getUsuario().getEmail());
            dto.setComentario(valoracion.getComentario());
            dto.setPuntuacion(valoracion.getPuntuacion());
            dto.setVisible(valoracion.isVisible());
            dto.setFecha(valoracion.getDetalle().getPedido().getFecha());

            return dto;
        });
    }

    @Override
    public Page<ValoracionAdminDTO> buscarValoracionesFiltradas(String usuario, String comentario, Integer puntuacion,
            String producto, LocalDateTime fechaInicio, LocalDateTime fechaFin, Boolean visible, Pageable pageable) {

        return valoracionRepository.buscarValoracionesFiltradas(
                usuario, comentario, puntuacion, producto, fechaInicio, fechaFin, visible, pageable).map(valoracion -> {
                    ValoracionAdminDTO dto = new ValoracionAdminDTO();
                    dto.setId(valoracion.getId());
                    dto.setProducto(valoracion.getProducto().getNombre());
                    dto.setImagenProducto(valoracion.getProducto().getImagen());
                    dto.setEmailUsuario(valoracion.getUsuario().getEmail());
                    dto.setComentario(valoracion.getComentario());
                    dto.setPuntuacion(valoracion.getPuntuacion());
                    dto.setVisible(valoracion.isVisible());
                    dto.setFecha(valoracion.getDetalle().getPedido().getFecha());

                    return dto;
                });
    }

    public void actualizarVisibilidad(Long id, boolean visible) {
    ValoracionVO valoracion = valoracionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Valoración no encontrada"));
    valoracion.setVisible(visible);
    valoracionRepository.save(valoracion);

    // Recalcular estadísticas del producto
    ProductoVO producto = valoracion.getProducto();
    List<ValoracionVO> visibles = valoracionRepository.findByProductoAndVisibleTrue(producto);

    int total = visibles.size();
    double media = visibles.stream()
        .mapToInt(ValoracionVO::getPuntuacion)
        .average()
        .orElse(0.0);

    producto.setTotalValoraciones(total);
    producto.setValoracionMedia(media);
    productoRepository.save(producto);
}


}
