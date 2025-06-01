package com.mvc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mvc.model.proovedor.ProveedorVO;

public interface ProveedorRepository extends JpaRepository<ProveedorVO, Long> {
    Optional<ProveedorVO> findByNombreContainingIgnoreCase(String nombre);
    Optional<ProveedorVO> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);

}
