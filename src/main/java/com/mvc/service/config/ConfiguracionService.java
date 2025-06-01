package com.mvc.service.config;

import java.util.List;
import java.util.Optional;

import com.mvc.model.configuracion.ConfiguracionDTO;
import com.mvc.model.configuracion.ConfiguracionVO;

public interface ConfiguracionService {
    String generarNumeroFactura();
    List<ConfiguracionDTO> listarConfiguraciones();
    ConfiguracionDTO actualizarConfiguracion(ConfiguracionDTO configDTO);
    boolean eliminarPorClave(String clave);
    Optional<ConfiguracionVO> findByClave(String clave);

}
