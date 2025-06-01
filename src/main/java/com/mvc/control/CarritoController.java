package com.mvc.control;

import com.mvc.model.carrito.CarritoDTO;
import com.mvc.model.carrito.CarritoVO;
import com.mvc.model.ia.chat.ChatRequestDTO;
import com.mvc.model.ia.chat.ChatResponseDTO;
import com.mvc.model.ia.compatibilidad.CompatibilidadRequestDTO;
import com.mvc.model.ia.compatibilidad.CompatibilidadResponseDTO;
import com.mvc.service.carrito.CarritoService;
import com.mvc.service.ia.chat.IChatService;
import com.mvc.service.ia.compatibilidad.ICompatibilidadService;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ICompatibilidadService compatibilidadService;

    @Autowired
    private IChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/notificar")
    public ResponseEntity<String> notificarTest() {
        messagingTemplate.convertAndSend("/avisos/stock/usuario/30", "🔥 ¡Mensaje de prueba desde el backend!");
        return ResponseEntity.ok("Mensaje enviado");
    }

    @GetMapping("/agregar/{productoId}")
    public ResponseEntity<String> agregarProducto(
            @PathVariable Long productoId,
            @RequestHeader(value = "usuario-id", required = false) Long usuarioId) {

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falta el usuario-id en cabecera");
        }

        carritoService.insertarOSumar(usuarioId, productoId, 1);
        return ResponseEntity.ok("Producto agregado al carrito");
    }

    @GetMapping("/quitar/{productoId}")
    public ResponseEntity<String> quitarProducto(
            @PathVariable Long productoId,
            @RequestHeader(value = "usuario-id", required = false) Long usuarioId) {

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        carritoService.quitarUnidad(usuarioId, productoId);

        return ResponseEntity.ok("Producto eliminado del carrito");
    }

    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<String> eliminarProductoDelCarrito(
            @PathVariable Long productoId,
            @RequestHeader(value = "usuario-id", required = false) Long usuarioId) {

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        carritoService.eliminarPorUsuarioYProducto(usuarioId, productoId);
        return ResponseEntity.ok("Producto eliminado completamente del carrito");
    }

    @GetMapping("/usuario")
    public ResponseEntity<Map<Long, Integer>> getCarritoUsuario(
            @RequestHeader(value = "usuario-id", required = false) Long usuarioId) {

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<CarritoVO> carritoBD = carritoService.getByUsuarioId(usuarioId);

        Map<Long, Integer> carritoMap = new HashMap<>();
        for (CarritoVO item : carritoBD) {
            Long productoId = item.getProducto().getId();
            Integer cantidad = item.getCantidad();
            carritoMap.put(productoId, cantidad);
        }

        return ResponseEntity.ok(carritoMap);
    }

    @GetMapping("/limpiar")
    public ResponseEntity<String> limpiarCarritosInactivos() {
        carritoService.eliminarCarritosInactivos(2); // borra los de hace más de 2h
        return ResponseEntity.ok("Carritos inactivos eliminados correctamente");
    }

    @PostMapping("/volcar")
    public ResponseEntity<String> volcarCarritoDesdeLocal(
            @RequestBody Map<Long, Integer> carritoLocal,
            @RequestHeader(value = "usuario-id", required = false) Long usuarioId) {

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no logueado");
        }

        carritoLocal.forEach((productoId, cantidad) -> {
            if (cantidad > 0) {
                carritoService.insertarOSumar(usuarioId, productoId, cantidad);
            }
        });

        return ResponseEntity.ok("🛒 Carrito local guardado en BBDD");
    }
    

    @DeleteMapping("/vaciar")
    public ResponseEntity<String> vaciarCarritoUsuario(
            @RequestHeader(value = "usuario-id", required = false) Long usuarioId) {

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        carritoService.vaciarPorUsuario(usuarioId);
        return ResponseEntity.ok("Carrito vaciado correctamente");
    }

    @PostMapping("/comprobar")
    public CompatibilidadResponseDTO comprobarCompatibilidad(@RequestBody
    CompatibilidadRequestDTO request) {
    return compatibilidadService.comprobarCompatibilidad(request);
    }
    
    // @PostMapping("/comprobar")
    // public ResponseEntity<String> comprobarCompatibilidad(@RequestBody CompatibilidadRequestDTO request) {
    //     String respuestaFalsa = "{\n" +
    //             " \"estado\": \"incompatible\",\n" +
    //             " \"problemas\": [\n" +
    //             " \"El procesador AMD Ryzen 9 7900X utiliza socket AM5, mientras que la placa base MSI PRO Z790-A WIFI utiliza socket LGA1700.\",\n"
    //             +
    //             " \"La placa base MSI PRO Z790-A WIFI no es compatible con el procesador AMD  Ryzen 9 7900X.\"\n" +
    //             " ],\n" +
    //             " \"sugerencias\": [\n" +
    //             " 11,\n" +
    //             " 12,\n" +
    //             " 13,\n" +
    //             " 14,\n" +
    //             " 15,\n" +
    //             " 16,\n" +
    //             " 17,\n" +
    //             " 18,\n" +
    //             " 10,\n" +
    //             " 20\n" +
    //             " ]\n" +
    //             "}";
    
    
    // return ResponseEntity.ok()
    // .header("Content-Type", "application/json")
    // .body(respuestaFalsa);
    // }

    @PostMapping("/chat-compatibilidad")
    public ResponseEntity<ChatResponseDTO> procesarPregunta(@RequestBody ChatRequestDTO request) {
        ChatResponseDTO respuesta = chatService.procesarPregunta(request);
        return ResponseEntity.ok(respuesta);
    }
}
