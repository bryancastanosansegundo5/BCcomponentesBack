package com.mvc.service.opcionMenu;

import java.util.List;
import com.mvc.model.opcionMenu.OpcionMenuDTO;

public interface OpcionMenuService {
    List<OpcionMenuDTO> getOpcionesGestionPorUsuario(Long usuarioId);
    void insertarOpcionesPorDefecto();
    List<OpcionMenuDTO> getOpcionesPorRolNombre(String nombreRol);
    List<OpcionMenuDTO> getOpcionesPorRolYUbicacion(String nombreRol, String ubicacion);
}
