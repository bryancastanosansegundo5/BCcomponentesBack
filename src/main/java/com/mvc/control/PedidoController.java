package com.mvc.control;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.mvc.model.enums.MetodoPago;
import com.mvc.model.enums.Transportista;
import com.mvc.model.pedido.PedidoDTO;
import com.mvc.model.pedido.PedidoListadoDTO;
import com.mvc.model.pedido.PedidoVO;
import com.mvc.model.pedido.ResumenPedidoCompletoDTO;
import com.mvc.model.pedido.ResumenPedidoDTO;
import com.mvc.model.usuario.UsuarioVO;
import com.mvc.service.pedido.PedidoService;
import com.mvc.service.usuario.UsuarioService;

@RestController
@RequestMapping("/api/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/pedidoRealizado")
    public ResponseEntity<Map<String, String>> realizarPedido(
            @RequestBody PedidoDTO pedidoDTO,
            @RequestHeader("usuario-id") Long usuarioId) {

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Debe iniciar sesión para realizar un pedido"));
        }

        pedidoDTO.setUsuarioId(usuarioId);
        String numFactura = pedidoService.insertar(pedidoDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("numFactura", numFactura));
    }

    @GetMapping("/transportistas")
    public ResponseEntity<Transportista[]> obtenerTransportistas() {
        return ResponseEntity.ok(Transportista.values());
    }

    @GetMapping("/metodos-pago")
    public ResponseEntity<MetodoPago[]> obtenerMetodosPago() {
        return ResponseEntity.ok(MetodoPago.values());
    }

    @GetMapping("/resumen/{numFactura}")
    public ResponseEntity<ResumenPedidoCompletoDTO> obtenerResumenMinimo(@PathVariable String numFactura) {
        ResumenPedidoCompletoDTO resumen = pedidoService.obtenerResumenPorFactura(numFactura);
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<PedidoListadoDTO>> getPedidosPorUsuario(@PathVariable Long id) {
        List<PedidoListadoDTO> pedidos = pedidoService.obtenerPedidosPorUsuario(id);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/factura/{numFactura}")
    public ResponseEntity<byte[]> descargarFactura(
            @PathVariable String numFactura,
            @RequestHeader("usuario-id") Long usuarioId) {

        UsuarioVO usuario = usuarioService.getById(usuarioId);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        PedidoVO pedido = pedidoService.buscarPorNumeroFactura(numFactura);
        if (pedido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada");
        }

        boolean esAdmin = usuario.getRol().getId() == 2 || usuario.getRol().getId() == 3;
        boolean esSuPedido = pedido.getUsuario().getId().equals(usuarioId);

        if (!esAdmin && !esSuPedido) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a esta factura");
        }

        // Llama al servicio pedidoService para generar la factura en formato PDF.
        // Devuelve un array de bytes (byte[]), que es el contenido del archivo PDF
        // generado.
        byte[] pdf = pedidoService.generarFacturaPdf(numFactura);
        // Crea un objeto HttpHeaders para definir cabeceras personalizadas que se
        // enviarán junto con la respuesta HTTP.
        HttpHeaders headers = new HttpHeaders();
        // Indica al navegador que el contenido que se devuelve es un archivo PDF,
        // estableciendo el Content-Type a application/pdf.
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Añade una cabecera Content-Disposition para sugerir al navegador cómo mostrar
        // el archivo:
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=factura-" + numFactura + ".pdf");

        // Crea y devuelve la respuesta HTTP completa:
        // Cuerpo (body) → el PDF (byte[]).
        // Cabeceras (headers) → las que acabas de configurar.
        // Código de estado → 200 OK.
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

    }

}
