package com.mvc.service.categoria;

import java.util.List;
import com.mvc.model.categoria.CategoriaDTO;
import com.mvc.model.categoria.CategoriaVO;

public interface CategoriaService {
    List<CategoriaVO> obtenerTodas();

    CategoriaVO obtenerPorId(Long id);

    void guardar(CategoriaDTO dto);

    List<CategoriaDTO> obtenerTodasDTO();
}
