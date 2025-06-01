package com.mvc.service.producto;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import com.mvc.model.producto.ProductoDTO;
import com.mvc.model.producto.ProductoListadoDTO;
import com.mvc.model.producto.ProductoVO;

public interface ProductoService {

    void insertar(ProductoDTO dto);

    void actualizar(Long id, ProductoDTO dto);

    void borrarPorId(Long id);

    ProductoVO getById(Long id);

    List<ProductoVO> getAll();

    List<ProductoVO> getByIds(List<Long> ids);

    List<ProductoVO> getByNombre(String nombre);

    Page<ProductoDTO> getByCategoria(Long categoriaId, Pageable pageable);

    List<ProductoVO> getByStockMenorA(int cantidad);

    void verificarStockEnCarritos(Long productoId);

    void notificarUsuarios(Long productoId);

    List<ProductoDTO> getProductos();

    List<ProductoDTO> filtrarPorCampo(String campo, String valor);

    Map<String, List<ProductoVO>> getRecomendaciones(List<Long> productoIds, Long usuarioId);

    List<ProductoVO> getMasVendidos();

    Page<ProductoDTO> getPaginado(Pageable pageable);

    List<ProductoVO> getMenosVendidosConStock();

    List<ProductoDTO> obtenerTop10PorCategoria(Long id);

    Page<ProductoDTO> buscarCatalogoPublico(String nombre, Long categoriaId, Pageable pageable);

    void importarDesdeExcel(MultipartFile archivo);
    byte[] generarPlantillaExcel();

}
