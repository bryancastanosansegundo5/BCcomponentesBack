package com.mvc.service.proveedor;

import java.util.List;
import java.util.Optional;

import com.mvc.model.proovedor.ProveedorVO;

public interface ProveedorService {
    Optional<ProveedorVO> findByNombreParcial(String nombre);
    List<ProveedorVO> findAll();

}
