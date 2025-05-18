package com.mvc.control;

import com.mvc.model.pedido.PedidoListadoDTO;
import com.mvc.model.producto.ProductoDTO;
import com.mvc.model.producto.ProductoListadoDTO;
import com.mvc.model.producto.ProductoVO;
import com.mvc.model.usuario.UsuarioDTO;
import com.mvc.model.usuario.UsuarioEditarDTO;
import com.mvc.model.usuario.UsuarioListadoDTO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.service.pedido.PedidoService;
import com.mvc.service.producto.ProductoService;
import com.mvc.service.usuario.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/empleado")
public class EmpleadoController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProductoService productoService;
    @Autowired
    private PedidoService pedidoService;

    // ✅ Lista de clientes con cantidad de pedidos
    @GetMapping("/gestion-usuarios")
    public List<UsuarioListadoDTO> obtenerListadoClientes(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        return usuarioService.obtenerUsuariosConPedidosPorCampo("nombre", "", 1);
    }

    @GetMapping("/gestion-usuarios/buscar")
    public List<UsuarioListadoDTO> buscarUsuariosConPedidos(
            HttpServletRequest request,
            @RequestParam String campo,
            @RequestParam String valor,
            @RequestParam(required = false, defaultValue = "0") int rol) {

        Long usuarioId = (Long) request.getAttribute("usuarioId");
        UsuarioVO usuario = usuarioService.getById(usuarioId);
        int rolUsuario = usuario.getRol().getId();

        // ⚠️ Seguridad: si un EMPLEADO intenta buscar fuera de clientes, lo bloqueamos
        if (rolUsuario == 2 && rol != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para acceder a esta información");
        }

        return usuarioService.obtenerUsuariosConPedidosPorCampo(campo, valor, rol);
    }

    @PutMapping("/usuario/{id}")
    public ResponseEntity<?> actualizarUsuarioDesdeEmpleado(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody @Valid UsuarioEditarDTO dto) {

        Long usuarioAdminId = (Long) request.getAttribute("usuarioId");
        UsuarioVO usuarioSolicitante = usuarioService.getById(usuarioAdminId);
        UsuarioVO usuarioObjetivo = usuarioService.getById(id);

        if (usuarioObjetivo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario a editar no encontrado");
        }

        if (usuarioSolicitante.getRol().getId() == 2 && usuarioObjetivo.getRol().getId() != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar este usuario");
        }

        usuarioService.actualizarUsuariosDesdeAdministracion(id, dto, usuarioAdminId);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }

    @PostMapping("/usuario")
    public ResponseEntity<?> crearUsuarioDesdeEmpleado(
            HttpServletRequest request,
            @RequestBody @Valid UsuarioDTO dto) {

        Long usuarioAdminId = (Long) request.getAttribute("usuarioId");
        UsuarioVO usuarioSolicitante = usuarioService.getById(usuarioAdminId);

        int rolSolicitante = usuarioSolicitante.getRol().getId();
        int rolObjetivo = dto.getRolId();

        if (rolSolicitante == 2 && rolObjetivo != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para crear este tipo de usuario");
        }

        if (usuarioService.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ya existe un usuario con ese correo electrónico");
        }

        usuarioService.insertar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado correctamente");
    }

    @PostMapping("/productos")
    public ResponseEntity<String> crearProducto(
            @RequestBody @Valid ProductoDTO dto) {
        productoService.insertar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Producto creado correctamente");
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<String> actualizarProducto(
            @PathVariable Long id,
            @RequestBody @Valid ProductoDTO dto) {
        productoService.actualizar(id, dto);
        return ResponseEntity.ok("Producto actualizado correctamente");
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<String> eliminarProducto(
            HttpServletRequest request,
            @PathVariable Long id) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        UsuarioVO usuario = usuarioService.getById(usuarioId);
        if (usuario.getRol().getId() != 3) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo un administrador puede eliminar productos");
        }

        productoService.borrarPorId(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }

    @GetMapping("/productos/buscarGestion")
    public List<ProductoDTO> buscarProductosGestion(
            @RequestParam String campo,
            @RequestParam String valor) {
        return productoService.filtrarPorCampo(campo, valor);
    }

    @PostMapping("/productos/importar")
    public ResponseEntity<Void> importarProductos(@RequestParam("archivo") MultipartFile archivo) {
        productoService.importarDesdeExcel(archivo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/productos/plantilla")
    public ResponseEntity<byte[]> descargarPlantillaExcel() {
        byte[] contenido = productoService.generarPlantillaExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plantilla_productos.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(contenido);
    }

    @GetMapping("/pedidos")
    public List<PedidoListadoDTO> obtenerTodosLosPedidos() {
        return pedidoService.obtenerTodosPedidos();
    }

}
