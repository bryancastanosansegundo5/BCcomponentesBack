package com.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mvc.model.descuento.DescuentoVO;

public interface DescuentoRepository extends JpaRepository<DescuentoVO, Long> {
    DescuentoVO findByCodigo(String codigo);
}
