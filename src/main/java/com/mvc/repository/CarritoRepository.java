package com.mvc.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mvc.model.carrito.CarritoVO;

public interface CarritoRepository extends JpaRepository<CarritoVO, Long> {

    List<CarritoVO> findByUsuarioId(Long usuarioId);

    @Query("SELECT c FROM CarritoVO c JOIN FETCH c.usuario WHERE c.producto.id = :productoId")
List<CarritoVO> findByProductoId(@Param("productoId") Long productoId);


    Optional<CarritoVO> findByUsuarioIdAndProductoId(Long usuarioId, Long productoId);

    @Modifying
    @Query("DELETE FROM CarritoVO c WHERE c.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);

    void deleteByUsuarioIdAndProductoId(Long usuarioId, Long productoId);

    @Query("SELECT SUM(c.cantidad) FROM CarritoVO c WHERE c.producto.id = :productoId")
    Integer sumCantidadByProductoId(Long productoId);

    @Query("SELECT SUM(c.cantidad) FROM CarritoVO c WHERE c.producto.id = :productoId")
    int contarPorProductoId(@Param("productoId") Long productoId);

    @Query("SELECT DISTINCT c.producto.id FROM CarritoVO c")
    List<Long> findDistinctProductoIds();

    @Modifying
    @Query("DELETE FROM CarritoVO c WHERE c.fechaCreacion < :limite")
    void eliminarCarritosAntiguos(@Param("limite") LocalDateTime limite);



}
