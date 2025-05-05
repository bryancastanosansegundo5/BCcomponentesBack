package com.mvc.components;

import com.mvc.service.carrito.CarritoService;
import com.mvc.service.producto.ProductoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class VerificacionStockTask {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CarritoService carritoService;

    /**
     * Elimina productos en carritos cuya fecha de creación es mayor a 2 horas
     * Se ejecuta cada 30 minutos
     */
    @Scheduled(fixedRate = 1800000) 
    public void limpiarCarritosInactivos() {
        carritoService.eliminarCarritosInactivos(2);
    }

    /**
     * Verifica si la demanda en carritos supera el stock disponible
     * Se ejecuta cada 30 segundos
     */
    @Scheduled(fixedRate = 60000) // 30 segundos
    public void verificarStockEnCarritos() {
        List<Long> productoIds = carritoService.obtenerProductoIdsEnCarritos();

        for (Long productoId : productoIds) {
            // Metodo para comprobar y notificar
            productoService.verificarStockEnCarritos(productoId);
        }
    }
}
