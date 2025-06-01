package com.mvc.control;

import com.mvc.model.configuracion.ConfiguracionDTO;
import com.mvc.model.estadisticas.ResumenEstadisticasDTO;
import com.mvc.model.pedido.VentasMensualesDTO;
import com.mvc.model.valoracion.ValoracionAdminDTO;
import com.mvc.model.valoracion.VisibilidadValoracionDTO;
import com.mvc.service.config.ConfiguracionService;
import com.mvc.service.pedido.PedidoService;
import com.mvc.service.usuario.UsuarioService;
import com.mvc.service.valoracion.ValoracionService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private ConfiguracionService configuracionService;
    @Autowired
    private ValoracionService valoracionService;

    @GetMapping("/configuracion")
    public ResponseEntity<List<ConfiguracionDTO>> obtenerTodas(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        usuarioService.verificarRol(usuarioId, 3);
        return ResponseEntity.ok(configuracionService.listarConfiguraciones());
    }

    @PutMapping("/configuracion")
    public ResponseEntity<ConfiguracionDTO> actualizar(HttpServletRequest request,
            @Valid @RequestBody ConfiguracionDTO dto) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        usuarioService.verificarRol(usuarioId, 3);
        return ResponseEntity.ok(configuracionService.actualizarConfiguracion(dto));
    }

    @DeleteMapping("/configuracion/{clave}")
    public ResponseEntity<Void> eliminar(HttpServletRequest request, @PathVariable String clave) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        usuarioService.verificarRol(usuarioId, 3);
        boolean eliminado = configuracionService.eliminarPorClave(clave);
        if (!eliminado)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Clave no encontrada");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estadisticas/ventas-mensuales")
    public ResponseEntity<List<VentasMensualesDTO>> obtenerVentasMensuales(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        usuarioService.verificarRol(usuarioId, 3);
        return ResponseEntity.ok(pedidoService.obtenerVentasPorMes());
    }

    @GetMapping("/estadisticas/resumen")
    public ResponseEntity<ResumenEstadisticasDTO> obtenerResumenGeneral(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        usuarioService.verificarRol(usuarioId, 3);
        return ResponseEntity.ok(pedidoService.obtenerResumenGeneral());
    }

    @GetMapping("/valoraciones")
    public ResponseEntity<Page<ValoracionAdminDTO>> listarTodasLasValoraciones(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");
        usuarioService.verificarRol(usuarioId, 3);
        return ResponseEntity.ok(valoracionService.obtenerTodasLasValoraciones(PageRequest.of(page, size)));
    }

    @PutMapping("/valoraciones/{id}/visibilidad")
    public ResponseEntity<Void> cambiarVisibilidad(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody VisibilidadValoracionDTO dto) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");
        usuarioService.verificarRol(usuarioId, 3); // Solo admin

        try {
            valoracionService.actualizarVisibilidad(id, dto.isVisible());
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Valoración no encontrada");
        }
    }

    @GetMapping("/valoraciones/buscar")
    public ResponseEntity<Page<ValoracionAdminDTO>> listarValoracionesFiltradas(
            HttpServletRequest request,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String comentario,
            @RequestParam(required = false) Integer puntuacion,
            @RequestParam(required = false) String producto,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate fechaInicio,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate fechaFin,

            @RequestParam(required = false) Boolean visible,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");
        usuarioService.verificarRol(usuarioId, 3); // Solo admin

        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime fechaInicioDT = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fechaFinDT = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

        return ResponseEntity.ok(
                valoracionService.buscarValoracionesFiltradas(
                        usuario,
                        comentario,
                        puntuacion,
                        producto,
                        fechaInicioDT,
                        fechaFinDT,
                        visible,
                        pageable)

        );
    }

}
