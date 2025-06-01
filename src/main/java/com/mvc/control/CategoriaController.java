package com.mvc.control;

import com.mvc.model.categoria.CategoriaDTO;
import com.mvc.model.categoria.CategoriaVO;
import com.mvc.service.categoria.CategoriaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // ✅ Obtener todas las categorías
    @GetMapping("/")
    public ResponseEntity<List<CategoriaVO>> obtenerCategorias() {
        List<CategoriaVO> categorias = categoriaService.obtenerTodas();
        return categorias.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(categorias);
    }

    @GetMapping("/resumen")
public ResponseEntity<List<CategoriaDTO>> obtenerCategoriasResumen() {
    List<CategoriaDTO> categorias = categoriaService.obtenerTodasDTO();
    return categorias.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(categorias);
}

    // ✅ Obtener una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaVO> obtenerCategoriaPorId(@PathVariable Long id) {
        CategoriaVO categoria = categoriaService.obtenerPorId(id);
        return (categoria != null)
                ? ResponseEntity.ok(categoria)
                : ResponseEntity.notFound().build();
    }

    // (Opcional) Crear categoría - útil solo si vas a permitir crear nuevas desde frontend
    @PostMapping("/")
    public ResponseEntity<String> crearCategoria(@RequestBody CategoriaDTO dto) {
        categoriaService.guardar(dto);
        return ResponseEntity.ok("Categoría creada correctamente");
    }
}
