package com.mvc.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mvc.model.opcionMenu.OpcionMenuDTO;
import com.mvc.service.opcionMenu.OpcionMenuService;

@RestController
@RequestMapping("/api/opciones-menu")
public class OpcionMenuController {

    @Autowired
    private OpcionMenuService opcionMenuService;

    @GetMapping("/gestion")
    public List<OpcionMenuDTO> getOpcionesDeGestion(@RequestHeader("usuario-id") Long usuarioId) {
        return opcionMenuService.getOpcionesGestionPorUsuario(usuarioId);
    }

    @GetMapping("/publicas")
    public List<OpcionMenuDTO> getOpcionesPublicas() {
        return opcionMenuService.getOpcionesPorRolNombre("CLIENTE");
    }

    @GetMapping("/por-rol-y-ubicacion")
    public List<OpcionMenuDTO> getOpcionesPorRolYUbicacion(
            @RequestParam String rol,
            @RequestParam String ubicacion
    ) {
        return opcionMenuService.getOpcionesPorRolYUbicacion(rol, ubicacion);
    }
}
