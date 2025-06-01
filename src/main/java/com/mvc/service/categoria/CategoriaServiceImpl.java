package com.mvc.service.categoria;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvc.model.categoria.CategoriaDTO;
import com.mvc.model.categoria.CategoriaVO;
import com.mvc.repository.CategoriaRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public List<CategoriaVO> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    @Override
    public List<CategoriaDTO> obtenerTodasDTO() {
        return categoriaRepository.findAll()
                .stream()
                .filter(c -> !c.isBaja())
                .map(c -> {
                    CategoriaDTO dto = new CategoriaDTO();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    dto.setBaja(c.isBaja());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public CategoriaVO obtenerPorId(Long id) {
        Optional<CategoriaVO> opt = categoriaRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    @Transactional
    public void guardar(CategoriaDTO dto) {
        CategoriaVO categoria = new CategoriaVO();
        categoria.setNombre(dto.getNombre());
        categoria.setBaja(dto.isBaja());
        categoriaRepository.save(categoria);
    }
}
