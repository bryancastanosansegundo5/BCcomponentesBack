package com.mvc.service.proveedor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mvc.model.proovedor.ProveedorVO;
import com.mvc.repository.ProveedorRepository;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    @Autowired
    ProveedorRepository proveedorRepository;

    @Override
    public Optional<ProveedorVO> findByNombreParcial(String nombre) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<ProveedorVO> findAll() {
        return proveedorRepository.findAll();
    }

}
