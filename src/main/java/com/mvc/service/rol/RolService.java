package com.mvc.service.rol;

import java.util.List;

import com.mvc.model.rol.RolVO;

public interface RolService {
    RolVO findByNombreIgnoreCase(String nombre);
    List<RolVO> obtenerTodos();
}
