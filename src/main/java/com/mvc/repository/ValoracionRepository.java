package com.mvc.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mvc.model.detalle.DetalleVO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.model.valoracion.ValoracionVO;

public interface ValoracionRepository extends JpaRepository<ValoracionVO, Long> {
        List<ValoracionVO> findByProductoAndVisibleTrue(ProductoVO producto);

        List<ValoracionVO> findByUsuario(UsuarioVO usuario);

        boolean existsByUsuarioAndProducto(UsuarioVO usuario, ProductoVO producto);

        boolean existsByDetalle(DetalleVO detalle);

        @Query("""
                            SELECT v FROM ValoracionVO v
                            WHERE (:usuario IS NULL OR LOWER(v.usuario.email) LIKE LOWER(CONCAT('%', :usuario, '%')))
                              AND (:comentario IS NULL OR LOWER(v.comentario) LIKE LOWER(CONCAT('%', :comentario, '%')))
                              AND (:puntuacion IS NULL OR v.puntuacion = :puntuacion)
                              AND (:producto IS NULL OR LOWER(v.producto.nombre) LIKE LOWER(CONCAT('%', :producto, '%')))
                              AND (:fechaInicio IS NULL OR v.detalle.pedido.fecha >= :fechaInicio)
                              AND (:fechaFin IS NULL OR v.detalle.pedido.fecha <= :fechaFin)
                              AND (:visible IS NULL OR v.visible = :visible)
                        """)
        Page<ValoracionVO> buscarValoracionesFiltradas(
                        @Param("usuario") String usuario,
                        @Param("comentario") String comentario,
                        @Param("puntuacion") Integer puntuacion,
                        @Param("producto") String producto,
                        @Param("fechaInicio") LocalDateTime fechaInicio,
                        @Param("fechaFin") LocalDateTime fechaFin,
                        @Param("visible") Boolean visible,
                        Pageable pageable);

}
