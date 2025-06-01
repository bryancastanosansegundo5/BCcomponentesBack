package com.mvc.service.rol;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvc.model.rol.RolVO;
import com.mvc.repository.RolRepository;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public RolVO findByNombreIgnoreCase(String nombre) {
        return rolRepository.findByNombreIgnoreCase(nombre).orElse(null);
    }

    @Override
    public List<RolVO> obtenerTodos() {
        return rolRepository.findAll();
    }
}

