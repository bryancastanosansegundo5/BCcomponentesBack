package com.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mvc.model.valoracion.ValoracionVO;

public interface ValoracionRepository extends JpaRepository<ValoracionVO, Long> {
}
