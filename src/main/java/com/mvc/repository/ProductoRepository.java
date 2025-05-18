package com.mvc.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mvc.model.producto.ProductoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoVO, Long> {

    List<ProductoVO> findByNombreContainingIgnoreCase(String nombre);

    List<ProductoVO> findByCategoriaId(Long categoriaId);

    Page<ProductoVO> findByCategoriaId(Long categoriaId, Pageable pageable);

    List<ProductoVO> findByStockLessThan(int cantidad);

    List<ProductoVO> findByProveedorId(Long id);

    List<ProductoVO> findByBaja(boolean baja);

    List<ProductoVO> findByStockBetween(int min, int max);

    List<ProductoVO> findByStockGreaterThanEqual(int min);

    /**
     * Devuelve productos de la misma categoría que los productos visitados,
     * excluyendo los ya vistos.
     */
    @Query("""
                SELECT productoSugerido
                FROM ProductoVO productoSugerido
                WHERE productoSugerido.categoria IN (
                    SELECT productoOriginal.categoria
                    FROM ProductoVO productoOriginal
                    WHERE productoOriginal.id IN :idsProductosVisitados
                )
                AND productoSugerido.id NOT IN :idsProductosVisitados
            """)
    List<ProductoVO> findSimilaresPorProductoIds(@Param("idsProductosVisitados") List<Long> idsProductosVisitados);

    /**
     * Devuelve productos que tienen atributos técnicos definidos (socket, RAM o
     * PCIe)
     * y que no estén entre los productos que el usuario ya ha comprado.
     */
    @Query("""
                SELECT productoComplementario
                FROM ProductoVO productoComplementario
                WHERE (
                    productoComplementario.socket IS NOT NULL OR
                    productoComplementario.tipoRam IS NOT NULL OR
                    productoComplementario.pcie IS NOT NULL OR
                    productoComplementario.chipset IS NOT NULL OR
                    productoComplementario.potenciaW IS NOT NULL OR
                    productoComplementario.factorForma IS NOT NULL
                )
                AND productoComplementario.id NOT IN :idsProductosComprados
            """)
    List<ProductoVO> findComplementariosPorProductoIds(@Param("idsProductosComprados") List<Long> ids);

    @Query("""
                SELECT p FROM ProductoVO p
                WHERE p.vendidos > 0
                ORDER BY p.vendidos DESC
            """)
    List<ProductoVO> findMasVendidosOrdenados();

    @Query("""
            SELECT p FROM ProductoVO p
            WHERE p.baja = false
            AND p.stock > 0 ORDER BY p.vendidos ASC, p.stock DESC
            """)
    List<ProductoVO> findMenosVendidosConMasStock();

    Page<ProductoVO> findByBajaFalse(Pageable pageable);

    List<ProductoVO> findByBajaFalse();

    List<ProductoVO> findTop10ByCategoriaIdOrderByVendidosDesc(Long id);

    @Query("""
                SELECT p FROM ProductoVO p
                WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
                  AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
            """)
    Page<ProductoVO> buscarCatalogoPublico(@Param("nombre") String nombre, @Param("categoriaId") Long categoriaId,
            Pageable pageable);

            @Query("SELECT p FROM ProductoVO p ORDER BY FUNCTION('RAND')")
            List<ProductoVO> findProductosAleatorios();
            

}
