package com.mvc.components;

import com.mvc.service.carrito.CarritoService;
import com.mvc.service.producto.ProductoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Clase encargada de ejecutar tareas programadas relacionadas con el carrito:
 * - Limpieza de carritos inactivos
 * - Verificación de demanda frente a stock
 */
@Component
public class VerificacionStockTask {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CarritoService carritoService;

    @Value("${carrito.tiempo.inactivo.horas}")
    private int horasInactividad;

    /**
     * Elimina productos en carritos cuya fecha de creación es mayor a 2 horas
     * Se ejecuta cada 30 minutos
     */
    @Scheduled(fixedRateString = "${carrito.tarea.limpieza.ms}")
    public void limpiarCarritosInactivos() {
        carritoService.eliminarCarritosInactivos(horasInactividad);
    }

    /**
     * Verifica si la demanda en carritos supera el stock disponible
     * Se ejecuta cada 30 segundos
     */
    @Scheduled(fixedRateString = "${carrito.tarea.verificacion.ms}")
    public void verificarStockEnCarritos() {
        List<Long> productoIds = carritoService.obtenerProductoIdsEnCarritos();
        for (Long productoId : productoIds) {
            productoService.verificarStockEnCarritos(productoId);
        }
    }
}
