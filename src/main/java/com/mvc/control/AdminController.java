package com.mvc.control;

import com.mvc.model.configuracion.ConfiguracionDTO;
import com.mvc.model.estadisticas.ResumenEstadisticasDTO;
import com.mvc.model.pedido.VentasMensualesDTO;
import com.mvc.service.config.ConfiguracionService;
import com.mvc.service.pedido.PedidoService;
import com.mvc.service.usuario.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/configuracion")
    public ResponseEntity<List<ConfiguracionDTO>> obtenerTodas(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        usuarioService.verificarRol(usuarioId, 3);
        return ResponseEntity.ok(configuracionService.listarConfiguraciones());
    }

    @PutMapping("/configuracion")
    public ResponseEntity<ConfiguracionDTO> actualizar(HttpServletRequest request, @Valid @RequestBody ConfiguracionDTO dto) {
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
}
