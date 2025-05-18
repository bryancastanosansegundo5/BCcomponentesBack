package com.mvc.service.valoracion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.mvc.model.usuario.CambioClaveDTO;
import com.mvc.model.usuario.UsuarioDTO;
import com.mvc.model.usuario.UsuarioEditarDTO;
import com.mvc.model.usuario.UsuarioListadoDTO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.model.valoracion.ValoracionAdminDTO;
import com.mvc.model.valoracion.ValoracionDTO;
import com.mvc.model.valoracion.ValoracionUsuarioDTO;
import com.mvc.model.valoracion.ValoracionVO;

public interface ValoracionService {
    ValoracionVO guardarValoracion(ValoracionDTO dto);

    List<ValoracionUsuarioDTO> obtenerValoracionesProducto(Long productoId);

    List<ValoracionUsuarioDTO> obtenerValoracionesPorUsuario(Long usuarioId);

    Page<ValoracionAdminDTO> obtenerTodasLasValoraciones(Pageable pageable);

    void actualizarVisibilidad(Long id, boolean visible);

    Page<ValoracionAdminDTO> buscarValoracionesFiltradas(
    String usuario,
    String comentario,
    Integer puntuacion,
    String producto,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    Boolean visible,
    Pageable pageable
);

}
