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

import com.mvc.config.PayPalClient;
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
@RequestMapping("/api/paypal")
public class PayPalController {

    @Autowired
    private PayPalClient payPalClient;
    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/crear-pedido")
    public ResponseEntity<Map<String, String>> crearPedidoPaypal(@RequestBody Map<String, Object> body) {
        double total = Double.parseDouble(body.get("total").toString());
        String moneda = body.getOrDefault("moneda", "EUR").toString();

        String orderId = payPalClient.crearPedido(total, moneda);
        return ResponseEntity.ok(Map.of("orderID", orderId));
    }

    @PostMapping("/capturar-pedido")
    public ResponseEntity<Map<String, String>> capturarPedido(
            @RequestParam String orderID,
            @RequestBody PedidoDTO pedidoDTO,
            @RequestHeader("usuario-id") Long usuarioId) {

        boolean ok = payPalClient.capturarPedido(orderID); // lo implementamos ahora

        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Pago no capturado"));
        }

        pedidoDTO.setUsuarioId(usuarioId);
        String numFactura = pedidoService.insertar(pedidoDTO);

        return ResponseEntity.ok(Map.of("numFactura", numFactura));
    }
}

