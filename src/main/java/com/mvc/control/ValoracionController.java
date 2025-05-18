package com.mvc.control;

import com.mvc.model.valoracion.ValoracionDTO;
import com.mvc.model.valoracion.ValoracionUsuarioDTO;
import com.mvc.model.valoracion.ValoracionVO;
import com.mvc.service.valoracion.ValoracionService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/valoracion")
public class ValoracionController {

    @Autowired
    private ValoracionService valoracionService;

    @PostMapping("/crear")
    public ResponseEntity<?> guardarValoracion(@Valid @RequestBody ValoracionDTO dto) {
        try {
            valoracionService.guardarValoracion(dto); // No necesitas guardar el resultado si no lo devuelves
            return ResponseEntity.ok().build(); // ✅ No devuelve cuerpo, evita errores de serialización
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Comentario rechazado: " + e.getMessage());
        }
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ValoracionUsuarioDTO>> obtenerValoraciones(@PathVariable Long productoId) {
    return ResponseEntity.ok(valoracionService.obtenerValoracionesProducto(productoId));
}

    @GetMapping("/usuario")
    public ResponseEntity<List<ValoracionUsuarioDTO>> obtenerPorUsuario(@RequestHeader("usuario-id") Long usuarioId) {
        return ResponseEntity.ok(valoracionService.obtenerValoracionesPorUsuario(usuarioId));
    }

}
