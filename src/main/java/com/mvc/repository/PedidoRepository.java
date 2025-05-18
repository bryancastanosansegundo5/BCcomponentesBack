package com.mvc.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mvc.model.pedido.PedidoVO;
import com.mvc.model.pedido.VentasMensualesDTO;

public interface PedidoRepository extends JpaRepository<PedidoVO, Long> {

    // Buscar por número de factura
    Optional<PedidoVO> findByNumFactura(String numFactura);

    // Buscar pedido con sus detalles y productos (para resumen completo del pedido)
    // @EntityGraph(attributePaths = { "detalles", "detalles.producto" })
    // Optional<PedidoVO> findWithDetallesByNumFactura(String numFactura);
@Query("""
    SELECT DISTINCT p FROM PedidoVO p
    JOIN FETCH p.detalles d
    JOIN FETCH d.producto
    WHERE p.numFactura = :numFactura
""")
Optional<PedidoVO> findWithDetallesByNumFactura(@Param("numFactura") String numFactura);

    @EntityGraph(attributePaths = { "detalles", "detalles.producto" })
    @Query("SELECT p FROM PedidoVO p ORDER BY p.fecha DESC")
    List<PedidoVO> findAllWithDetallesAndProductos();

    List<PedidoVO> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    @Query("SELECT COUNT(p) FROM PedidoVO p WHERE p.usuario.id = :usuarioId")
    int contarPedidosPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT new com.mvc.model.pedido.VentasMensualesDTO(FUNCTION('MONTHNAME', p.fecha), SUM(p.total)) " +
            "FROM PedidoVO p WHERE p.estado = 'Pago aceptado' GROUP BY FUNCTION('MONTH', p.fecha) ORDER BY FUNCTION('MONTH', p.fecha)")//Consulta de hibernate
    List<VentasMensualesDTO> obtenerVentasAgrupadasPorMes();

    @Query("SELECT SUM(p.total) FROM PedidoVO p WHERE p.estado = 'Pago aceptado'")
    Double obtenerTotalVentas();

    @Query("SELECT COUNT(p) FROM PedidoVO p")
    Long contarTotalPedidos();

    @Query("SELECT p FROM PedidoVO p ORDER BY p.fecha DESC")
    List<PedidoVO> findAllPedidos();

}
