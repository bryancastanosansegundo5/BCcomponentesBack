package com.mvc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mvc.model.configuracion.ConfiguracionVO;

public interface ConfiguracionRepository extends JpaRepository<ConfiguracionVO, Long> {
    Optional<ConfiguracionVO> findByClave(String clave);
    boolean existsByClave(String clave);
    boolean existsByClaveAndIdNot(String clave, Long id);

}
