package com.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mvc.model.opcionMenu.OpcionMenuVO;
import com.mvc.model.rol.RolVO;

public interface OpcionMenuRepository extends JpaRepository<OpcionMenuVO, Long> {
    List<OpcionMenuVO> findByRol(RolVO rol);
    List<OpcionMenuVO> findByRolAndUbicacion(RolVO rol, String ubicacion);
}
