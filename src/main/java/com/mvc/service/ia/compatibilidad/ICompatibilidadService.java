package com.mvc.service.ia.compatibilidad;

import com.mvc.model.ia.compatibilidad.CompatibilidadRequestDTO;
import com.mvc.model.ia.compatibilidad.CompatibilidadResponseDTO;

public interface ICompatibilidadService {
    CompatibilidadResponseDTO comprobarCompatibilidad(CompatibilidadRequestDTO request);
}
