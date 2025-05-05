package com.mvc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mvc.model.categoria.CategoriaVO;

public interface CategoriaRepository extends JpaRepository<CategoriaVO, Long> {
    Optional<CategoriaVO> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);

}
