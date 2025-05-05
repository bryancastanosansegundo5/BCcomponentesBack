package com.mvc.service.usuario;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mvc.model.usuario.CambioClaveDTO;
import com.mvc.model.usuario.UsuarioDTO;
import com.mvc.model.usuario.UsuarioEditarDTO;
import com.mvc.model.usuario.UsuarioListadoDTO;
import com.mvc.model.usuario.UsuarioVO;

public interface UsuarioService {

    void insertar(UsuarioDTO dto);

    void actualizar(UsuarioVO usuario);

    void borrarPorId(Long id);

    UsuarioVO getById(Long id);

    List<UsuarioVO> getAll();

    boolean existsByEmail(String email);

    boolean loginCorrecto(String email, String clave);

    UsuarioVO getByEmail(String email);

    void crearUsuarioSiNoExiste(String email, String clave, String nombre, String rolNombre);

    boolean cambiarClave(CambioClaveDTO datosCambioClave);

    List<UsuarioVO> obtenerPorRol(int rolId);

    void verificarRol(Long id, int... rolesPermitidos);

    List<UsuarioListadoDTO> obtenerUsuariosConPedidosPorCampo(String campo, String valor, int rolId);

    void actualizarUsuariosDesdeAdministracion(Long id, UsuarioEditarDTO dto, Long usuarioEditorId);
}
