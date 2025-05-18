package com.mvc.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mvc.model.detalle.DetalleVO;
import com.mvc.model.pedido.PedidoVO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.model.usuario.UsuarioVO;

public interface DetalleRepository extends JpaRepository<DetalleVO, Long> {
    List<DetalleVO> findByPedido(PedidoVO pedido);

    @Query("""
                SELECT DISTINCT d.producto.id
                FROM DetalleVO d
                WHERE d.pedido.usuario.id = :usuarioId
                AND d.pedido.fecha >= :limite
            """)
    List<Long> findProductoIdsCompradosRecientemente(
            @Param("usuarioId") Long usuarioId,
            @Param("limite") LocalDateTime limite);

    @Query(value = """
                SELECT producto_id
                FROM detalle
                GROUP BY producto_id
                ORDER BY SUM(unidades) DESC
            """, nativeQuery = true)
    List<Long> findProductosMasVendidos();

    @Query("""
        SELECT COUNT(d) > 0 FROM DetalleVO d
        WHERE d.pedido.usuario = :usuario
        AND d.producto = :producto
        AND d.pedido.estado = 'Pago aceptado'
    """)
    boolean usuarioHaCompradoProductoEnviado(UsuarioVO usuario, ProductoVO producto);
}
