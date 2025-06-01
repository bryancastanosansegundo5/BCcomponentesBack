package com.mvc.service.usuario;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.mvc.model.producto.ProductoDTO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.model.rol.RolDTO;
import com.mvc.model.usuario.*;
import com.mvc.repository.PedidoRepository;
import com.mvc.repository.RolRepository;
import com.mvc.repository.UsuarioRepository;
import com.mvc.service.rol.RolService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private RolService rolService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private Argon2PasswordEncoder passwordEncoder;

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    @Transactional
    public void insertar(UsuarioDTO dto) {
        UsuarioVO usuario = new UsuarioVO();
        usuario.setEmail(dto.getEmail());
        usuario.setClave(encodePassword(dto.getClave()));
        usuario.setNombre(Optional.ofNullable(dto.getNombre()).orElse(""));
        usuario.setApellidos(Optional.ofNullable(dto.getApellidos()).orElse(""));
        usuario.setDireccion(Optional.ofNullable(dto.getDireccion()).orElse(""));
        usuario.setBaja(false);
        if (dto.getRolId() > 0) {
            usuario.setRol(rolRepository.findById(dto.getRolId()).orElse(null));
        } else {
            usuario.setRol(rolRepository.findByNombreIgnoreCase("CLIENTE").orElse(null));
        }
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void actualizar(UsuarioVO vo) {
        UsuarioVO usuario = usuarioRepository.findById(vo.getId()).orElse(null);
        if (usuario != null) {
            usuario.setEmail(vo.getEmail());
            usuario.setClave(vo.getClave());
            usuario.setNombre(vo.getNombre());
            usuario.setApellidos(vo.getApellidos());
            usuario.setDireccion(vo.getDireccion());
            usuario.setBaja(vo.isBaja());
            usuario.setRol(rolRepository.findById(vo.getRol().getId()).orElse(null));
            usuario.setNumeroTarjeta(vo.getNumeroTarjeta());
            usuario.setFechaExpiracion(vo.getFechaExpiracion());
            usuario.setCodigoSeguridad(vo.getCodigoSeguridad());
            usuario.setTitularTarjeta(vo.getTitularTarjeta());
            usuario.setNumLogins(vo.getNumLogins());
            usuarioRepository.save(usuario);
        }
    }

    @Override
    @Transactional
    public void borrarPorId(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UsuarioVO getById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public List<UsuarioVO> getAll() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public boolean loginCorrecto(String email, String clave) {
        UsuarioVO usuario = usuarioRepository.findByEmail(email).orElse(null);
        return usuario != null && matchesPassword(clave, usuario.getClave());
    }

    @Override
    @Transactional
    public UsuarioVO getByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional
    public void crearUsuarioSiNoExiste(String email, String clave, String nombre, String rolNombre) {
        if (!usuarioRepository.existsByEmail(email)) {
            UsuarioVO usuario = new UsuarioVO();
            usuario.setEmail(email);
            usuario.setClave(encodePassword(clave));
            usuario.setNombre(nombre);
            usuario.setBaja(false);
            usuario.setRol(rolService.findByNombreIgnoreCase(rolNombre));
            usuarioRepository.save(usuario);
        }
    }

    @Override
    @Transactional
    public boolean cambiarClave(CambioClaveDTO datosCambioClave) {
        UsuarioVO usuario = usuarioRepository.findById(datosCambioClave.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (datosCambioClave.getClaveActual() == null || datosCambioClave.getClaveActual().isBlank()) {
            throw new IllegalArgumentException("La contraseña actual es obligatoria.");
        }
        if (!matchesPassword(datosCambioClave.getClaveActual(), usuario.getClave())) {
            throw new IllegalArgumentException("La clave actual no es correcta.");
        }

        if (!datosCambioClave.getNuevaClave().equals(datosCambioClave.getRepetirNuevaClave())) {
            throw new IllegalArgumentException("Las nuevas contraseñas no coinciden.");
        }

        usuario.setClave(encodePassword(datosCambioClave.getNuevaClave())); 
        usuarioRepository.save(usuario);
        return true;
    }

    @Override
    public List<UsuarioVO> obtenerPorRol(int rolId) {
        return usuarioRepository.findByRolId(rolId);
    }

    @Override
    public void verificarRol(Long usuarioId, int... rolesPermitidos) {
        UsuarioVO usuario = getById(usuarioId);
        int rolUsuario = usuario.getRol().getId();
        for (int rol : rolesPermitidos) {
            if (rolUsuario == rol)
                return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
    }

    @Override
    public List<UsuarioListadoDTO> obtenerUsuariosConPedidosPorCampo(String campoBusqueda, String valorBusqueda,
            int rolId) {
        List<UsuarioVO> usuariosFiltrados;
        boolean aplicarFiltroRol = rolId != 0;
        boolean estaDadoDeBaja = campoBusqueda.equals("baja") && Boolean.parseBoolean(valorBusqueda);

        if (campoBusqueda.equals("rol")) {
            int idRolBuscado;
            try {
                idRolBuscado = Integer.parseInt(valorBusqueda);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor del rol debe ser un número");
            }
            usuariosFiltrados = usuarioRepository.findByRolId(idRolBuscado);
        } else if (aplicarFiltroRol) {
            usuariosFiltrados = switch (campoBusqueda) {
                case "nombre" -> usuarioRepository.findByRolIdAndNombreContainingIgnoreCase(rolId, valorBusqueda);
                case "apellidos" -> usuarioRepository.findByRolIdAndApellidosContainingIgnoreCase(rolId, valorBusqueda);
                case "email" -> usuarioRepository.findByRolIdAndEmailContainingIgnoreCase(rolId, valorBusqueda);
                case "telefono" -> usuarioRepository.findByRolIdAndTelefonoContainingIgnoreCase(rolId, valorBusqueda);
                case "localidad" -> usuarioRepository.findByRolIdAndProvinciaContainingIgnoreCase(rolId, valorBusqueda);
                case "baja" -> usuarioRepository.findByRolIdAndBaja(rolId, estaDadoDeBaja);
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo de búsqueda no permitido");
            };
        } else {
            usuariosFiltrados = switch (campoBusqueda) {
                case "nombre" -> usuarioRepository.findByNombreContainingIgnoreCase(valorBusqueda);
                case "apellidos" -> usuarioRepository.findByApellidosContainingIgnoreCase(valorBusqueda);
                case "email" -> usuarioRepository.findByEmailContainingIgnoreCase(valorBusqueda);
                case "telefono" -> usuarioRepository.findByTelefonoContainingIgnoreCase(valorBusqueda);
                case "localidad" -> usuarioRepository.findByProvinciaContainingIgnoreCase(valorBusqueda);
                case "baja" -> usuarioRepository.findByBaja(estaDadoDeBaja);
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo de búsqueda no permitido");
            };
        }

        return usuariosFiltrados.stream().map(u -> {
            UsuarioListadoDTO dto = new UsuarioListadoDTO();
            dto.setId(u.getId());
            dto.setNombre(u.getNombre());
            dto.setApellidos(u.getApellidos());
            dto.setEmail(u.getEmail());
            dto.setTelefono(u.getTelefono());
            dto.setDireccion(u.getDireccion());
            dto.setPoblacion(u.getPoblacion());
            dto.setProvincia(u.getProvincia());
            dto.setPais(u.getPais());
            dto.setCodigoPostal(u.getCodigoPostal());
            dto.setBaja(u.isBaja());
            dto.setPedidos(pedidoRepository.contarPedidosPorUsuario(u.getId()));

            RolDTO rolDTO = new RolDTO();
            rolDTO.setId(u.getRol().getId());
            rolDTO.setNombre(u.getRol().getNombre());
            dto.setRol(rolDTO);

            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public void actualizarUsuariosDesdeAdministracion(Long id, UsuarioEditarDTO dto, Long usuarioEditorId) {

        UsuarioVO usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioVO editor = usuarioRepository.findById(usuarioEditorId)
                .orElseThrow(() -> new RuntimeException("Usuario editor no encontrado"));

        int rolEditor = editor.getRol().getId();

        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());
        usuario.setPoblacion(dto.getPoblacion());
        usuario.setProvincia(dto.getProvincia());
        usuario.setPais(dto.getPais());
        usuario.setCodigoPostal(dto.getCodigoPostal());
        usuario.setBaja(dto.isBaja());

        // Validación del cambio de rol
        if (dto.getRol() != null && dto.getRol().getId() > 0) {
            int nuevoRol = dto.getRol().getId();

            if (rolEditor == 2 && nuevoRol != 1) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Un empleado solo puede asignar el rol cliente");
            }

            usuario.setRol(rolRepository.findById(nuevoRol)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no válido")));
        }

        // Cambio de clave (si se indica)
        if (dto.getNuevaClave() != null && !dto.getNuevaClave().isBlank()) {
            if (!dto.getNuevaClave().equals(dto.getRepetirClave())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden");
            }
            usuario.setClave(encodePassword(dto.getNuevaClave())); // ← aquí
        }

        usuarioRepository.save(usuario);
    }

}
