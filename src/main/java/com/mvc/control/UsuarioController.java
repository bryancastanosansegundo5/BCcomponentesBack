package com.mvc.control;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mvc.model.usuario.CambioClaveDTO;
import com.mvc.model.usuario.LoginDTO;
import com.mvc.model.usuario.LoginResponseDTO;
import com.mvc.model.usuario.RegistroDTO;
import com.mvc.model.usuario.UsuarioDTO;
import com.mvc.model.carrito.CarritoVO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.security.JwtUtil;
import com.mvc.service.usuario.UsuarioService;
import com.mvc.service.carrito.CarritoService;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
    private static final Logger loginLogger = LogManager.getLogger("loginLogger");

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CarritoService carritoService;
 @Autowired
    private JwtUtil jwtUtil;
    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<List<UsuarioVO>> getUsuarioAll() {
        List<UsuarioVO> usuarios = usuarioService.getAll();
        return usuarios.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(usuarios);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UsuarioVO> getUsuarioById(@PathVariable Long id) {
        return Optional.ofNullable(usuarioService.getById(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/registro")
    public ResponseEntity<String> createUsuario(
            @Valid @RequestBody RegistroDTO registroDTO,
            @RequestHeader(value = "carrito-sesion", required = false) List<Long> carritoSesion) {

        try {
            if (!registroDTO.getClave().equals(registroDTO.getConfirmarClave())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Las contraseñas no coinciden");
            }

            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setEmail(registroDTO.getEmail());
            usuarioDTO.setClave(registroDTO.getClave());
            if (registroDTO.getNombre() != null && !registroDTO.getNombre().isBlank()) {
                usuarioDTO.setNombre(registroDTO.getNombre());
            }

            if (registroDTO.getApellidos() != null && !registroDTO.getApellidos().isBlank()) {
                usuarioDTO.setApellidos(registroDTO.getApellidos());
            }

            usuarioService.insertar(usuarioDTO);
            UsuarioVO usuarioRegistrado = usuarioService.getByEmail(usuarioDTO.getEmail());

            if (carritoSesion != null && !carritoSesion.isEmpty()) {
                Map<Long, Integer> cantidades = new HashMap<>();
                for (Long prodId : carritoSesion) {
                    cantidades.put(prodId, cantidades.getOrDefault(prodId, 0) + 1);
                }
                for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {
                    carritoService.insertarOSumar(usuarioRegistrado.getId(), entrada.getKey(), entrada.getValue());
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Usuario registrado correctamente");

        } catch (Exception e) {
            System.out.println("Error al registrar el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al registrar el usuario");
        }
    }

    @PutMapping("/")
    public ResponseEntity<String> putUsuario(@RequestBody UsuarioVO usuarioEditado) {
        usuarioService.actualizar(usuarioEditado);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsuario(@PathVariable Long id) {
        usuarioService.borrarPorId(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }

    @PostMapping("/verificarEmail")
    public ResponseEntity<String> verificarEmail(@RequestParam String email) {
        UsuarioVO usuario = usuarioService.getByEmail(email);

        if (usuario == null) {
            return ResponseEntity.ok("El email está disponible");
        } else if (usuario.isBaja()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Este email pertenece a un usuario dado de baja");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El email ya está registrado");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        boolean loginCorrecto = usuarioService.loginCorrecto(loginDTO.getEmail(), loginDTO.getClave());

        if (loginCorrecto) {
            UsuarioVO usuario = usuarioService.getByEmail(loginDTO.getEmail());
            loginLogger.info("Bien - Login exitoso para usuarioId: {} - email: {}", usuario.getId(),
                    loginDTO.getEmail());

            if (usuario.isBaja()) {
                LoginResponseDTO error = new LoginResponseDTO();
                error.setErrorMessage("Tu cuenta está desactivada. Contacta con soporte.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            if (usuario.getNumLogins() == null || usuario.getNumLogins() == 0) {
                usuario.setNumLogins(1);
            } else {
                usuario.setNumLogins(usuario.getNumLogins() + 1);
            }

            usuarioService.actualizar(usuario);

            // Generar token
            // String token = JwtUtil.generarToken(usuario.getId(), usuario.getRol().getId());
            String token = jwtUtil.generarToken(usuario.getId(), usuario.getRol().getId());

            // Devolver el usuario y el token
            LoginResponseDTO respuesta = new LoginResponseDTO(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellidos(),
                    usuario.getEmail(),
                    usuario.getRol().getId(),
                    token,
                    null
            );

            return ResponseEntity.ok(respuesta);
        } else {
            loginLogger.warn("Malo - Login fallido para email: {}", loginDTO.getEmail());
            LoginResponseDTO error = new LoginResponseDTO();
            error.setErrorMessage("Credenciales incorrectas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // En una API REST real sin estado, esto sería opcional o innecesario.
        // El frontend simplemente elimina el token / datos locales.
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

    @PutMapping("/guardarPaso")
    public ResponseEntity<String> actualizarParcialUsuario(
            @RequestBody UsuarioDTO usuarioDTO,
            @RequestHeader("usuario-id") Long usuarioId) {

        UsuarioVO usuario = usuarioService.getById(usuarioId);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        // Solo actualiza si no es null
        if (usuarioDTO.getNombre() != null)
            usuario.setNombre(usuarioDTO.getNombre());
        if (usuarioDTO.getApellidos() != null)
            usuario.setApellidos(usuarioDTO.getApellidos());
        if (usuarioDTO.getDireccion() != null)
            usuario.setDireccion(usuarioDTO.getDireccion());
        if (usuarioDTO.getPoblacion() != null)
            usuario.setPoblacion(usuarioDTO.getPoblacion());
        if (usuarioDTO.getProvincia() != null)
            usuario.setProvincia(usuarioDTO.getProvincia());
        if (usuarioDTO.getPais() != null)
            usuario.setPais(usuarioDTO.getPais());
        if (usuarioDTO.getTelefono() != null)
            usuario.setTelefono(usuarioDTO.getTelefono());
        if (usuarioDTO.getCodigoPostal() != null)
            usuario.setCodigoPostal(usuarioDTO.getCodigoPostal());
        if (usuarioDTO.getNumeroTarjeta() != null)
            usuario.setNumeroTarjeta(usuarioDTO.getNumeroTarjeta());
        if (usuarioDTO.getFechaExpiracion() != null)
            usuario.setFechaExpiracion(usuarioDTO.getFechaExpiracion());
        if (usuarioDTO.getCodigoSeguridad() != null)
            usuario.setCodigoSeguridad(usuarioDTO.getCodigoSeguridad());
        if (usuarioDTO.getTitularTarjeta() != null)
            usuario.setTitularTarjeta(usuarioDTO.getTitularTarjeta());

        // Y otros campos futuros como tarjeta, etc.

        usuarioService.actualizar(usuario);

        return ResponseEntity.ok("Usuario actualizado con datos del paso");
    }

    @PutMapping("/cambiar-clave")
    public ResponseEntity<String> cambiarClave(@Valid @RequestBody CambioClaveDTO cambioClaveDTO) {
        try {
            boolean ok = usuarioService.cambiarClave(cambioClaveDTO);
            if (ok)
                return ResponseEntity.ok("Contraseña actualizada correctamente.");
            return ResponseEntity.badRequest().body("No se encontró el usuario.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
