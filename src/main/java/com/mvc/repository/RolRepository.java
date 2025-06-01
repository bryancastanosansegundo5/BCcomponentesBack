package com.mvc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mvc.model.rol.RolVO;

public interface RolRepository extends JpaRepository<RolVO, Integer> {
    Optional<RolVO> findByNombreIgnoreCase(String nombre);
    Optional<RolVO> findByNombre(String nombre);
}
