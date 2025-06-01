package com.mvc.control;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mvc.model.producto.ProductoDTO;
import com.mvc.model.producto.ProductoListadoDTO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.service.producto.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    ProductoService productoService;

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<List<ProductoDTO>> getProductoAll() {
        List<ProductoDTO> productos = productoService.getProductos();
        return productos.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(productos);
    }

    @GetMapping(value = "/paginado", produces = "application/json")
    public ResponseEntity<Page<ProductoDTO>> getPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoDTO> productos = productoService.getPaginado(pageable);

        return productos.hasContent()
                ? ResponseEntity.ok(productos)
                : ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/categoria/{id}", produces = "application/json")
    public ResponseEntity<Page<ProductoDTO>> getProductoByCategoria(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoDTO> productos = productoService.getByCategoria(id, pageable);

        return ResponseEntity.ok(productos);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ProductoVO> getProductoById(@PathVariable Long id) {
        return Optional.ofNullable(productoService.getById(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/nombre/{nombre}", produces = "application/json")
    public ResponseEntity<List<ProductoVO>> getProductoByNombre(@PathVariable String nombre) {
        List<ProductoVO> productos = productoService.getByNombre(nombre);
        return productos.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(productos);
    }

    @GetMapping(value = "/stock/menor/{cantidad}", produces = "application/json")
    public ResponseEntity<List<ProductoVO>> getProductosPorStockMenorA(@PathVariable int cantidad) {
        List<ProductoVO> productos = productoService.getByStockMenorA(cantidad);
        return productos.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(productos);
    }

    @GetMapping("/buscarCatalogo")
    public ResponseEntity<Page<ProductoDTO>> buscarPublico(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoDTO> productos = productoService.buscarCatalogoPublico(nombre, categoriaId, pageable);

        return productos.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(productos);
    }

    @PostMapping("/por-ids")
    public ResponseEntity<List<ProductoVO>> getProductosPorIds(@RequestBody List<Long> ids) {
        List<ProductoVO> productos = productoService.getByIds(ids);
        return ResponseEntity.ok(productos);
    }

    @PostMapping(value = "/recomendaciones", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, List<ProductoVO>>> getRecomendaciones(
            @RequestBody List<Long> productoIds,
            @RequestParam(required = false) Long usuarioId) {

        Map<String, List<ProductoVO>> resultado = productoService.getRecomendaciones(productoIds, usuarioId);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/mas-vendidos")
    public ResponseEntity<List<ProductoVO>> getMasVendidos() {
        List<ProductoVO> productos = productoService.getMasVendidos();
        return productos.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(productos);
    }

    @GetMapping("/menos-vendidos")
    public ResponseEntity<List<ProductoVO>> getMenosVendidosConStock() {
        return ResponseEntity.ok(productoService.getMenosVendidosConStock());
    }

    @GetMapping("/mas-vendidos/categoria/{id}")
    public List<ProductoDTO> obtenerMasVendidosPorCategoria(@PathVariable Long id) {
        return productoService.obtenerTop10PorCategoria(id);
    }
}
