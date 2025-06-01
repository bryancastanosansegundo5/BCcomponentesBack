package com.mvc.service.config;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mvc.model.configuracion.ConfiguracionDTO;
import com.mvc.model.configuracion.ConfiguracionVO;
import com.mvc.repository.ConfiguracionRepository;

import jakarta.transaction.Transactional;

@Service
public class ConfiguracionServiceImpl implements ConfiguracionService {
    @Autowired
    private ConfiguracionRepository configuracionRepository;

    @Override
    public synchronized String generarNumeroFactura() {
        ConfiguracionVO config = configuracionRepository.findByClave("numero_factura").orElse(null);
    
        String prefijo = "F-BC-";
        int añoActual = Year.now().getValue();
    
        if (config == null) {
            config = new ConfiguracionVO();
            config.setClave("numero_factura");
            config.setValor(prefijo + añoActual + String.format("%06d", 1));
        } else {
            String valorActual = config.getValor(); // Ej: F-BC-2025000123
            String parteNumerica = valorActual.replace(prefijo, ""); // "2025000123"
    
            int añoGuardado = Integer.parseInt(parteNumerica.substring(0, 4));
            int numero = Integer.parseInt(parteNumerica.substring(4));
    
            if (añoGuardado == añoActual) {
                numero++;
            } else {
                // Año nuevo → reiniciar contador
                numero = 1;
            }
    
            String nuevoValor = String.format("%s%d%06d", prefijo, añoActual, numero);
            config.setValor(nuevoValor);
        }
    
        configuracionRepository.save(config);
        return config.getValor();
    }
    
     @Override
    public List<ConfiguracionDTO> listarConfiguraciones() {
        return configuracionRepository.findAll().stream().map(config -> {
            ConfiguracionDTO dto = new ConfiguracionDTO();
            dto.setId(config.getId());
            dto.setClave(config.getClave());
            dto.setValor(config.getValor());
            return dto;
        }).collect(Collectors.toList());
    }

 @Override
@Transactional
public ConfiguracionDTO actualizarConfiguracion(ConfiguracionDTO dto) {
    ConfiguracionVO vo = configuracionRepository.findById(dto.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Configuración no encontrada"));

    // Validar que no haya otra configuración con la misma clave y distinto ID
    if (configuracionRepository.existsByClaveAndIdNot(dto.getClave(), dto.getId())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una configuración con esa clave");
    }

    vo.setClave(dto.getClave());
    vo.setValor(dto.getValor());

    ConfiguracionVO guardado = configuracionRepository.save(vo);
    return new ConfiguracionDTO(guardado.getId(), guardado.getClave(), guardado.getValor());
}


@Override
@Transactional
public boolean eliminarPorClave(String clave) {
    return configuracionRepository.findByClave(clave).map(configuracion -> {
        configuracionRepository.delete(configuracion);
        return true;
    }).orElse(false);
}

@Override
public Optional<ConfiguracionVO> findByClave(String clave) {
    return configuracionRepository.findByClave(clave);
}
}
